package de.flyndre.flat.composables.trackingscreen

import android.graphics.BitmapFactory
import android.location.Location
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import de.flyndre.flat.R
import de.flyndre.flat.models.AccessResquestMessage
import de.flyndre.flat.models.LeavingUserMessage
import de.flyndre.flat.models.Track
import de.flyndre.flat.models.TrackCollection
import qrcode.render.QRCodeGraphics
import java.util.UUID
import kotlin.math.roundToLong

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(
    modifier: Modifier = Modifier,
    trackingScreenViewModel: TrackingScreenViewModel,
    onNavigateToInitialScreen: () -> Unit,
    onNavigateToAssignmentScreen: () -> Unit,
    onNavigateToParticipantScreen: () -> Unit,
    onShareLink: ((String) -> Unit),
) {
    val trackingEnabled by trackingScreenViewModel.trackingEnabled.collectAsState()
    val localTrackList by trackingScreenViewModel.trackList.collectAsState()
    val remoteTrackList by trackingScreenViewModel.remoteTrackList.collectAsState()
    val divisionList by trackingScreenViewModel.divisionList.collectAsState()
    val participantsToJoin by trackingScreenViewModel.participantsToJoin.collectAsState()
    val participantsLeaved by trackingScreenViewModel.participantsLeaved.collectAsState()
    val qrCodeGraphics by trackingScreenViewModel.qrCodeGraphics.collectAsState()
    val joinLink by trackingScreenViewModel.joinLink.collectAsState()
    val cameraPosition by trackingScreenViewModel.cameraPosition.collectAsState()
    val cameraPositionState = rememberCameraPositionState {
        position = cameraPosition
    }
    val userId by trackingScreenViewModel.clientId.collectAsState()
    var showLeavingDialog by remember { mutableStateOf(false) }
    var showClosingDialog by remember { mutableStateOf(false) }
    var showAddPaticipantsDialog by remember { mutableStateOf(false) }
    var showSummaryDialog by remember { mutableStateOf(false) }
    val showCollectionClosedDialog by trackingScreenViewModel.showCollectionClosedDialog.collectAsState()
    val showParticipantKickedDialog by trackingScreenViewModel.showParticipantKickedDialog.collectAsState()
    val userList by trackingScreenViewModel.userList.collectAsState()

    BackHandler(enabled = true) {
        if (trackingScreenViewModel.isThisUserAdmin()) {
            showClosingDialog = true
        } else {
            showLeavingDialog = true
        }
    }

    if(showParticipantKickedDialog){
        ParticipantKickedDialog(onAccept = onNavigateToInitialScreen)
    }

    if(showCollectionClosedDialog){
        CollectionClosedDialog(onAccept = {showSummaryDialog=true})
    }

    if (participantsToJoin.isNotEmpty()) {
        ParticipantJoinDialog(
            onDecline = {
                trackingScreenViewModel.declineParticipantJoinDialog(
                    message = participantsToJoin.get(
                        0
                    )
                )
            },
            onAccept = {
                trackingScreenViewModel.acceptParticipantJoinDialog(
                    message = participantsToJoin.get(
                        0
                    )
                )
            },
            accessResquestMessage = participantsToJoin.get(0)
        )
    }

    if(participantsLeaved.isNotEmpty() && trackingScreenViewModel.isThisUserAdmin()){
        UserLeavedCollectionDialog(leavingUserMessage = participantsLeaved.first(), onAccept = { trackingScreenViewModel.removeFirstLeavedparticipant() })
    }

    if (showLeavingDialog) {
        LeavingDialog(
            onDecline = { showLeavingDialog = false },
            onAccept = { trackingScreenViewModel.leaveOrCloseCollection(false); showLeavingDialog=false; showSummaryDialog=true })
    }

    if (showClosingDialog) {
        ClosingDialog(
            onDecline = { showClosingDialog = false },
            onAccept = { trackingScreenViewModel.leaveOrCloseCollection(true);showClosingDialog=false; showSummaryDialog=true })
    }
    if (showAddPaticipantsDialog) {
        AddParticipantDialog(
            onDismissRequest = { showAddPaticipantsDialog = false },
            onShareButtonClick = onShareLink,
            qrCodeGraphics = qrCodeGraphics,
            joinLink = joinLink
        )
    }
    if(showSummaryDialog){
        SummaryDialog(
            onDismissRequest = {showSummaryDialog =false;onNavigateToInitialScreen()},
            localTrackList = localTrackList,
            remoteTrackList = remoteTrackList,
            userList = userList
        )
    }

    Scaffold(topBar = {
        Row() {
            if (!trackingScreenViewModel.isThisUserAdmin()) {
                FloatingActionButton(
                    modifier = Modifier.padding(10.dp),
                    onClick = { showLeavingDialog = true }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back to start screen"
                    )
                }
            }

        }
    }, bottomBar = {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            ExtendedFloatingActionButton(
                onClick = { trackingScreenViewModel.toggleTracking() },
                icon = {
                    if (trackingEnabled) {
                        Icon(
                            painter = painterResource(id = R.drawable.stop_circle_fill),
                            contentDescription = "toggle tracking"
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "toggle tracking"
                        )
                    }
                },
                text = {
                    if (trackingEnabled) {
                        Text(text = "Stop tracking")
                    } else {
                        Text(text = "Start tracking")
                    }
                },
                modifier = Modifier.padding(10.dp)
            )
            ExtendedFloatingActionButton(onClick = { showAddPaticipantsDialog = true }, icon = {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "show add participant dialog"
                )
            }, text = {
                Text(
                    text = "Einladen"
                )
            }, modifier = Modifier.padding(10.dp))
        }
    }, floatingActionButton = {
        Column {
            if (trackingScreenViewModel.isThisUserAdmin()) {
                AdminMenu(
                    onClosingCollection = { showClosingDialog = true },
                    onNavigateToAssignmentScreen = onNavigateToAssignmentScreen,
                    onNavigateToParticipantScreen = onNavigateToParticipantScreen,
                    trackingScreenViewModel = trackingScreenViewModel
                )
            }
            FloatingActionButton(
                modifier = Modifier.padding(10.dp),
                onClick = { trackingScreenViewModel.centerOnPosition(cameraPositionState = cameraPositionState) }) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "center on own location"
                )
            }
            FloatingActionButton(
                modifier = Modifier.padding(10.dp),
                onClick = { trackingScreenViewModel.centerOnOwnArea(cameraPositionState = cameraPositionState) }) {
                Icon(
                    painter = painterResource(id = R.drawable.texture_fill),
                    contentDescription = "center on own location"
                )
            }
        }
    }) { innerPadding ->
        Modifier.padding(innerPadding)
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = MapProperties(isMyLocationEnabled = false),
            uiSettings = MapUiSettings(zoomControlsEnabled = false),
            cameraPositionState = cameraPositionState
        ) {
            //rendering local track
            if (localTrackList.tracks.isNotEmpty()) {
                for (track in localTrackList.tracks) {
                    var list = arrayListOf<LatLng>()
                    for (position in track.positions) {
                        list.add(LatLng(position.latitude, position.longitude))
                    }
                    if (list.isNotEmpty()) {
                        Polyline(points = list, color = Color(66, 90, 245))
                        if (track.equals(localTrackList.tracks.last())) {
                            Circle(
                                center = list.last(),
                                radius = 2.0,
                                strokeColor = Color(66, 90, 245),
                                fillColor = Color(66, 90, 245)
                            )
                        }
                    }
                }
            }
            //rendering remote tracks
            if (remoteTrackList.isNotEmpty()) {
                for (trackCollection in remoteTrackList) {
                    for (track in trackCollection.value.tracks) {
                        var list = arrayListOf<LatLng>()
                        for (position in track.positions) {
                            list.add(LatLng(position.latitude, position.longitude))
                        }
                        if (list.isNotEmpty()) {
                            Polyline(points = list, color = Color(66, 90, 245))
                            if (track.equals(trackCollection.value.tracks.last())) {
                                Circle(
                                    center = list.last(),
                                    radius = 2.0,
                                    strokeColor = Color(66, 90, 245),
                                    fillColor = Color(66, 90, 245)
                                )
                            }
                        }
                    }

                }
            }
            //rendering collection areas
            if (divisionList.isNotEmpty()) {
                for (collectionArea in divisionList) {
                    //get inner list of multipolygon and draw it on map
                    val area = collectionArea.area.coordinates[0]
                    //convert list<position> in list<latlong>
                    val list = arrayListOf<LatLng>()
                    for (position in area) {
                        list.add(LatLng(position.latitude, position.longitude))
                    }

                    //compute colors from hex string
                    val red: Int = Integer.parseInt(collectionArea.color.substring(1, 3), 16)
                    val green: Int = Integer.parseInt(collectionArea.color.substring(3, 5), 16)
                    val blue: Int = Integer.parseInt(collectionArea.color.substring(5), 16)

                    //paint own collections with higher alpha
                    if (userId.equals(collectionArea.clientId.toString())) {
                        Polygon(
                            points = list,
                            strokeColor = Color(red, green, blue, alpha = 255),
                            fillColor = Color(red, green, blue, alpha = 127)
                        )
                    } else {
                        Polygon(
                            points = list,
                            strokeColor = Color(red, green, blue, alpha = 63),
                            fillColor = Color(red, green, blue, alpha = 31)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminMenu(
    onClosingCollection: () -> Unit,
    onNavigateToAssignmentScreen: () -> Unit,
    onNavigateToParticipantScreen: () -> Unit,
    trackingScreenViewModel: TrackingScreenViewModel,
) {
    var expanded by remember { mutableStateOf(false) }

    Row() {

        FloatingActionButton(modifier = Modifier.padding(10.dp), onClick = { expanded = true }) {
            Icon(Icons.Filled.MoreVert, contentDescription = "open collection management")
            if (expanded) {
                Popup(
                    onDismissRequest = { expanded = false },
                    alignment = Alignment.TopEnd,
                ) {
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        ExtendedFloatingActionButton(
                            modifier = Modifier.padding(bottom = 10.dp),
                            onClick = {
                                expanded = false
                                onClosingCollection()
                            })
                        {
                            Text(text = "Sammlung beenden")
                        }
                        ExtendedFloatingActionButton(
                            modifier = Modifier.padding(vertical = 10.dp),
                            onClick = {
                                expanded = false
                                trackingScreenViewModel.updateAssignmentScreenViewModel()
                                onNavigateToAssignmentScreen()
                            }) {
                            Text(text = "Zuweisungen verwalten")
                        }
                        ExtendedFloatingActionButton(
                            modifier = Modifier.padding(vertical = 10.dp),
                            onClick = {
                                expanded = false
                                trackingScreenViewModel.updateParticipantScreenViewModel()
                                onNavigateToParticipantScreen()
                            }) {
                            Text(text = "Teilnehmer verwalten")
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun ParticipantJoinDialog(
    onDecline: () -> Unit,
    onAccept: () -> Unit,
    accessResquestMessage: AccessResquestMessage,
) {
    AlertDialog(
        onDismissRequest = { onDecline() },
        confirmButton = {
            TextButton(onClick = { onAccept() }) {
                Text(
                    text = "Zustimmen"
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onDecline() }) {
                Text(
                    text = "Ablehnen"
                )
            }
        },
        icon = {
            Icon(
                Icons.Default.Info,
                contentDescription = "asking whether to accept the join of an user"
            )
        },
        title = {
            Text(
                text = "Information"
            )
        },
        text = { Text(text = "Der Nutzer " + accessResquestMessage.username + " möchte deiner Sammlung beitreten.") })
}

@Composable
fun LeavingDialog(onDecline: () -> Unit, onAccept: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDecline() },
        confirmButton = {
            TextButton(onClick = { onAccept() }) {
                Text(
                    text = "Verlassen"
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onDecline() }) {
                Text(
                    text = "Abbrechen"
                )
            }
        },
        icon = {
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
        text = { Text(text = "Bist du sicher, dass du die Sammlung verlassen möchtest?") })
}

@Composable
fun ClosingDialog(onDecline: () -> Unit, onAccept: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDecline() },
        confirmButton = {
            TextButton(onClick = { onAccept() }) {
                Text(
                    text = "Schließen"
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onDecline() }) {
                Text(
                    text = "Abbrechen"
                )
            }
        },
        icon = {
            Icon(
                Icons.Default.Info,
                contentDescription = "asking whether to close the collection"
            )
        },
        title = {
            Text(
                text = "Warnung"
            )
        },
        text = { Text(text = "Bist du sicher, dass du die Sammlung schließen möchtest?") })
}

@Composable
fun UserLeavedCollectionDialog(leavingUserMessage: LeavingUserMessage, onAccept: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onAccept() },
        confirmButton = {
            TextButton(onClick = { onAccept() }) {
                Text(text = "OK")
            }
        },
        icon = {
            Icon(imageVector = Icons.Default.Info, contentDescription = "user left the collection")
        },
        title = {
            Text(text = "Information")
        },
        text = { Text(text = "Der Nutzer " + leavingUserMessage.user.username + " hat die Sammlung verlassen.")}
    )
}

@Composable
fun CollectionClosedDialog(onAccept: () -> Unit){
    AlertDialog(onDismissRequest = { onAccept() },
        confirmButton = { 
            TextButton(onClick = { onAccept() }) {
                Text(text = "OK")
            }
        },
        icon = {
            Icon(imageVector = Icons.Default.Info, contentDescription = "collection was closed")
        },
        title = {
            Text(text = "Information")
        },
        text = { Text(text = "Die Sammlung wurde vom Administrator beendet.")}
    )
}

@Composable
fun ParticipantKickedDialog(onAccept: () -> Unit){
    AlertDialog(onDismissRequest = { onAccept() },
        confirmButton = {
            TextButton(onClick = { onAccept() }) {
                Text(text = "OK")
            }
        },
        icon = {
            Icon(imageVector = Icons.Default.Info, contentDescription = "collection was closed")
        },
        title = {
            Text(text = "Information")
        },
        text = { Text(text = "Du wurdest vom Administrator aus der Sammlung entfernt.")}
    )
}

@Composable
fun AddParticipantDialog(
    onDismissRequest: () -> Unit,
    onShareButtonClick: (String) -> Unit,
    qrCodeGraphics: QRCodeGraphics,
    joinLink: String,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(shape = RoundedCornerShape(16.dp)) {
            Image(
                bitmap = BitmapFactory.decodeByteArray(
                    qrCodeGraphics.getBytes(),
                    0,
                    qrCodeGraphics.getBytes().size
                ).asImageBitmap(), contentDescription = ""
            )
            SelectionContainer(modifier = Modifier.padding(10.dp)) {
                TextField(joinLink, { val s = it }, readOnly = true)
            }
            Button(onClick = { onShareButtonClick(joinLink) }) {
                Text(text = "Teilen")
            }
        }
    }
}

@Composable
fun SummaryDialog(
    onDismissRequest: () -> Unit,
    localTrackList: TrackCollection,
    remoteTrackList: Map<UUID, TrackCollection>,
    userList: Map<String,String>
){
    Dialog(onDismissRequest = onDismissRequest) {
        Card(shape = RoundedCornerShape(16.dp),) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(text = "Statistik")
                var localDistance = 0.0
                var remoteDistance = 0.0

                Text(text = "Du hast ${localTrackList.distance} m gesammelt")
                remoteTrackList.values.forEach {
                    Text(text = "${userList[it.clientId.toString()]} hat: = ${it.distance} m gesammelt")
                }
                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.align(Alignment.End)) {
                    Text(text = "Weiter")
                }
            }
        }
    }
}