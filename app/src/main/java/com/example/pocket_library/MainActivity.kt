package com.example.pocket_library

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp

private const val CURRENT_SCREEN_KEY = "CURRENT_SCREEN_KEY"

class MainActivity : AppCompatActivity() {
    private val vm: BookViewModel by viewModels {
        BookViewModelFactory(application,BookDatabase.getDatabase(this).bookDao())
    }

    private var currentMainScreen = "list"
    private var currentScreen = "list"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            currentScreen = savedInstanceState.getString(CURRENT_SCREEN_KEY, "list")
        }

        FirebaseApp.initializeApp(this)

        findViewById<View?>(R.id.root)?.let { root ->
            ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
                val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(sb.left, sb.top, sb.right, sb.bottom)
                insets
            }
        }

        val collectionButton = findViewById<Button>(R.id.collection_button)
        val libraryButton = findViewById<Button>(R.id.library_button)
        val addButton = findViewById<Button>(R.id.add_button)
        val shareButton = findViewById<Button>(R.id.share_button)

        collectionButton.setOnClickListener {
            currentScreen = "list"
            render()
        }

        libraryButton.setOnClickListener {
            currentScreen = "search"
            render()
        }

        addButton?.setOnClickListener {
            showEditScreen()
        }

        shareButton?.setOnClickListener {
            val selectedItem = vm.getSelectedItem()
            if (selectedItem != null) {
                val bookSummary = "Check out this book: ${selectedItem.title} by ${selectedItem.author} in ${selectedItem.year}"
                shareToContacts(bookSummary)
            } else {
                Toast.makeText(this, "Please select a book to share", Toast.LENGTH_SHORT).show()
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val rightPane = findViewById<View?>(R.id.rightPane)
                val hasSelection = vm.getSelectedItem() != null

                if (rightPane == null && hasSelection) {
                    vm.clearCurrentItem()
                    return
                }

                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        vm.selectedItemId.observe(this) { render() }
        render()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CURRENT_SCREEN_KEY, currentScreen)
    }

    private fun showEditScreen() {
        replaceIfNeeded(R.id.list_container, EditFragment::class.java)
    }

    private fun render() {
        val rightPane = findViewById<View?>(R.id.rightPane)
        val collectionButton = findViewById<Button>(R.id.collection_button)
        val libraryButton = findViewById<Button>(R.id.library_button)

        val hasSelection = vm.getSelectedItem() != null

        if (currentScreen != "edit") {
            currentMainScreen = currentScreen
        }


        val mainFragmentClass = if (currentMainScreen == "search") {
            SearchFragment::class.java
        } else {
            ListFragment::class.java
        }

        if (rightPane?.visibility == View.VISIBLE) {
            replaceIfNeeded(R.id.list_container, ListFragment::class.java)
            replaceIfNeeded(R.id.rightPane, SearchFragment::class.java)
            libraryButton.visibility = View.GONE
            collectionButton.visibility = View.GONE

            if (hasSelection) {
                replaceIfNeeded(R.id.list_container, EditFragment::class.java)
            }

        } else {
            libraryButton.visibility = View.VISIBLE
            collectionButton.visibility = View.VISIBLE
            if (hasSelection) {
                replaceIfNeeded(R.id.list_container, EditFragment::class.java)
            } else {
                replaceIfNeeded(R.id.list_container, mainFragmentClass)
            }

        }
    }

    private fun replaceIfNeeded(
        containerId: Int,
        fragmentClass: Class<out androidx.fragment.app.Fragment>,
    ) {
        val fm = supportFragmentManager
        val current = fm.findFragmentById(containerId)
        if (current == null || current::class.java != fragmentClass) {
            fm.beginTransaction()
                .replace(containerId, fragmentClass, null)
                .commit()
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

    private fun shareToContacts(bookSummary: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, bookSummary)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

}
