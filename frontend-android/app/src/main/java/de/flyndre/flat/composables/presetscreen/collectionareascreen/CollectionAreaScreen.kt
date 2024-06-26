package de.flyndre.flat.composables.presetscreen.collectionareascreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
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
    onNavigateToPresetScreen: () -> Unit,
    collectionAreaScreenViewModel: CollectionAreaScreenViewModel,
) {
    //bottom navigation bar
    var selectedNavigationItem by remember { mutableStateOf(0) }
    //color picker based on Segmented Button
    var selectedColorItem by remember { mutableStateOf(AreaBlue) }
    //drawing state
    var drawingEnabled by remember { mutableStateOf(false) }
    //map data
    val collectionAreas by collectionAreaScreenViewModel.listCollectionAreas.collectAsState()
    val cameraPosition by collectionAreaScreenViewModel.cameraPosition.collectAsState()
    val cameraPositionState = rememberCameraPositionState {
        position = cameraPosition
    }
    val ownLocation by collectionAreaScreenViewModel.ownLocation.collectAsState()
    //editing area names
    var areaName by remember { mutableStateOf("") }
    var areaIndex: Long? by remember { mutableStateOf(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        collectionAreaScreenViewModel.saveChanges()
                        collectionAreaScreenViewModel.setCameraPosition(cameraPositionState.position)
                        onNavigateToPresetScreen()
                    }) {
                        Icon(
                            painter = painterResource(id = de.flyndre.flat.R.drawable.save_fill),
                            contentDescription = "save and return to preset view"
                        )
                    }

                }

            }, navigationIcon = {
                IconButton(onClick = {
                    collectionAreaScreenViewModel.discardChanges()
                    onNavigateToPresetScreen()
                }) {
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
            if (selectedNavigationItem == 0) {
                SmallFloatingActionButton(onClick = { collectionAreaScreenViewModel.centerOnPosition(cameraPositionState) }) {
                    Icon(Icons.Filled.LocationOn, contentDescription = "focus on own location")
                }
            } else if (selectedNavigationItem == 1) {
                Row(
                    modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        10.dp,
                        Alignment.End
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (drawingEnabled) {
                        SmallFloatingActionButton(onClick = { collectionAreaScreenViewModel.removeLastCollectionAreaPoint() }) {
                            Icon(
                                painter = painterResource(id = de.flyndre.flat.R.drawable.undo_fill),
                                contentDescription = "delete last point"
                            )
                        }
                        SmallFloatingActionButton(onClick = {
                            collectionAreaScreenViewModel.addCollectionAreaPoint(
                                cameraPositionState.position.target
                            )
                        }) {
                            Icon(Icons.Filled.Add, contentDescription = "add point")
                        }
                        SmallFloatingActionButton(onClick = {
                            drawingEnabled = false;
                            collectionAreaScreenViewModel.checkNewCollectionIsEmpty()
                        }) {
                            Icon(Icons.Filled.Check, contentDescription = "finish drawing of area")
                        }
                    } else {
                        SegmentedButtons(
                            modifier = Modifier
                                .width((LocalConfiguration.current.screenWidthDp * 0.7).dp)
                                .height(40.dp)
                        ) {
                            SegmentedButtonItem(
                                selected = selectedColorItem.equals(AreaBlue),
                                onClick = { selectedColorItem = AreaBlue },
                                icon = {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(shape = RoundedCornerShape(5.dp))
                                            .background(color = AreaBlue)
                                    )
                                })
                            SegmentedButtonItem(
                                selected = selectedColorItem.equals(AreaPink),
                                onClick = { selectedColorItem = AreaPink },
                                icon = {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(shape = RoundedCornerShape(5.dp))
                                            .background(color = AreaPink)
                                    )
                                })
                            SegmentedButtonItem(
                                selected = selectedColorItem.equals(AreaGreen),
                                onClick = { selectedColorItem = AreaGreen },
                                icon = {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(shape = RoundedCornerShape(5.dp))
                                            .background(color = AreaGreen)
                                    )
                                })
                            SegmentedButtonItem(
                                selected = selectedColorItem.equals(AreaOrange),
                                onClick = { selectedColorItem = AreaOrange },
                                icon = {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(shape = RoundedCornerShape(5.dp))
                                            .background(color = AreaOrange)
                                    )
                                })
                            SegmentedButtonItem(
                                selected = selectedColorItem.equals(AreaPurple),
                                onClick = { selectedColorItem = AreaPurple },
                                icon = {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(shape = RoundedCornerShape(5.dp))
                                            .background(color = AreaPurple)
                                    )
                                })
                        }
                        SmallFloatingActionButton(onClick = {
                            drawingEnabled = true
                            collectionAreaScreenViewModel.addNewCollectionArea(selectedColorItem)
                        }) {
                            Icon(Icons.Filled.Edit, contentDescription = "draw new area")
                        }
                    }
                }
            } else {

            }
        }
    ) { innerPadding ->
        var mapsModifier: Modifier = Modifier
        var mapSettings: MapUiSettings
        var mapProperties: MapProperties
        if (selectedNavigationItem == 0) {
            mapSettings = MapUiSettings(zoomControlsEnabled = false)
            mapProperties = MapProperties()
        } else if (selectedNavigationItem == 1) {
            mapSettings = MapUiSettings(
                zoomControlsEnabled = false,
                zoomGesturesEnabled = true,
                tiltGesturesEnabled = false,
                rotationGesturesEnabled = false,
                scrollGesturesEnabled = true
            )
            mapProperties = MapProperties(isMyLocationEnabled = false)
        } else {
            mapSettings = MapUiSettings(zoomControlsEnabled = false)
            mapProperties = MapProperties(isMyLocationEnabled = true)
            mapsModifier = mapsModifier.height((LocalConfiguration.current.screenHeightDp * 0.5).dp)
        }
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxHeight()
            .fillMaxWidth()) {
            //map

            GoogleMap(
                modifier = mapsModifier,

                uiSettings = mapSettings,
                properties = mapProperties,

                cameraPositionState = cameraPositionState,
            ) {
                //draw areas
                if (collectionAreas.isNotEmpty()) {
                    collectionAreas.forEach { area ->
                        if (area.listAreaPoints.isNotEmpty()) {
                            Polygon(
                                points = area.listAreaPoints,
                                fillColor = area.color.copy(alpha = 0.5f),
                                strokeColor = area.color.copy(alpha = 1F)
                            )
                            if(area.isSelected&&area.listAreaPoints.isNotEmpty()){
                                area.listAreaPoints.forEach {
                                    Circle(
                                        center = it,
                                        radius = 2.0,
                                        fillColor = area.color.copy(alpha = 1F),
                                        strokeColor = area.color.copy(alpha = 1F))
                                }
                            }
                        }
                    }
                }
                //draw own location indicator
                if(ownLocation != null){
                    Circle(
                        center = ownLocation!!,
                        radius = 2.0,
                        strokeColor = Color(66, 90, 245),
                        fillColor = Color(66, 90, 245)
                    )
                }
            }
            if(drawingEnabled){
                Icon(
                    Icons.Filled.Add, "Cursor", modifier = Modifier
                        .align(Alignment.Center)
                        .height(40.dp)
                        .width(40.dp), tint = Color.Black
                )
            }
            //list of areas if navigation is set to
            if (selectedNavigationItem == 2) {
                LazyColumn {
                    items(collectionAreas) {
                        ListItem(modifier = Modifier.clickable {
                            collectionAreaScreenViewModel.animateCameraPositionToCollectionArea(
                                it,
                                cameraPositionState
                            )
                        },
                            headlineContent = {
                                if(areaIndex == null){
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = it.name
                                        )
                                        IconButton(onClick = {
                                            areaName = it.name
                                            areaIndex = collectionAreas.indexOf(it).toLong()
                                        }) {
                                            Icon(imageVector = Icons.Filled.Edit, contentDescription = "edit area name")
                                        }
                                    }
                                }else{
                                    if(areaIndex!!.toInt() == collectionAreas.indexOf(it)){
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            TextField(value = areaName, onValueChange = {areaName = it}, trailingIcon = {
                                                IconButton(onClick = {
                                                    collectionAreaScreenViewModel.setCollectionAreaName(it, areaName)
                                                    areaName = ""
                                                    areaIndex = null
                                                }) {
                                                    Icon(painterResource(id = de.flyndre.flat.R.drawable.save_fill), contentDescription = "save area name")
                                                }
                                            })
                                        }
                                    }else{
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = it.name
                                            )
                                            IconButton(onClick = {
                                                areaName = it.name
                                                areaIndex = collectionAreas.indexOf(it).toLong()
                                            }) {
                                                Icon(imageVector = Icons.Filled.Edit, contentDescription = "edit area name")
                                            }
                                        }
                                    }
                                }
                            },
                            leadingContent = {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(shape = RoundedCornerShape(3.dp))
                                        .background(color = it.color)
                                )
                            },
                            trailingContent = {
                                IconButton(onClick = {
                                    collectionAreaScreenViewModel.removeCollectionArea(
                                        it
                                    )
                                }) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = "delete area",
                                    )
                                }
                            })
                    }
                }
            }
        }
    }
}

