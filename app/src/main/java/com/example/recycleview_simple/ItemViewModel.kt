package com.example.recycleview_simple

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ItemViewModel : ViewModel() {
    var currentCategory: ItemCategories = ItemCategories.ALL
    var currentQuery: String = ""

    private val _items = MutableLiveData<List<Item>>(
        listOf(
            Item("1", "Caldo Verde", ItemCategories.SIDE, "Portuguese green soup", imageSrc = R.drawable.caldo, imageBackground = R.drawable.caldo_big),
            Item("2", "Salisbury Steak", ItemCategories.MAIN, "Fake steak", imageSrc = R.drawable.salisbury, imageBackground = R.drawable.salisbury_big),
            Item("3", "Pho", ItemCategories.MAIN, "Beef noodle soup", imageSrc = R.drawable.pho, imageBackground = R.drawable.pho_big),
            Item("4", "Tuna Mornay", ItemCategories.MAIN, "Tuna casserole", imageSrc = R.drawable.mornay, imageBackground = R.drawable.mornay_big),
            Item("5", "Spaghetti Bolognese", ItemCategories.MAIN,"Meat, spaghetti and tomato based sauce", imageSrc = R.drawable.spaghetti, imageBackground = R.drawable.spaghetti_big),
            Item("6", "Ice Cream", ItemCategories.DESSERT,"Frozen milk dessert treat", imageSrc = R.drawable.icecream, imageBackground = R.drawable.icecream_big),
            Item("7","Bacon and Waffles", ItemCategories.BREAKFAST, "Waffles loaded with bacon and maple syrup", imageSrc = R.drawable.waffles, imageBackground = R.drawable.waffles_big),
            Item("8", "Water", ItemCategories.DRINK, "Glass of water", imageSrc = R.drawable.water, imageBackground = R.drawable.water_big),
            Item("9", "Arancini", ItemCategories.APPETISER, "Deep fried Italian rice balls", imageSrc = R.drawable.arancini, imageBackground = R.drawable.arancini_big),
            Item("10","Toast", ItemCategories.SNACK, "Toasted bread", imageSrc = R.drawable.toast, imageBackground = R.drawable.toast_big)
        )
    )

    val items: LiveData<List<Item>> = _items

    private val _filteredItems = MutableLiveData<List<Item>>(_items.value)
    val filteredItems: LiveData<List<Item>> = _filteredItems

    private val _selectedItemId = MutableLiveData<String?>()
    val selectedItemId: LiveData<String?> = _selectedItemId

    fun setCurrentItem(itemId: String) {
        _selectedItemId.value = itemId
    }

    fun clearCurrentItem() {
        _selectedItemId.value = null
    }

    fun getSelectedItem(): Item? {
        return _items.value?.find { it.id == _selectedItemId.value }
    }

    fun filterByCategory(category: ItemCategories) {
        currentCategory = category
        applyFilters()
    }

    fun toggleFav(item: Item) {
        val updatedList = _items.value.orEmpty().map {
            if (it.id == item.id) it.copy(isFavourite = !it.isFavourite) else it
        }
        _items.value = updatedList
        applyFilters()
    }

    fun search(query: String) {
        currentQuery = query
        applyFilters()
    }

    fun applyFilters(){
        val baseList = _items.value.orEmpty()

        val categoryFiltered = if (currentCategory == ItemCategories.ALL) {
            baseList
        } else if (currentCategory == ItemCategories.FAVOURITES) {
            baseList.filter { it.isFavourite }
        } else {
            baseList.filter { it.category == currentCategory }
        }

        val searchFiltered = if (currentQuery.isBlank()) {
            categoryFiltered
        } else {
            val lowerQuery = currentQuery.lowercase()
            categoryFiltered.filter {
                it.name.lowercase().contains(lowerQuery) ||
                        it.description.lowercase().contains(lowerQuery)
            }
        }

        _filteredItems.value = searchFiltered
    }
}