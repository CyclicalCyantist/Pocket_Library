package com.example.recycleview_simple

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListFragment : Fragment() {
    private val vm: ItemViewModel by activityViewModels()
    private lateinit var adapter: ItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv = view.findViewById<RecyclerView>(R.id.recyclerView)
        rv.layoutManager = GridLayoutManager(requireContext(), 1)

        adapter = ItemAdapter(
            view.context,
            onItemClick = { item ->
                vm.setCurrentItem(item.id)
            },
            onFavouriteClick = { vm.toggleFav(it) }
        )
        rv.adapter = adapter

        vm.filteredItems.observe(viewLifecycleOwner) { adapter.submitList(it) }
    }
}
