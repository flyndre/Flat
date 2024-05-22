package de.flyndre.flat.composables.presetscreen.collectionareascreen

import android.widget.ListView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.LinearGradient
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
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
    //map data
    val collectionAreas by collectionAreaScreenViewModel.listCollectionAreas.collectAsState()
    val cameraPosition by collectionAreaScreenViewModel.cameraPosition.collectAsState()
    val cameraPositionState = rememberCameraPositionState {
        position = cameraPosition
    }
    val selectedPoint by collectionAreaScreenViewModel.selectedPoint.collectAsState()
    val selectedArea by collectionAreaScreenViewModel.selectedArea.collectAsState()
    var areaListVisible = remember { mutableStateOf(false)}
    //modal bottom sheet for choosing area
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Area editieren")
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
                },
                navigationIcon = {
                    IconButton(onClick = {
                        collectionAreaScreenViewModel.discardChanges()
                        onNavigateToPresetScreen()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "back to preset screen"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black.copy(alpha = 0.5f))
            )
        },
        bottomBar = {
            Column(modifier = Modifier.heightIn(0.dp,300.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .clickable { areaListVisible.value = !areaListVisible.value }
                    ) {
                    HorizontalDivider(
                        Modifier
                            .height(10.dp)
                            .align(Alignment.CenterHorizontally), thickness = 3.dp)
                    HorizontalDivider(
                        Modifier
                            .height(10.dp)
                            .align(Alignment.CenterHorizontally), thickness = 3.dp)
                }
                if(areaListVisible.value) {
                    FloatingActionButton(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .align(Alignment.CenterHorizontally),
                        onClick = { collectionAreaScreenViewModel.addArea() }) {
                        Row(){
                            Icon(Icons.Filled.Add, contentDescription = "Add new Area")
                            Text(text = "Neue Area")
                        }
                    }
                    LazyColumn {
                        items(collectionAreas) {
                            val border = if(it==selectedArea) 2.dp else 0.dp
                            ListItem(modifier = Modifier.clickable {
                                collectionAreaScreenViewModel.setSelectedArea(
                                    it,
                                    cameraPositionState
                                )
                            }.border(width = border, color = Color.White),
                                headlineContent = {
                                    Text(
                                        text = "Area " + (collectionAreas.indexOf(
                                            it
                                        ) + 1)
                                    )
                                },
                                supportingContent = {
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
        },
        floatingActionButton = {
            Row {
                if(selectedArea!=null){
                    SmallFloatingActionButton(onClick = { collectionAreaScreenViewModel.removeLastCollectionAreaPoint() }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Remove point")
                    }
                    SmallFloatingActionButton(onClick = { collectionAreaScreenViewModel.addPoint(cameraPositionState.position.target) }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add point")
                    }
                    if(selectedPoint!=null){
                        SmallFloatingActionButton(onClick = { collectionAreaScreenViewModel.updatePoint(
                            selectedPoint!!,cameraPositionState.position.target) }) {
                            Icon(Icons.Filled.Edit,"Edit point")
                        }
                    }
                }
            }
            /*
            if (selectedNavigationItem == 0) {
                SmallFloatingActionButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Filled.Search, contentDescription = "search for location")
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
                            if(selectedPoint==null){
                                collectionAreaScreenViewModel.addPoint(
                                    cameraPositionState.position.target
                                )
                            }else{
                                collectionAreaScreenViewModel.updatePoint(selectedPoint!!, cameraPositionState.position.target)
                            }
                        }) {
                            Icon(Icons.Filled.Add, contentDescription = "add point")
                        }
                        SmallFloatingActionButton(onClick = {
                            drawingEnabled = false;
                            collectionAreaScreenViewModel.setSelectedPoint(null,cameraPositionState)
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
                            if(selectedPoint!=null) collectionAreaScreenViewModel.addNewCollectionArea(selectedColorItem)
                        }) {
                            Icon(Icons.Filled.Edit, contentDescription = "draw new area")
                        }
                    }
                }
            } else {

            }*/
        }
    ) { innerPadding ->
        var mapsModifier: Modifier = Modifier
        var mapSettings: MapUiSettings
        var mapProperties: MapProperties
        mapSettings = MapUiSettings(
            zoomControlsEnabled = false,
            zoomGesturesEnabled = true,
            tiltGesturesEnabled = false,
            rotationGesturesEnabled = true,
            scrollGesturesEnabled = true
        )
        mapProperties = MapProperties(isMyLocationEnabled = false)

        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxHeight()
            .fillMaxWidth()) {
            //map
            GoogleMap(
                uiSettings = mapSettings,
                properties = mapProperties,
                onMapClick = {
                    collectionAreaScreenViewModel.setSelectedPoint(null, cameraPositionState)
                    collectionAreaScreenViewModel.setSelectedArea(null, cameraPositionState)
                },
                cameraPositionState = cameraPositionState,
            ) {
                if (collectionAreas.isNotEmpty()) {
                    collectionAreas.forEach { area ->
                        if (area.listAreaPoints.isNotEmpty()) {
                            Polygon(
                                points = area.listAreaPoints,
                                fillColor = area.color.copy(alpha = 0.5f),
                                strokeColor = area.color.copy(alpha = 1F),
                                clickable = true,
                                onClick = {
                                    collectionAreaScreenViewModel.setSelectedArea(
                                        area,
                                        cameraPositionState
                                    )
                                }
                            )

                        }
                    }
                }
                selectedArea?.listAreaPoints?.forEach { point ->
                    Circle(
                        center = point,
                        radius = 2.0,
                        clickable = true,
                        onClick = {
                            collectionAreaScreenViewModel.setSelectedPoint(
                                point,
                                cameraPositionState
                            )
                        },
                        fillColor = if (point == selectedPoint) Color.Red else Color.Black,
                        strokeColor = if (point == selectedPoint) Color.Red else Color.Black
                    )
                }
            }
            Icon(Icons.Outlined.Add,"Cursor",modifier = Modifier
                .align(Alignment.Center)
                .height(40.dp)
                .width(40.dp), tint = Color.Black)
        }
    }
}

