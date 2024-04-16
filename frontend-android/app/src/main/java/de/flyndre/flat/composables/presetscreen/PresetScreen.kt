package de.flyndre.flat.composables.presetscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import de.flyndre.flat.composables.presetscreen.collectionareascreen.CollectionAreaScreenViewModel
import de.flyndre.flat.database.AppDatabase


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetScreen(
    modifier: Modifier = Modifier,
    presetId: Long?, navController: NavController, topBarText: String, onNavigateToCreateGroupScreen: () -> Unit, onNavigateToTrackingScreen: () -> Unit, presetScreenViewModel: PresetScreenViewModel){
    val presetName by presetScreenViewModel.presetName.collectAsState()
    val presetDescription by presetScreenViewModel.presetDescription.collectAsState()

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text(text = topBarText) }, navigationIcon = { IconButton(
            onClick = { onNavigateToCreateGroupScreen() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "back to create group screen"
            )
        }})
    },
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
            ) {
                Button(onClick = { presetScreenViewModel.savePresetToDatabase(); presetScreenViewModel.openCollection(); onNavigateToTrackingScreen() }) {
                    Text("Save and Start")
                }
                Button(onClick = { presetScreenViewModel.savePresetToDatabase(); onNavigateToCreateGroupScreen() }) {
                    Text("Save")
                }
            }
        }) {
        innerPadding ->
        Column (modifier = Modifier.padding(innerPadding)) {
            val modifier = Modifier.padding(10.dp)
            TextField(modifier = modifier, value = presetName, onValueChange = {presetScreenViewModel.updatePresetName(it)}, label = {Text(text = "Preset Name")})
            TextField(modifier = modifier, value = presetDescription, onValueChange = {presetScreenViewModel.updatePresetDescription(it)}, label = {Text(text = "Preset Description")})
            Card (modifier = modifier){
                GoogleMap(onMapClick = { navController.navigate("collectionarea/" + presetScreenViewModel.getPresetId()) },
                    cameraPositionState = CameraPositionState(position = presetScreenViewModel.getCameraPosition()),
                    uiSettings = MapUiSettings(zoomControlsEnabled = false, zoomGesturesEnabled = false, scrollGesturesEnabled = false, rotationGesturesEnabled = false, tiltGesturesEnabled = false))
            }
        }
    }
}