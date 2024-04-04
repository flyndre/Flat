package de.flyndre.flat.composables.joinscreen

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.flyndre.flat.composables.scaffolds.StandardScaffold

@Composable
fun JoinScreen(modifier: Modifier = Modifier, onNavigateToInitialScreen: () -> Unit){
    StandardScaffold(topBarText = "Join a group", onNavigateBack = onNavigateToInitialScreen) {
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Join a group with a link")
        }
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Join a group through scanning a qr code")
        }
    }
}