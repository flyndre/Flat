package de.flyndre.flat.composables.creategroupscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

@Composable
fun CreateGroupScreen(modifier: Modifier = Modifier, onNavigateToInitialScreen: () -> Unit, createGroupScreenViewModel: CreateGroupScreenViewModel = CreateGroupScreenViewModel()){
    val createGroupScreenState by createGroupScreenViewModel.createGroupState.collectAsState()
    PresetList(presets = createGroupScreenState.presets)
}

@Composable
private fun PresetList(presets: List<String>){
    Column {
        presets.forEach{
            preset -> Text(text = preset)
        }
    }
}