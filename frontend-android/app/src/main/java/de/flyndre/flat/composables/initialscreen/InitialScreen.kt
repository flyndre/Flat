package de.flyndre.flat.composables.initialscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings

@Composable
fun InitialScreen(
    modifier: Modifier = Modifier,
    onNavigateToJoinScreen: () -> Unit,
    onNavigateToCreateGroupScreen: () -> Unit,
    onNavigateToSettingScreen: () -> Unit
) {
    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                ExtendedFloatingActionButton(onClick = { onNavigateToJoinScreen() }, icon = {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "join existing collection"
                    )
                }, text = {
                    Text(
                        text = "Join"
                    )
                }, modifier = Modifier.padding(10.dp))
                ExtendedFloatingActionButton(onClick = { onNavigateToCreateGroupScreen() }, icon = {
                    Icon(
                        imageVector = Icons.Filled.Create,
                        contentDescription = "create new collection"
                    )
                }, text = {
                    Text(
                        text = "Create"
                    )
                }, modifier = Modifier.padding(10.dp))
                FloatingActionButton(onClick = onNavigateToSettingScreen, modifier = Modifier.padding(10.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "navigate to setting screen"
                    )

                }
            }
        }
    ) { innerPadding ->
        modifier.padding(innerPadding)
        GoogleMap(
            modifier = modifier.fillMaxSize(),
            uiSettings = MapUiSettings(zoomControlsEnabled = false)
        ) {
        }
    }
}
