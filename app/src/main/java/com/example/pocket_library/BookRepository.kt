package com.example.pocket_library

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

class BookRepository(private val context: Context) {

    private val db = FirebaseFirestore.getInstance()
    private val bookDao = BookDatabase.getDatabase(context).bookDao()
    private var firestoreListener: ListenerRegistration? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun startSync() {
        // Listen for local changes
//        scope.launch {
//            bookDao.getAllBooks().collectLatest { books ->
//                books.forEach { book ->
//                    if (!book.synced) {
//                        val docRef = db.collection("books").document(book.id)
//                        val syncedBook = book.copy(synced = true)
//                        docRef.set(syncedBook) // Firestore serializes Book automatically
//                        println("$syncedBook.title uploaded to firebase")
//                        bookDao.insert(syncedBook)
//                    }
//                }
//            }
//        }

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
                            val inCollection = bookDao.getBookById(book.id)
                            if (inCollection == null) {
                                bookDao.insert(book)
                            }
                        }
                    }
                }
            }
    }

    fun stopSync() {
        firestoreListener?.remove()
        scope.cancel()
    }

    fun deleteFromFirestore(book: Book) {
        try {
            db.collection("books")
                .document(book.id)
                .delete()
                .addOnSuccessListener {
                    println("Deleted ${book.title} from Firestore.")
                }
                .addOnFailureListener { e ->
                    println("Error deleting ${book.title}: ${e.message}")
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
