package de.flyndre.flat.composables.initialscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun InitialScreen(
    modifier: Modifier = Modifier,
    onNavigateToJoinScreen: () -> Unit,
    onNavigateToCreateGroupScreen: () -> Unit,
    onLukasBUHtton: () -> Unit
) {
    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
            ) {
                Button(onClick = { onNavigateToJoinScreen() }) {
                    Text("Join")
                }
                Button(onClick = { onNavigateToCreateGroupScreen() }) {
                    Text("Create")
                }
                Button(onClick = {  }) {
                    Text("Lukas BUHtton")
                }
            }
        }
    ) { innerPadding ->
        modifier.padding(innerPadding)
        GoogleMap(modifier = modifier.fillMaxSize()) {
        }
    }
}