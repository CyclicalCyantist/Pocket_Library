package com.example.pocket_library

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

class BookRepository(private val context: Context) {

    private val db = FirebaseFirestore.getInstance()
    private val bookDao = AppDatabase.getDatabase(context).bookDao()
    private var firestoreListener: ListenerRegistration? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun startSync() {
        // 1Listen for local changes
        scope.launch {
            bookDao.getAllBooks().collectLatest { books ->
                books.forEach { book ->
                    val docRef = db.collection("books").document(book.id.toString())
                    docRef.set(book) // Firestore serializes Book automatically
                }
            }
        }

        // Listen for remote changes
        firestoreListener = db.collection("books")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Firestore listener error: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val books = snapshot.toObjects(Book::class.java)
                    scope.launch {
                        books.forEach { book ->
                            bookDao.insert(book)
                        }
                    }
                }
            }
    }

    fun stopSync() {
        firestoreListener?.remove()
        scope.cancel()
    }
}
