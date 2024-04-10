package de.flyndre.flat.composables.collectionareascreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import de.flyndre.flat.database.AppDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionAreaScreen(modifier: Modifier = Modifier, presetId: Long, db: AppDatabase, navController: NavController, collectionAreaScreenViewModel: CollectionAreaScreenViewModel = CollectionAreaScreenViewModel(presetId, db)){
    var movingEnabled by remember { mutableStateOf(true) }
    Scaffold(
        topBar = {
            TopAppBar(title = {}, navigationIcon = { IconButton(onClick = { navController.navigate("editpreset/$presetId") }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "back to preset screen")
            }})
        },
        bottomBar = {
                    if(!movingEnabled){
                        Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)) {
                            Button(onClick = { /*TODO*/ }) {
                                Icon(Icons.Filled.Edit, contentDescription = "create a new collection area")
                            }
                            Button(onClick = { /*TODO*/ }) {
                                Icon(Icons.Filled.Delete, contentDescription = "delete the existing collection area")
                            }
                        }
                    }
        },
        floatingActionButton = {
            if(movingEnabled){
                SmallFloatingActionButton(onClick = { movingEnabled = false }) {
                    Icon(Icons.Filled.Edit, contentDescription = "lock map for editing collection area")
                }
            }else{
                SmallFloatingActionButton(onClick = { movingEnabled = true }) {
                    Icon(Icons.Filled.LocationOn, contentDescription = "unlock map for zooming")
                }
            }
        }
    ) {
        innerPadding ->
        var mapSettings = MapUiSettings()
        var mapProperties = MapProperties()
        if(movingEnabled){
            mapSettings = MapUiSettings()
            mapProperties = MapProperties()
        }
        GoogleMap(modifier = Modifier.padding(innerPadding), uiSettings = mapSettings, properties = mapProperties)
    }
}