package de.flyndre.flat.composables.creategroupscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import de.flyndre.flat.database.AppDatabase
import de.flyndre.flat.database.entities.Preset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(modifier: Modifier = Modifier, db: AppDatabase, onNavigateToInitialScreen: () -> Unit, onNavigateToNewPresetScreen: () -> Unit, navController: NavController, createGroupScreenViewModel: CreateGroupScreenViewModel = CreateGroupScreenViewModel(db = db)){
    val presets by createGroupScreenViewModel.presets.collectAsState()
    Scaffold(topBar = {
        TopAppBar(title = {Text(text = "Presets")},
            navigationIcon = {
               IconButton(onClick = { onNavigateToInitialScreen() }) {
                   Icon(
                       imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                       contentDescription = "back to start screen"
                   )
               }
        })
    }, floatingActionButton = {
        ExtendedFloatingActionButton(onClick = { onNavigateToNewPresetScreen() }) {
            Icon(Icons.Filled.Add, contentDescription = "add new preset")
            Text(text = "create preset")
        }
    }
    ) {
        innerPadding ->
        PresetList(Modifier.padding(innerPadding),navController = navController, presets = presets)
    }
}

@Composable
private fun PresetList(modifier: Modifier, navController: NavController, presets: List<Preset>){
    Column(modifier = modifier) {
        presets.forEach{
            preset -> ListItem(modifier = Modifier.clickable { navController.navigate("editpreset/" + preset.id) }, headlineContent = { Text(text = preset.presetName) }, supportingContent = { Text(text = preset.presetDescription) } )
        }
    }
}