package de.flyndre.flat.composables.trackingscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import de.flyndre.flat.models.Track
import java.util.UUID
import kotlin.math.exp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(
    modifier: Modifier = Modifier,
    trackingScreenViewModel: TrackingScreenViewModel,
    onNavigateToInitialScreen: () -> Unit,
    onNavigateToParticipantScreen: ()->Unit,
    userId: UUID,
) {
    val trackingEnabled by trackingScreenViewModel.trackingEnabled.collectAsState()
    val localTrackList by trackingScreenViewModel.trackList.collectAsState()
    val remoteTrackList by trackingScreenViewModel.remoteTrackList.collectAsState()

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = trackingScreenViewModel.collectionInstance.name) },
            navigationIcon = {
                IconButton(onClick = { onNavigateToInitialScreen() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back to start screen"
                    )
                }
            })
    }, bottomBar = {
        BottomAppBar() {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
            ) {
                Button(onClick = { trackingScreenViewModel.toggleTracking() }) {
                    if (trackingEnabled) {
                        Text(text = "Stop Tracking")
                    } else {
                        Text(text = "Start Tracking")
                    }
                }
                if (userId.equals(trackingScreenViewModel.collectionInstance.clientId)) {//if this user is admin
                    Button(onClick = {}) {
                        Text(text = "Add Participant")
                    }
                }
            }
        }
    }, floatingActionButton = {
        if(userId.equals(trackingScreenViewModel.collectionInstance.clientId)){
            AdminMenu(onNavigateToInitialScreen = onNavigateToInitialScreen, onNavigateToParticipantScreen = onNavigateToParticipantScreen, trackingScreenViewModel = trackingScreenViewModel)
        }
    }) { innerPadding ->
        GoogleMap(modifier = Modifier.padding(innerPadding), properties = MapProperties(isMyLocationEnabled = true), uiSettings = MapUiSettings(zoomControlsEnabled = false)){
            //rendering local track
            if(localTrackList.isNotEmpty()){
                for(track in localTrackList){
                    var list = arrayListOf<LatLng>()
                    for(position in track){
                        list.add(LatLng(position.latitude, position.longitude))
                    }
                    if(list.isNotEmpty()){
                        Polyline(points = list)
                    }
                }
            }
            //rendering remote tracks
            if(remoteTrackList.isNotEmpty()){
                for(trackCollection in remoteTrackList){
                    for(track in trackCollection.value){
                        var list = arrayListOf<LatLng>()
                        for(position in track){
                            list.add(LatLng(position.latitude, position.longitude))
                        }
                        if(list.isNotEmpty()){
                            Polyline(points = list)
                        }
                    }
                }
            }
            //rendering collection areas
            if(trackingScreenViewModel.collectionInstance.divisions.isNotEmpty()){
                for(collectionArea in trackingScreenViewModel.collectionInstance.divisions){
                    //get inner list of multipolygon and draw it on map
                    val area = collectionArea.area.coordinates[0]
                    //convert list<position> in list<latlong>
                    val list = arrayListOf<LatLng>()
                    for(position in area){
                        list.add(LatLng(position.latitude, position.longitude))
                    }

                    Polygon(points = list)
                }
            }
        }
    }
}

@Composable
fun AdminMenu(onNavigateToInitialScreen: () -> Unit, onNavigateToParticipantScreen: () -> Unit, trackingScreenViewModel: TrackingScreenViewModel) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier) {
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text(text = "End Collection") }, onClick = { onNavigateToInitialScreen() })
            HorizontalDivider()
            DropdownMenuItem(text = { Text(text = "Manage Groups") }, onClick = {
                trackingScreenViewModel.updateParticipantScreenViewModel()
                onNavigateToParticipantScreen()
            })
        }
        FloatingActionButton(onClick = { expanded = true }) {
            Icon(Icons.Filled.MoreVert, contentDescription = "open collection management")
        }
    }
}

@Composable
fun ParticipantJoinDialog(onDecline: ()->Unit, onAccept: ()->Unit){
    Dialog(onDismissRequest = { onDecline() }) {
        Card(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "A new participant wants to join.")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                    TextButton(modifier = Modifier.padding(8.dp), onClick = { onDecline() }) {
                        Text(text = "Decline")
                    }
                    TextButton(modifier = Modifier.padding(8.dp), onClick = { onAccept() }) {
                        Text(text = "Accept")
                    }
                }
            }
        }
    }
}