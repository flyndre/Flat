package de.flyndre.flat.composables.presetscreen.collectionareascreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import de.flyndre.flat.database.AppDatabase
import io.github.dellisd.spatialk.geojson.dsl.point

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionAreaScreen(modifier: Modifier = Modifier, navController: NavController, collectionAreaScreenViewModel: CollectionAreaScreenViewModel){
    var movingEnabled by remember { mutableStateOf(true) }
    var selectedItem by remember { mutableStateOf(0) }
    val listAreaPoints by collectionAreaScreenViewModel.listAreaPoints.collectAsState()
    val cameraPosition by collectionAreaScreenViewModel.cameraPosition.collectAsState()
    val cameraPositionState = rememberCameraPositionState {
        position = cameraPosition
    }
    Scaffold(
        topBar = {
            TopAppBar(title = { if(movingEnabled){
                Text(text = "Zoom to the collection area")
            }else{
                Text(text = "Select the collection area")
            }
                 }, navigationIcon = { IconButton(onClick = { navController.navigate("editpreset/0") }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "back to preset screen")
            }})
        },
        bottomBar = {
                    NavigationBar {
                        NavigationBarItem(selected = selectedItem == 0, onClick = { selectedItem = 0 }, icon = {
                            Icon(
                                painter = painterResource(id = de.flyndre.flat.R.drawable.map_fill),
                                contentDescription = "search on map"
                            )
                        })
                        NavigationBarItem(selected = selectedItem == 1, onClick = { selectedItem = 1 }, icon = {
                            Icon(
                                painter = painterResource(id = de.flyndre.flat.R.drawable.palette_fill),
                                contentDescription = "draw collection areas on map"
                            )
                        })
                        NavigationBarItem(selected = selectedItem == 2, onClick = { selectedItem = 2 }, icon = {
                            Icon(
                                painter = painterResource(id = de.flyndre.flat.R.drawable.texture_fill),
                                contentDescription = "chose available collection areas"
                            )
                        })
                    }
                    /*if(!movingEnabled){
                        Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)) {
                            Button(onClick = {
                                collectionAreaScreenViewModel.setCameraPosition(cameraPositionState.position)
                                navController.navigate("editpreset/0")
                            }) {
                                Text(text = "Save Area")
                            }
                            Button(onClick = { collectionAreaScreenViewModel.removeLastCollectionAreaPoint() }) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "remove the last point")
                            }
                            Button(onClick = { collectionAreaScreenViewModel.clearCollectionArea() }) {
                                Icon(Icons.Filled.Delete, contentDescription = "delete the existing collection area")
                            }
                        }
                    }*/
        },
        floatingActionButton = {
            if(selectedItem == 1){

            }
            /*if(movingEnabled){
                SmallFloatingActionButton(onClick = { movingEnabled = false }) {
                    Icon(Icons.Filled.Edit, contentDescription = "lock map for editing collection area")
                }
            }else{
                SmallFloatingActionButton(onClick = { movingEnabled = true }) {
                    Icon(Icons.Filled.LocationOn, contentDescription = "unlock map for zooming")
                }
            }*/
        }
    ) {
        innerPadding ->
        var mapSettings: MapUiSettings
        var mapProperties: MapProperties
        if(selectedItem == 0){
            mapSettings = MapUiSettings(zoomControlsEnabled = false)
            mapProperties = MapProperties(isMyLocationEnabled = true)
        }else if(selectedItem == 1){
            mapSettings = MapUiSettings(zoomControlsEnabled = false, zoomGesturesEnabled = false, tiltGesturesEnabled = false, rotationGesturesEnabled = false, scrollGesturesEnabled = false)
            mapProperties = MapProperties(isMyLocationEnabled = false)
        }else{
            mapSettings = MapUiSettings(zoomControlsEnabled = false)
            mapProperties = MapProperties(isMyLocationEnabled = true)
        }

        GoogleMap(modifier = Modifier.padding(innerPadding), uiSettings = mapSettings, properties = mapProperties, cameraPositionState = cameraPositionState, onMapClick = {if(!movingEnabled){collectionAreaScreenViewModel.addPCollectionAreaPoint(it)}}){
            if(listAreaPoints.isNotEmpty()){
                Polygon(points = listAreaPoints, fillColor = Color(255, 159, 246, 127), strokeColor = Color(255, 159, 246, 255))
            }
        }
    }
}

