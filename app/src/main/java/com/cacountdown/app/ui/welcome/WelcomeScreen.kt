package com.cacountdown.app.ui.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cacountdown.app.ui.theme.Gold
import com.cacountdown.app.ui.theme.Navy

@Composable
fun WelcomeScreen(onGetStarted: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .clip(CircleShape)
                .background(Gold)
                .padding(24.dp),
        ) {
            Text("CA", style = MaterialTheme.typography.displaySmall, color = Navy)
        }

        androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 24.dp))

        Text(
            "CA Countdown",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
        )
        Text(
            "Your exam countdown, study target and reminders — always correct, fully offline.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp),
        )

        Button(onClick = onGetStarted, modifier = Modifier.fillMaxWidth()) {
            Text("Get started")
        }
    }
}
