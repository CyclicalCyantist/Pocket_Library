package com.example.recycleview_simple

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class DetailFragment : Fragment() {
    private val vm: ItemViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val linearLayout = view.findViewById<LinearLayout>(R.id.mainLayout)
        linearLayout.orientation = LinearLayout.HORIZONTAL

        val imageView = view.findViewById<ImageView>(R.id.itemImage)
        val itemName = view.findViewById<TextView>(R.id.nameTextView)
        val desc = view.findViewById<TextView>(R.id.detailedDescription)
        val favBtn = view.findViewById<ImageButton>(R.id.btnFav)
        val backBtn = view.findViewById<ImageButton>(R.id.backBtn)
        /*
        vm.selectedItemId.observe(viewLifecycleOwner) {
            vm.getSelectedItem()?.let { item ->
                imageView.setImageResource(item.imageBackground)
                itemName.text = item.name
                desc.text = item.description
                favBtn.setImageResource(
                    if (item.isFavourite) R.drawable.one_star_icon2
                    else R.drawable.one_star_outline_icon2
                )
            }
        }


        favBtn.setOnClickListener {
            vm.getSelectedItem()?.let { item ->
                vm.toggleFav(item)
            }
            vm.getSelectedItem()?.let { item ->
                favBtn.setImageResource(
                    if (item.isFavourite) R.drawable.one_star_icon2
                    else R.drawable.one_star_outline_icon2
                )
            }
        }*/

        backBtn.setOnClickListener {
            vm.clearCurrentItem()
            parentFragmentManager.popBackStack()
        }
    }
}
