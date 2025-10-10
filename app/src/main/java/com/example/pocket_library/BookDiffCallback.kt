package com.example.pocket_library

import androidx.recyclerview.widget.DiffUtil

class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
    override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {

        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {

        return oldItem == newItem
    }
}
