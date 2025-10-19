package com.example.pocket_library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListFragment : Fragment() {
    private val vm: BookViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        val searchView: SearchView = view.findViewById(R.id.searchView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                vm.searchLocal(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                vm.searchLocal(newText.orEmpty())
                return true
            }
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rv = view.findViewById<RecyclerView>(R.id.recyclerView)
        val spanCount = resources.getInteger(R.integer.book_list_span_count)
        rv.layoutManager = GridLayoutManager(requireContext(), spanCount)

        val adapter = BookAdapter(
            requireContext(),
            onItemClick = { book ->
                vm.setCurrentItem(book.id)
            },
            onButtonClick = { book ->
                vm.delete(book.id)
            }
        )
        rv.adapter = adapter

        vm.filteredBooks.observe(viewLifecycleOwner) { books ->
            adapter.submitList(books)
        }
    }
}
