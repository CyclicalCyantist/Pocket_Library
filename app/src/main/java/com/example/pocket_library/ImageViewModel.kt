package com.example.pocket_library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UiState(
    val query: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val results: List<BookDoc> = emptyList()
)

class ImageViewModel : ViewModel() {
    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    private var searchJob: Job? = null

    fun updateQuery(q: String) {
        _state.value = _state.value.copy(query = q)
        // Simple debounce:
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            search()
        }
    }

    fun search() {
        val q = _state.value.query.trim()
        if (q.isEmpty()) {
            _state.value = _state.value.copy(results = emptyList(),
                error = null, loading = false)
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val validTitles = Network.api.searchBooksByTitle(
                    title = q,
                    //author = q,
                    perPage = 30
                )
                val validAuthors = Network.api.searchBooks(
                    author = q,
                    //title = q,
                    perPage = 30
                )
                val resp = LibraryResponse(
                    numFound =  validAuthors.numFound + validTitles.numFound,
                    docs = validAuthors.docs + validTitles.docs
                )
                _state.value = _state.value.copy(results = resp.docs, loading = false)
            } catch (t: Throwable) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = t.message ?: "Something went wrong"
                )
            }
        }
    }
}