package com.cacountdown.app.ui.analysis

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cacountdown.app.domain.model.UrgencyStatus
import com.cacountdown.app.ui.common.BarChart
import com.cacountdown.app.ui.common.BarChartEntry
import com.cacountdown.app.ui.theme.AttentionAmber
import com.cacountdown.app.ui.theme.BehindOrange
import com.cacountdown.app.ui.theme.Gold
import com.cacountdown.app.ui.theme.Navy
import com.cacountdown.app.ui.theme.OnTrackGreen
import com.cacountdown.app.ui.theme.UrgentRed

@Composable
fun AnalysisScreen(viewModel: AnalysisViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item { Text("Analysis", style = MaterialTheme.typography.headlineSmall) }

        if (state.rows.isEmpty()) {
            item { Text("No subjects yet — finish setup to see your analysis.") }
            return@LazyColumn
        }

        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Syllabus % complete, by subject", style = MaterialTheme.typography.titleSmall)
                    BarChart(
                        entries = state.rows.map {
                            BarChartEntry(
                                label = shortName(it.exam.subjectName),
                                value = (it.progress.syllabusFraction * 100).toFloat(),
                                color = Gold,
                                valueLabel = "${(it.progress.syllabusFraction * 100).toInt()}%",
                            )
                        },
                        maxValue = 100f,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                }
            }
        }

        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Revision % complete, by subject", style = MaterialTheme.typography.titleSmall)
                    BarChart(
                        entries = state.rows.map {
                            BarChartEntry(
                                label = shortName(it.exam.subjectName),
                                value = (it.progress.revisionFraction * 100).toFloat(),
                                color = Navy,
                                valueLabel = "${(it.progress.revisionFraction * 100).toInt()}%",
                            )
                        },
                        maxValue = 100f,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                }
            }
        }

        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Urgency breakdown", style = MaterialTheme.typography.titleSmall)
                    val counts = state.urgencyCounts
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        UrgencyLegendItem("On track", counts[UrgencyStatus.ON_TRACK] ?: 0, OnTrackGreen)
                        UrgencyLegendItem("Attention", counts[UrgencyStatus.ATTENTION] ?: 0, AttentionAmber)
                        UrgencyLegendItem("Behind", counts[UrgencyStatus.BEHIND] ?: 0, BehindOrange)
                        UrgencyLegendItem("Urgent", counts[UrgencyStatus.URGENT] ?: 0, UrgentRed)
                    }
                }
            }
        }
    }
}

@Composable
private fun UrgencyLegendItem(label: String, count: Int, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count.toString(), style = MaterialTheme.typography.headlineSmall, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

private fun shortName(name: String): String = name.split(" ").first().take(8)
