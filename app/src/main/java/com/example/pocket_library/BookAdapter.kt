package com.example.pocket_library

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import coil.load
import java.io.File
import androidx.core.net.toUri

class BookAdapter(
    private val context: Context,
    private val onItemClick: (Book) -> Unit,
    private val onButtonClick: (Book) -> Unit
) : ListAdapter<Book, BookAdapter.ItemViewHolder>(BookDiffCallback()) {
    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textTitle: TextView = view.findViewById(R.id.textName)
        val textAuthor: TextView = view.findViewById(R.id.textCategory)
        val textYear: TextView = view.findViewById(R.id.textYear)
        val itemImage: ImageView = view.findViewById(R.id.itemImage)
        val btnFav: ImageButton = view.findViewById(R.id.btnFav)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val book = getItem(position)

        holder.textTitle.text = book.title
        holder.textAuthor.text = book.author
        holder.textYear.text = book.year.toString()

        // Load local cover if it exists, otherwise fallback to placeholder
        holder.itemImage.load(book.cover?.toUri()) {
            crossfade(true)
            placeholder(R.mipmap.ic_logo)
            error(R.mipmap.ic_logo)
        }

        holder.itemView.setOnClickListener {
            onItemClick(book)
        }

        holder.btnFav.setOnClickListener {
            onButtonClick(book)
        }
    }
}