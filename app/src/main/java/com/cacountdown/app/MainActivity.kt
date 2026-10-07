package com.cacountdown.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.cacountdown.app.ui.navigation.CaDestination
import com.cacountdown.app.ui.navigation.CaNavHost
import com.cacountdown.app.ui.theme.CaCountdownTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val hasSetup by viewModel.hasCompletedSetup.collectAsState()
            CaCountdownTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    hasSetup?.let { completed ->
                        CaNavHost(
                            startDestination = if (completed) CaDestination.Home.route else CaDestination.Setup.route,
                        )
                    }
                }
            }
        }
    }
}
