package de.flyndre.flat.composables.joinscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinScreen(modifier: Modifier = Modifier, onNavigateToInitialScreen: () -> Unit, joinScreenViewModel: JoinScreenViewModel) {
    val joinLink by joinScreenViewModel.joinLink.collectAsState()
    val joinName by joinScreenViewModel.joinName.collectAsState()
    var joiningAllowed by remember { mutableStateOf(false) }
    if(!joinLink.equals("") && !joinName.equals("")){
        joiningAllowed = true
    }else{
        joiningAllowed = false
    }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "") }, navigationIcon = {
                IconButton(onClick = { onNavigateToInitialScreen() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back to start screen"
                    )
                }
            })
        }) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding)) {
            val modifier = Modifier.padding(10.dp)
            TextField(modifier = modifier, value = joinLink, onValueChange = {joinScreenViewModel.updateJoinLink(it)}, label = {Text(text = "Link for joining")})
            TextField(modifier = modifier, value = joinName, onValueChange = {joinScreenViewModel.updateJoinName(it)}, label = {Text(text = "Name for joining")})
            Button(onClick = { /*TODO*/ }, enabled = joiningAllowed) {
                Text(text = "Join")
            }
        }
    }
}