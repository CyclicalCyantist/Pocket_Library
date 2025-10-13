package com.example.pocket_library

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    private val vm: BookViewModel by viewModels {
        BookViewModelFactory(application,AppDatabase.getDatabase(this).bookDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialise Firebase
        FirebaseApp.initializeApp(this)

        // Inset spacing
        findViewById<View?>(R.id.root)?.let { root ->
            ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
                val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(sb.left, sb.top, sb.right, sb.bottom)
                insets
            }
        }

        // Load initial list fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.list_container, ListFragment())
                .commit()
        }

        // Menu navigation
        val collectionButton = findViewById<Button>(R.id.collection_button)
        val libraryButton = findViewById<Button>(R.id.library_button)

        collectionButton.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.list_container, ListFragment())
                .commit()
        }

        libraryButton.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.list_container, SearchFragment())
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
        val hasSelection = vm.getSelectedItem() != null

        if (rightPane != null) {
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
                replaceIfNeeded(R.id.list_container, DetailFragment::class.java, addToBackStack = false)
            } else {
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

    override fun onStart() {
        super.onStart()
        vm.startSync()
    }

    override fun onStop() {
        super.onStop()
        vm.stopSync()
    }

}
