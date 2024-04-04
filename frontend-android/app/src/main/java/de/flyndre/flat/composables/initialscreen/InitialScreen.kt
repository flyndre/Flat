package de.flyndre.flat.composables.initialscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun InitialScreen(modifier: Modifier = Modifier, onNavigateToJoinScreen: () -> Unit, onNavigateToCreateGroupScreen: () -> Unit){
    Column(modifier = modifier){
        Button(modifier = modifier, onClick = onNavigateToJoinScreen){
            Text(text = "Join a group")
        }
        Button(modifier = modifier, onClick = onNavigateToCreateGroupScreen){
            Text(text = "Create a group")
        }
    }
}