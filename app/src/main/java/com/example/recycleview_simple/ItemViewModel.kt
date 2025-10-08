package com.example.recycleview_simple

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ItemViewModel(private val bookDao: BookDAO) : ViewModel() {

    // Connect to database
//    val books: LiveData<List<Book>> = bookDao.getAllBooks().asLiveData()

    // Hardcoded test data
    val books: MutableLiveData<List<Book>> = MutableLiveData<List<Book>>(listOf(
        Book(title = "Hitchhiker's Guide to the Galaxy", author = "Neil Gaiman", year = 1979),
        Book(title = "Pride & Prejudice", author = "Jane Austen", year = 1813),
        Book(title = "Of Mice and Men", author = "John Steinbeck", year = 1937),
        Book(title = "Metamorphosis", author = "Franz Kafka", year = 1915)
        )
    )

    var currentCategory: ItemCategories = ItemCategories.ALL
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

    fun filterByCategory(category: ItemCategories) {
        currentCategory = category
        applyFilters()
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