package de.flyndre.flat.composables.trackingscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(modifier: Modifier = Modifier, trackingScreenViewModel: TrackingScreenViewModel, onNavigateToInitialScreen: () -> Unit){
    Scaffold (topBar = {
        TopAppBar(title = { Text(text = "") }, navigationIcon = {
            IconButton(onClick = { onNavigateToInitialScreen() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "back to start screen"
                )
            }
        })
    }, bottomBar = {
        BottomAppBar() {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
            ) {
                Button(onClick = {  }) {
                    Text("Test")
                }
            }
        }
    }) {
        innerPadding ->
        modifier.padding(innerPadding)
    }
}