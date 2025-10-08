package com.example.recycleview_simple

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private val vm: ItemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        findViewById<View?>(R.id.root)?.let { root ->
            ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
                val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(sb.left, sb.top, sb.right, sb.bottom)
                insets
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.search_container, SearchFragment())
                .replace(R.id.list_container, ListFragment())
                .commit()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val rightPane = findViewById<View?>(R.id.rightPane)
                val hasSelection = vm.getSelectedItem() != null

                if (rightPane == null && hasSelection) {
                    vm.clearCurrentItem()
                    render()
                    return
                }

                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        })

        vm.selectedItemId.observe(this) { render() }
        render()
    }

    private fun render() {
        val rightPane = findViewById<View?>(R.id.rightPane)
        val search = findViewById<View?>(R.id.search_container)
        val hasSelection = vm.getSelectedItem() != null

        if (rightPane != null) {
            search?.visibility = View.VISIBLE
            replaceIfNeeded(R.id.list_container, ListFragment::class.java)

            if (hasSelection) {
                replaceIfNeeded(R.id.rightPane, DetailFragment::class.java)
            } else {
                supportFragmentManager.findFragmentById(R.id.rightPane)?.let { f ->
                    supportFragmentManager.beginTransaction().remove(f).commit()
                }
            }
        } else {
            if (hasSelection) {
                search?.visibility = View.GONE
                replaceIfNeeded(R.id.list_container, DetailFragment::class.java, addToBackStack = false)
            } else {
                search?.visibility = View.VISIBLE
                replaceIfNeeded(R.id.list_container, ListFragment::class.java, addToBackStack = false)
            }
        }
    }

    private fun replaceIfNeeded(
        containerId: Int,
        fragmentClass: Class<out androidx.fragment.app.Fragment>,
        addToBackStack: Boolean = false
    ) {
        val fm = supportFragmentManager
        val current = fm.findFragmentById(containerId)
        if (current == null || current::class.java != fragmentClass) {
            val tx = fm.beginTransaction().replace(containerId, fragmentClass, null)
            if (addToBackStack) tx.addToBackStack(null)
            tx.commit()
        }
    }
}
