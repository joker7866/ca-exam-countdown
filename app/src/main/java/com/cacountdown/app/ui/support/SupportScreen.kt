package com.cacountdown.app.ui.support

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private data class Faq(val question: String, val answer: String)

private val FAQS = listOf(
    Faq(
        "Why did my countdown change by itself?",
        "Exam dates come from the official ICAI timetable. If a notice moves a paper, you'll see a Schedule update banner before anything changes — nothing updates silently.",
    ),
    Faq(
        "Does the app work without internet?",
        "Yes. Once the timetable is downloaded, countdowns, targets and reminders all work fully offline.",
    ),
    Faq(
        "How is my daily target calculated?",
        "Hours left (based on your syllabus, revision and mock-test progress) divided by study days remaining. Open a subject's Plan screen to see the exact numbers.",
    ),
    Faq(
        "I'm not getting reminders.",
        "Check Settings > battery optimisation for this app, and make sure quiet hours in Alerts aren't covering the time you expect them.",
    ),
)

@Composable
fun SupportScreen() {
    val context = LocalContext.current

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item { Text("Help & support", style = MaterialTheme.typography.headlineSmall) }

        items(FAQS) { faq ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(faq.question, style = MaterialTheme.typography.titleSmall)
                    Text(faq.answer, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        item {
            Button(
                onClick = {
                    val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                        data = android.net.Uri.parse("mailto:support@cacountdown.app")
                        putExtra(android.content.Intent.EXTRA_SUBJECT, "CA Countdown support")
                    }
                    runCatching { context.startActivity(intent) }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Email support")
            }
        }

        item {
            Text(
                "CA Countdown · version 0.1.0",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
