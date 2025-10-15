package com.example.pocket_library
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

suspend fun downloadCoverImage(context: Context, coverUrl: String, bookId: String): String? {
    return try {
        val file = File(context.filesDir, "covers/${bookId}.jpg")
        file.parentFile?.mkdirs()

        val url = URL(coverUrl)
        url.openStream().use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        file.absolutePath // return path to save in Room
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
@Preview
fun LibrarySearchScreen(vm: ImageViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    val context = LocalContext.current
    val bookDao = BookDatabase.getDatabase(context).bookDao()
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = state.query,
            onValueChange = vm::updateQuery,
            label = { Text("Search Library") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardActions = KeyboardActions(
                onSearch = { vm.search() })
        )

        Spacer(Modifier.height(12.dp))

        Box(Modifier.fillMaxSize()) {
            when {
                state.loading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                state.error != null -> {
                    Text(
                        text = state.error ?: "Error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.results.isEmpty() && state.query.isNotEmpty() -> {
                    Text("No results", modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(state.results) { doc ->
                            val coverUrl = if (doc.coverId != null) {
                                "https://covers.openlibrary.org/b/id/${doc.coverId}-M.jpg"
                            } else {
                                null // Coil will show a placeholder/error if the URL is null
                            }

                            Card {
                                Row(
                                    modifier = Modifier.height(240.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = coverUrl,
                                        contentDescription = doc.title,
                                        contentScale = ContentScale.FillBounds,
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .weight(0.4f)
                                    )

                                    Column(
                                        modifier = Modifier
                                            .weight(0.6f)
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = doc.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            maxLines = 3
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        doc.firstPublishYear?.let { year ->
                                            Text(
                                                text = "Published: $year",
                                                style = MaterialTheme.typography.bodyMedium,
                                                maxLines = 3
                                            )

                                        }
                                        doc.authorName?.let { authors ->
                                            Text(
                                                text = "Author(s): ${authors.joinToString(", ")}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                maxLines = 3
                                            )
                                        }

                                        Spacer(Modifier.height(4.dp))

                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    val coverUrl = doc.coverId?.let { "https://covers.openlibrary.org/b/id/${it}-M.jpg" }
                                                    var localPath: String? = null

                                                    if (coverUrl != null) {
                                                        localPath = downloadCoverImage(context, coverUrl, doc.key.replace("/", "_"))
                                                    }

                                                    bookDao.insert(
                                                        Book(
                                                            id = doc.key.replace("/", "_"),
                                                            title = doc.title,
                                                            author = doc.authorName?.joinToString(", ") ?: "Unknown",
                                                            year = doc.firstPublishYear ?: 0,
                                                            cover = localPath
                                                        )
                                                    )

                                                    withContext(Dispatchers.Main) {
                                                        Toast.makeText(
                                                            context,
                                                            "Added '${doc.title}' to collection",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                            }
                                        ) {
                                            Text("Add to Collection")
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}