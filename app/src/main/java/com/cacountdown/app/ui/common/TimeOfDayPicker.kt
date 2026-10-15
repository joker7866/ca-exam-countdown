package com.cacountdown.app.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalTime

/** A lightweight +/- stepper for picking a time of day, avoiding the experimental Material3 TimePicker API. */
@Composable
fun TimeOfDayPicker(
    time: LocalTime,
    onChange: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Stepper(label = "Hour", value = time.hour, range = 0..23) { onChange(time.withHour(it)) }
        Text(":", style = MaterialTheme.typography.titleLarge)
        Stepper(label = "Min", value = time.minute, range = 0..59, step = 5) { onChange(time.withMinute(it)) }
    }
}

@Composable
private fun Stepper(label: String, value: Int, range: IntRange, step: Int = 1, onChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {
            val next = value - step
            onChange(if (next < range.first) range.last else next)
        }) { Icon(Icons.Filled.Remove, contentDescription = "Decrease $label") }
        Text("%02d".format(value), style = MaterialTheme.typography.titleMedium)
        IconButton(onClick = {
            val next = value + step
            onChange(if (next > range.last) range.first else next)
        }) { Icon(Icons.Filled.Add, contentDescription = "Increase $label") }
    }
}
