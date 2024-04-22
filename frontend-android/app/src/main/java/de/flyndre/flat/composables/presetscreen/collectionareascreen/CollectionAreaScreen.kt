package de.flyndre.flat.composables.presetscreen.collectionareascreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import de.flyndre.flat.composables.customComponents.SegmentedButtonItem
import de.flyndre.flat.composables.customComponents.SegmentedButtons
import de.flyndre.flat.ui.theme.AreaBlue
import de.flyndre.flat.ui.theme.AreaGreen
import de.flyndre.flat.ui.theme.AreaOrange
import de.flyndre.flat.ui.theme.AreaPink
import de.flyndre.flat.ui.theme.AreaPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionAreaScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    collectionAreaScreenViewModel: CollectionAreaScreenViewModel,
) {
    //bottom navigation bar
    var selectedNavigationItem by remember { mutableStateOf(0) }
    //color picker based on Segmented Button
    var selectedColorItem by remember { mutableStateOf(0) }
    //map data
    val cameraPosition by collectionAreaScreenViewModel.cameraPosition.collectAsState()
    val cameraPositionState = rememberCameraPositionState {
        position = cameraPosition
    }
    //modal bottom sheet for choosing area
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Edit Collection Area")
                    IconButton(onClick = {
                        collectionAreaScreenViewModel.setCameraPosition(cameraPositionState.position)
                        navController.navigate("editpreset/0")
                    }) {
                        Icon(
                            painter = painterResource(id = de.flyndre.flat.R.drawable.save_fill),
                            contentDescription = "save and return to preset view"
                        )
                    }

                }

            }, navigationIcon = {
                IconButton(onClick = { navController.navigate("editpreset/0") }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back to preset screen"
                    )
                }
            })
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedNavigationItem == 0,
                    onClick = { selectedNavigationItem = 0 },
                    icon = {
                        Icon(
                            painter = painterResource(id = de.flyndre.flat.R.drawable.map_fill),
                            contentDescription = "search on map"
                        )
                    })
                NavigationBarItem(
                    selected = selectedNavigationItem == 1,
                    onClick = { selectedNavigationItem = 1 },
                    icon = {
                        Icon(
                            painter = painterResource(id = de.flyndre.flat.R.drawable.palette_fill),
                            contentDescription = "draw collection areas on map"
                        )
                    })
                NavigationBarItem(
                    selected = selectedNavigationItem == 2,
                    onClick = { selectedNavigationItem = 2 },
                    icon = {
                        Icon(
                            painter = painterResource(id = de.flyndre.flat.R.drawable.texture_fill),
                            contentDescription = "chose available collection areas"
                        )
                    })
            }
        },
        floatingActionButton = {
            if(selectedNavigationItem == 0){
                SmallFloatingActionButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Filled.Search, contentDescription = "search for location")
                }
            }else if (selectedNavigationItem == 1) {
                Row(
                    modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        10.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SegmentedButtons(modifier = Modifier
                        .width((LocalConfiguration.current.screenWidthDp * 0.7).dp)
                        .height(40.dp)) {
                        SegmentedButtonItem(selected = selectedColorItem == 0, onClick = { selectedColorItem = 0 }, icon = { Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(shape = RoundedCornerShape(5.dp))
                                .background(color = AreaBlue)
                        )})
                        SegmentedButtonItem(selected = selectedColorItem == 1, onClick = { selectedColorItem = 1 }, icon = { Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(shape = RoundedCornerShape(5.dp))
                                .background(color = AreaPink)
                        )})
                        SegmentedButtonItem(selected = selectedColorItem == 2, onClick = { selectedColorItem = 2 }, icon = { Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(shape = RoundedCornerShape(5.dp))
                                .background(color = AreaGreen)
                        )})
                        SegmentedButtonItem(selected = selectedColorItem == 3, onClick = { selectedColorItem = 3 }, icon = { Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(shape = RoundedCornerShape(5.dp))
                                .background(color = AreaOrange)
                        )})
                        SegmentedButtonItem(selected = selectedColorItem == 4, onClick = { selectedColorItem = 4 }, icon = { Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(shape = RoundedCornerShape(5.dp))
                                .background(color = AreaPurple)
                        )})
                    }
                    SmallFloatingActionButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Filled.Edit, contentDescription = "create new area")
                    }
                }
            }else{
                SmallFloatingActionButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = "search for location")
                }
            }
        }
    ) { innerPadding ->
        var mapSettings: MapUiSettings
        var mapProperties: MapProperties
        if (selectedNavigationItem == 0) {
            mapSettings = MapUiSettings(zoomControlsEnabled = false)
            mapProperties = MapProperties(isMyLocationEnabled = true)
        } else if (selectedNavigationItem == 1) {
            mapSettings = MapUiSettings(
                zoomControlsEnabled = false,
                zoomGesturesEnabled = false,
                tiltGesturesEnabled = false,
                rotationGesturesEnabled = false,
                scrollGesturesEnabled = false
            )
            mapProperties = MapProperties(isMyLocationEnabled = false)
        } else {
            mapSettings = MapUiSettings(zoomControlsEnabled = false)
            mapProperties = MapProperties(isMyLocationEnabled = true)
        }

        GoogleMap(
            modifier = Modifier.padding(innerPadding),
            uiSettings = mapSettings,
            properties = mapProperties,
            cameraPositionState = cameraPositionState,
            onMapClick = {
                if (selectedNavigationItem == 1) {
                    collectionAreaScreenViewModel.addPCollectionAreaPoint(it)
                }
            }) {
            if (collectionAreaScreenViewModel.listCollectionAreas.isNotEmpty()) {
                collectionAreaScreenViewModel.listCollectionAreas.forEach{ area ->
                    Polygon(
                        points = area.listAreaPoints,
                        fillColor = area.color.copy(alpha = 0.5f),
                        strokeColor = area.color.copy(alpha = 1F)
                    )
                }
            }
        }
    }
}

