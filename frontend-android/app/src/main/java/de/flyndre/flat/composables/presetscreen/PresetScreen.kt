package de.flyndre.flat.composables.presetscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.flyndre.flat.database.AppDatabase


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetScreen(
    modifier: Modifier = Modifier,
    presetId: Int?, db: AppDatabase, topBarText: String, onNavigateToCreateGroupScreen: () -> Unit, presetScreenViewModel: PresetScreenViewModel = PresetScreenViewModel(presetId = presetId, db = db)){
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text(text = topBarText) }, navigationIcon = { IconButton(
            onClick = { onNavigateToCreateGroupScreen() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "back to preset screen"
            )
        }})
    },
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
            ) {
                Button(onClick = {  }) {
                    Text("Save and Start")
                }
                Button(onClick = {  }) {
                    Text("Save")
                }
            }
        }) {
        innerPadding -> {
            Modifier.padding(innerPadding)
    }
    }
}