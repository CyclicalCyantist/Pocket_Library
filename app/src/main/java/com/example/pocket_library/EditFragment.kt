package com.example.pocket_library

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class EditFragment : Fragment() {
    private val vm: BookViewModel by activityViewModels()

    private lateinit var authorInput: EditText
    private lateinit var titleInput: EditText
    private lateinit var publicationInput: EditText
    private lateinit var saveBtn: Button
    private lateinit var cameraBtn: Button
    private lateinit var imagePreview: ImageView

    private lateinit var bookDao: BookDAO
    private var imageUri: Uri? = null
    private var bookId: String = ""

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && imageUri != null) {
                imagePreview.setImageURI(imageUri)
            } else {
                imageUri = null
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookDao = BookDatabase.getDatabase(requireContext()).bookDao()

        if (savedInstanceState != null) {
            bookId = savedInstanceState.getString("bookId", "")
            imageUri = savedInstanceState.getParcelable("imageUri")
        } else {
            val selected = vm.getSelectedItem()
            bookId = selected?.id ?: arguments?.getString("book_id") ?: UUID.randomUUID().toString()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("bookId", bookId)
        outState.putParcelable("imageUri", imageUri)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authorInput = view.findViewById(R.id.bookAuthor)
        titleInput = view.findViewById(R.id.bookTitle)
        publicationInput = view.findViewById(R.id.bookPublication)
        saveBtn = view.findViewById(R.id.btnSave)
        cameraBtn = view.findViewById(R.id.btnCamera)
        imagePreview = view.findViewById(R.id.preview)
        val backBtn = view.findViewById<ImageButton>(R.id.backBtn)

        imageUri?.let { imagePreview.setImageURI(it) }

        lifecycleScope.launch {
            val book = bookDao.getBookById(bookId)
            if (book != null) {
                withContext(Dispatchers.Main) {
                    authorInput.setText(book.author)
                    titleInput.setText(book.title)
                    publicationInput.setText(book.year.toString())
                    if (book.cover != null && imageUri == null) {
                        imageUri = Uri.parse(book.cover)
                        imagePreview.setImageURI(imageUri)
                    }
                }
            }
        }

        cameraBtn.setOnClickListener {
            val photoFile = File(requireContext().filesDir, "cover_${UUID.randomUUID()}.jpg")
            imageUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                photoFile
            )
            cameraLauncher.launch(imageUri)
        }

        saveBtn.setOnClickListener {
            val title = titleInput.text.toString()
            val author = authorInput.text.toString()
            val publish = publicationInput.text.toString().toIntOrNull() ?: 0

            if (title.isNotEmpty()) {
                lifecycleScope.launch {
                    val book = bookDao.getBookById(bookId)
                    val coverPath = imageUri?.toString()

                    if (book != null) {
                        if (book.cover != null && book.cover != coverPath) {
                            runCatching { File(Uri.parse(book.cover).path!!).delete() }
                        }

                        bookDao.insert(
                            book.copy(
                                title = title,
                                author = author,
                                year = publish,
                                cover = coverPath,
                                synced = false
                            )
                        )
                    } else {
                        bookDao.insert(
                            Book(
                                id = bookId,
                                title = title,
                                author = author,
                                year = publish,
                                cover = coverPath,
                                synced = false
                            )
                        )
                    }
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
