package com.example.pocket_library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BookViewModel(private val bookDao: BookDAO) : ViewModel() {

    // Connect to database
//    val books: LiveData<List<Book>> = bookDao.getAllBooks().asLiveData()

    // Hardcoded test data
    val books: MutableLiveData<List<Book>> = MutableLiveData<List<Book>>(listOf(
        Book(title = "Hitchhiker's Guide to the Galaxy", author = "Neil Gaiman", year = 1979),
        Book(title = "Pride & Prejudice", author = "Jane Austen", year = 1813),
        Book(title = "Of Mice and Men", author = "John Steinbeck", year = 1937),
        Book(title = "Metamorphosis", author = "Franz Kafka", year = 1915),
        Book(title = "Babel", author = "R.F. Kuang", year = 2022),
        Book(title = "Atomic Habits", author = "James Clear", year = 2018)
        )
    )

    var currentQuery: String = ""

    private val _filteredBooks = MediatorLiveData<List<Book>>().apply {
        addSource(books) { applyFilters() }
    }
    val filteredBooks: LiveData<List<Book>> = _filteredBooks

    private val _selectedItemId = MutableLiveData<Long?>()
    val selectedItemId: LiveData<Long?> = _selectedItemId

    fun setCurrentItem(itemId: Long) {
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
}