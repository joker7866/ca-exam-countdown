package com.cacountdown.app.ui.topics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cacountdown.app.data.local.entity.ChapterEntity

@Composable
fun TopicsScreen(viewModel: TopicsViewModel = hiltViewModel()) {
    val chapters by viewModel.chapters.collectAsState()
    var newTopic by remember { mutableStateOf("") }
    val done = chapters.count { it.isDone }
    val progress = if (chapters.isNotEmpty()) done.toFloat() / chapters.size else 0f

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = newTopic,
                    onValueChange = { newTopic = it },
                    label = { Text("Add a topic") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                TextButton(onClick = {
                    viewModel.addTopic(newTopic)
                    newTopic = ""
                }) { Text("Add") }
            }
        },
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            item {
                Text("Topic checklist", style = MaterialTheme.typography.headlineSmall)
                Text("$done of ${chapters.size} topics done", style = MaterialTheme.typography.bodyMedium)
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
            }

            items(chapters, key = ChapterEntity::id) { chapter ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = chapter.isDone, onCheckedChange = { viewModel.toggle(chapter) })
                        Text(
                            chapter.title,
                            textDecoration = if (chapter.isDone) TextDecoration.LineThrough else TextDecoration.None,
                        )
                    }
                    IconButton(onClick = { viewModel.deleteTopic(chapter) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete topic")
                    }
                }
            }
        }
    }
}
