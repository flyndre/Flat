package de.flyndre.flat.composables.creategroupscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(modifier: Modifier = Modifier, onNavigateToInitialScreen: () -> Unit, createGroupScreenViewModel: CreateGroupScreenViewModel = CreateGroupScreenViewModel()){
    val createGroupScreenState by createGroupScreenViewModel.createGroupState.collectAsState()
    Scaffold(topBar = {
        TopAppBar(title = {Text(text = "Presets")},
            navigationIcon = {
               IconButton(onClick = { onNavigateToInitialScreen() }) {
                   Icon(
                       imageVector = Icons.Filled.ArrowBack,
                       contentDescription = "back to start screen"
                   )
               }
        })
    }) {
        innerPadding ->
        PresetList(Modifier.padding(innerPadding), presets = createGroupScreenState.presets)
    }
}

@Composable
private fun PresetList(modifier: Modifier, presets: List<String>){
    Column(modifier = modifier) {
        presets.forEach{
            preset -> Text(text = preset)
        }
    }
}