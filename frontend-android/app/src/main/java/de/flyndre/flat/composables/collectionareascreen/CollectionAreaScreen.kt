package de.flyndre.flat.composables.collectionareascreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import de.flyndre.flat.database.AppDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionAreaScreen(modifier: Modifier = Modifier, presetId: Long, db: AppDatabase, navController: NavController){
    Scaffold(
        topBar = {
            TopAppBar(title = {}, navigationIcon = { IconButton(onClick = { navController.navigate("editpreset/$presetId") }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "back to preset screen")
            }})
        }
    ) {
        innerPadding ->
        modifier.padding(innerPadding)
    }
}