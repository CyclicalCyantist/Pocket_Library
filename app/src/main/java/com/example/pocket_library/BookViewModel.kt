package com.example.pocket_library

import android.app.Application
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

// State for the network search screen
data class NetworkUiState(
    val query: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val results: List<BookDoc> = emptyList()
)

class BookViewModel(
    application: Application,
    private val bookDao: BookDAO
) : AndroidViewModel(application) {

    private val repository = BookRepository(application)

    // --- LiveData for local collection --- (ListFragment, EditFragment)
    val books: LiveData<List<Book>> = bookDao.getAllBooks().asLiveData()
    private val _filteredBooks = MediatorLiveData<List<Book>>().apply {
        addSource(books) { applyFilters() }
    }
    val filteredBooks: LiveData<List<Book>> = _filteredBooks
    private val _selectedItemId = MutableLiveData<String?>()
    val selectedItemId: LiveData<String?> = _selectedItemId
    var localSearchQuery: String = ""

    // --- StateFlow for network search --- (SearchFragment / LibraryScreen)
    private val _networkState = MutableStateFlow(NetworkUiState())
    val networkState: StateFlow<NetworkUiState> = _networkState
    private var searchJob: Job? = null

    // --- Methods for local collection --- 
    fun setCurrentItem(itemId: String) {
        _selectedItemId.value = itemId
    }

    fun clearCurrentItem() {
        _selectedItemId.value = null
    }

    fun getSelectedItem(): Book? {
        return books.value?.find { it.id == _selectedItemId.value }
    }

    fun searchLocal(query: String) {
        localSearchQuery = query
        applyFilters()
    }

    fun delete(bookId: String) {
        viewModelScope.launch {
            val book = bookDao.getBookById(bookId)
            if (book != null) {
                book.cover?.let {
                    val file = File(it.toUri().path!!)
                    if (file.exists()) file.delete()
                }
                repository.deleteFromFirestore(book)
            }
            bookDao.deleteById(bookId)
        }
    }

    private fun applyFilters() {
        val baseList = books.value.orEmpty()
        val searchFiltered = if (localSearchQuery.isBlank()) {
            baseList
        } else {
            val lowerQuery = localSearchQuery.lowercase()
            baseList.filter {
                it.title.lowercase().contains(lowerQuery) ||
                        it.author.lowercase().contains(lowerQuery)
            }
        }
        _filteredBooks.value = searchFiltered
    }

    // --- Methods for network search & adding books ---
    fun updateNetworkQuery(q: String) {
        _networkState.value = _networkState.value.copy(query = q)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            searchNetwork()
        }
    }

    fun searchNetwork() {
        val q = _networkState.value.query.trim()
        if (q.isEmpty()) {
            _networkState.value = _networkState.value.copy(
                results = emptyList(),
                error = null, loading = false
            )
            return
        }
        viewModelScope.launch {
            _networkState.value = _networkState.value.copy(loading = true, error = null)
            try {
                val validTitles = Network.api.searchBooksByTitle(title = q, perPage = 30)
                val validAuthors = Network.api.searchBooks(author = q, perPage = 30)
                val resp = LibraryResponse(
                    numFound = validAuthors.numFound + validTitles.numFound,
                    docs = (validAuthors.docs + validTitles.docs).distinctBy { it.key }
                )
                _networkState.value = _networkState.value.copy(results = resp.docs, loading = false)
            } catch (t: Throwable) {
                _networkState.value = _networkState.value.copy(
                    loading = false,
                    error = t.message ?: "Something went wrong"
                )
            }
        }
    }

    fun addBookToCollection(doc: BookDoc) {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val coverUrl = doc.coverId?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" }
            var coverUri: String? = null
            if (coverUrl != null) {
                coverUri = saveCoverImage(context, coverUrl, doc.key.replace("/", "_"))?.toString()
            }
            val newBook = Book(
                id = doc.key.replace("/", "_"),
                title = doc.title,
                author = doc.authorName?.joinToString(", ") ?: "Unknown",
                year = doc.firstPublishYear ?: 0,
                cover = coverUri
            )
            bookDao.insert(newBook)

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Added '${doc.title}' to collection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun saveCoverImage(context: Context, imageUrl: String, bookId: String): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val connection = URL(imageUrl).openConnection() as HttpURLConnection
                connection.inputStream.use { input ->
                    val file = File(context.filesDir, "${bookId}_cover.jpg")
                    file.outputStream().use { output -> input.copyTo(output) }
                    Uri.fromFile(file)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // --- Firestore Sync --- 
    fun startSync() {
        repository.startSync()
    }

    fun stopSync() {
        repository.stopSync()
    }
}