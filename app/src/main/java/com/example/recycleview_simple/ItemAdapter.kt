package com.example.recycleview_simple

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter

class ItemAdapter(
    private val context: Context,
    private val onFavouriteClick: (Item) -> Unit,
    private val onItemClick: (Item) -> Unit
) : ListAdapter<Item, ItemAdapter.ItemViewHolder>(ItemDiffCallback()) {
    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textName: TextView = view.findViewById(R.id.textName)
        val textCategory: TextView = view.findViewById(R.id.textCategory)
        val itemImage: ImageView = view.findViewById(R.id.itemImage)
        val btnFav: ImageButton = view.findViewById(R.id.btnFav)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)

        holder.textName.text = item.name
        holder.textCategory.text = item.category.toString()
        holder.itemImage.setImageResource(item.imageSrc)

        if (item.isFavourite) {
            holder.btnFav.setImageResource(R.drawable.one_star_icon)
        } else {
            holder.btnFav.setImageResource(R.drawable.one_star_outline_icon)
        }

        holder.btnFav.setOnClickListener {
            onFavouriteClick(item)
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }
}