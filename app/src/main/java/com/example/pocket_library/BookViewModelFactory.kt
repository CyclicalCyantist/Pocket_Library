package com.example.pocket_library

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BookViewModelFactory(
    private val application: Application,
    private val bookDao: BookDAO) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookViewModel::class.java)) {
            return BookViewModel(application, bookDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}