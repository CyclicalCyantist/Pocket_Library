package com.example.network_call
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.max

@Composable
@Preview
fun LibrarySearchScreen(vm: ImageViewModel = viewModel()) {
    val state by vm.state.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = state.query,
            onValueChange = vm::updateQuery,
            label = { Text("Search Library") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(
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