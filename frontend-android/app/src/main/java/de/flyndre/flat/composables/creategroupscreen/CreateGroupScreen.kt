package de.flyndre.flat.composables.creategroupscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import de.flyndre.flat.database.entities.Preset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(
    modifier: Modifier = Modifier,
    onNavigateToInitialScreen: () -> Unit,
    onNavigateToPresetScreen: () -> Unit,
    createGroupScreenViewModel: CreateGroupScreenViewModel,
) {
    val presets by createGroupScreenViewModel.presets.collectAsState()
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    if (showDeleteConfirmationDialog) {
        deleteConfimationDialog(onConfirmation = {
            showDeleteConfirmationDialog = false; createGroupScreenViewModel.deleteSetPreset()
        }, onDismiss = { showDeleteConfirmationDialog = false })
    }
    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "Presets") },
            navigationIcon = {
                IconButton(onClick = { onNavigateToInitialScreen() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back to start screen"
                    )
                }
            })
    }, floatingActionButton = {
        ExtendedFloatingActionButton(onClick = {
            createGroupScreenViewModel.navigateToPreset(
                0,
                onNavigateToPresetScreen
            )
        }) {
            Icon(Icons.Filled.Add, contentDescription = "add new preset")
            Text(text = "create preset")
        }
    }
    ) { innerPadding ->
        PresetList(
            Modifier.padding(innerPadding),
            createGroupScreenViewModel,
            onNavigateToPresetScreen,
            presets = presets,
            onDelete = { showDeleteConfirmationDialog = true })
    }
}

@Composable
private fun PresetList(
    modifier: Modifier,
    createGroupScreenViewModel: CreateGroupScreenViewModel,
    onNavigateToPresetScreen: () -> Unit,
    presets: List<Preset>,
    onDelete: () -> Unit,
) {
    LazyColumn(modifier = modifier) {
       items(presets){
           ListItem(
               modifier = Modifier.clickable {
                   createGroupScreenViewModel.navigateToPreset(
                       it.id,
                       onNavigateToPresetScreen
                   )
               },
               headlineContent = { Text(text = it.presetName) },
               supportingContent = { Text(text = it.presetDescription) },
               trailingContent = {
                   Icon(
                       Icons.Filled.Delete,
                       contentDescription = "Delete preset",
                       modifier = Modifier.clickable {
                           createGroupScreenViewModel.setIdToBeDeleted(it.id)
                           onDelete()
                       })
               }
           )
       }
    }
}

@Composable
fun deleteConfimationDialog(onConfirmation: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirmation) {
                Text(text = "Zustimmen")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(
                    text = "Ablehnen"
                )
            }
        }, icon = {
            Icon(
                Icons.Default.Info,
                contentDescription = "asking whether to leave the collection"
            )
        },
        title = {
            Text(
                text = "Warnung"
            )
        },
        text = { Text(text = "Bist du sicher, dass du die Vorlage löschen möchtest?") }
    )
}