package com.example.pocket_library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.UUID


class EditFragment : Fragment() {
    private val vm: BookViewModel by activityViewModels()

    private lateinit var authorInput: EditText
    private lateinit var titleInput: EditText
    private lateinit var publicationInput: EditText
    private lateinit var saveBtn: Button

    private lateinit var db: BookDatabase
    private lateinit var bookDao: BookDAO

    private var bookId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = BookDatabase.getDatabase(requireContext())
        bookDao = db.bookDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_edit, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val linearLayout = view.findViewById<LinearLayout>(R.id.mainLayout)
        linearLayout.orientation = LinearLayout.HORIZONTAL

        authorInput = view.findViewById<EditText>(R.id.bookAuthor)
        titleInput = view.findViewById<EditText>(R.id.bookTitle)
        publicationInput = view.findViewById<EditText>(R.id.bookPublication)
        saveBtn = view.findViewById<Button>(R.id.btnSave)

        val backBtn = view.findViewById<ImageButton>(R.id.backBtn)

        val selected = vm.getSelectedItem()
        val bookId = selected?.id ?: arguments?.getString("book_id") ?: UUID.randomUUID().toString()

        lifecycleScope.launch {
            val book = bookDao.getBookById(bookId)
            if (book != null) {
                authorInput.setText(book.author)
                titleInput.setText(book.title)
                publicationInput.setText(book.year.toString())
            }
        }

        saveBtn.setOnClickListener {
            val author = authorInput.text.toString()
            val title = titleInput.text.toString()
            val publish = publicationInput.text.toString().toInt()
            if (title.isNotEmpty()) {
                val book = Book(bookId, title, author, publish, synced = false)
                lifecycleScope.launch {
                    bookDao.insert(book)
                }

            }
            vm.clearCurrentItem()
            parentFragmentManager.popBackStack()
        }

        backBtn.setOnClickListener {
            vm.clearCurrentItem()
            parentFragmentManager.popBackStack()
        }
    }
}
