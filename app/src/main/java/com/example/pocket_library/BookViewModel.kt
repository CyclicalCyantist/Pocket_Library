package com.example.pocket_library

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.content.Context
import java.io.File

class BookViewModel(
    application: Application,
    private val bookDao: BookDAO) : AndroidViewModel(application) {

    private val context = getApplication<Application>()
    private val repository = BookRepository(application)

    // Connect to database
   val books: LiveData<List<Book>> = bookDao.getAllBooks().asLiveData()

    // Hardcoded test data
//    val books: MutableLiveData<List<Book>> = MutableLiveData<List<Book>>(listOf(
//        Book(title = "Hitchhiker's Guide to the Galaxy", author = "Neil Gaiman", year = 1979),
//        Book(title = "Pride & Prejudice", author = "Jane Austen", year = 1813),
//        Book(title = "Of Mice and Men", author = "John Steinbeck", year = 1937),
//        Book(title = "Metamorphosis", author = "Franz Kafka", year = 1915),
//        Book(title = "Babel", author = "R.F. Kuang", year = 2022),
//        Book(title = "Atomic Habits", author = "James Clear", year = 2018)
//        )
//    )

    var currentQuery: String = ""

    private val _filteredBooks = MediatorLiveData<List<Book>>().apply {
        addSource(books) { applyFilters() }
    }
    val filteredBooks: LiveData<List<Book>> = _filteredBooks

    private val _selectedItemId = MutableLiveData<String?>()
    val selectedItemId: LiveData<String?> = _selectedItemId

    fun setCurrentItem(itemId: String) {
        _selectedItemId.value = itemId
    }

    fun clearCurrentItem() {
        _selectedItemId.value = null
    }

    fun getSelectedItem(): Book? {
        return books.value?.find { it.id == _selectedItemId.value }
    }

    fun search(query: String) {
        currentQuery = query
        applyFilters()
    }

    fun delete(bookId: String) {
        viewModelScope.launch {
            val book = bookDao.getBookById(bookId)


            if (book != null){
                // Delete local cover image
                val coverFile = File(context.filesDir, "covers/${book.id}.jpg")
                if (coverFile.exists()) {
                    coverFile.delete()
                }

                // Delete from firestore
                repository.deleteFromFirestore(book)
            }
            bookDao.deleteById(bookId)
        }
    }

    fun applyFilters(){
        val baseList = books.value.orEmpty()

        val searchFiltered = if (currentQuery.isBlank()) {
            baseList
        } else {
            val lowerQuery = currentQuery.lowercase()
            baseList.filter {
                it.title.lowercase().contains(lowerQuery) ||
                        it.author.lowercase().contains(lowerQuery)
            }
        }

        _filteredBooks.value = searchFiltered
    }

    fun startSync() {
        repository.startSync()
    }

    fun stopSync() {
        repository.stopSync()
    }
}