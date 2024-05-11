package de.flyndre.flat.composables.trackingscreen

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import de.flyndre.flat.R
import de.flyndre.flat.models.AccessResquestMessage
import qrcode.render.QRCodeGraphics
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(
    modifier: Modifier = Modifier,
    trackingScreenViewModel: TrackingScreenViewModel,
    onNavigateToInitialScreen: () -> Unit,
    onNavigateToParticipantScreen: () -> Unit,
    onShareLink: ((String) -> Unit),
    userId: UUID,
) {
    val trackingEnabled by trackingScreenViewModel.trackingEnabled.collectAsState()
    val localTrackList by trackingScreenViewModel.trackList.collectAsState()
    val remoteTrackList by trackingScreenViewModel.remoteTrackList.collectAsState()
    val participantsToJoin by trackingScreenViewModel.participantsToJoin.collectAsState()
    val qrCodeGraphics by trackingScreenViewModel.qrCodeGraphics.collectAsState()
    val joinLink by trackingScreenViewModel.joinLink.collectAsState()
    val cameraPosition by trackingScreenViewModel.cameraPosition.collectAsState()
    val cameraPositionState = rememberCameraPositionState {
        position = cameraPosition
    }
    var showLeavingDialog by remember { mutableStateOf(false) }
    var showClosingDialog by remember { mutableStateOf(false) }
    var showAddPaticipantsDialog by remember { mutableStateOf(false) }

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

    if (showLeavingDialog) {
        LeavingDialog(
            onDecline = { showLeavingDialog = false },
            onAccept = { trackingScreenViewModel.leaveOrCloseCollection(false); onNavigateToInitialScreen() })
    }

    if (showClosingDialog) {
        ClosingDialog(
            onDecline = { showClosingDialog = false },
            onAccept = { trackingScreenViewModel.leaveOrCloseCollection(true); onNavigateToInitialScreen() })
    }
    if (showAddPaticipantsDialog) {
        AddParticipantDialog(
            onDismissRequest = { showAddPaticipantsDialog = false },
            onShareButtonClick = onShareLink,
            qrCodeGraphics = qrCodeGraphics,
            joinLink = joinLink
        )
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = trackingScreenViewModel.collectionInstance.name) },
            navigationIcon = {
                if (!userId.equals(trackingScreenViewModel.collectionInstance.clientId)) {//if this user is no admin
                    IconButton(onClick = { showLeavingDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "back to start screen"
                        )
                    }
                }
            })
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
                        Text(text = "Stop Tracking")
                    } else {
                        Text(text = "Start Tracking")
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
                    text = "Add Participant"
                )
            }, modifier = Modifier.padding(10.dp))
        }
    }, floatingActionButton = {
        Column {
            if (userId.equals(trackingScreenViewModel.collectionInstance.clientId)) {
                AdminMenu(
                    onClosingCollection = { showClosingDialog = true },
                    onNavigateToParticipantScreen = onNavigateToParticipantScreen,
                    trackingScreenViewModel = trackingScreenViewModel
                )
            }
            FloatingActionButton(modifier = Modifier.padding(10.dp), onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "center on own location"
                )
            }
            FloatingActionButton(modifier = Modifier.padding(10.dp), onClick = { /*TODO*/ }) {
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
            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(zoomControlsEnabled = false),
            cameraPositionState = cameraPositionState
        ) {
            //rendering local track
            if (localTrackList.isNotEmpty()) {
                for (track in localTrackList) {
                    var list = arrayListOf<LatLng>()
                    for (position in track) {
                        list.add(LatLng(position.latitude, position.longitude))
                    }
                    if (list.isNotEmpty()) {
                        Polyline(points = list)
                    }
                }
            }
            //rendering remote tracks
            if (remoteTrackList.isNotEmpty()) {
                for (trackCollection in remoteTrackList) {
                    for (track in trackCollection.value) {
                        var list = arrayListOf<LatLng>()
                        for (position in track) {
                            list.add(LatLng(position.latitude, position.longitude))
                        }
                        if (list.isNotEmpty()) {
                            Polyline(points = list)
                        }
                    }

                }
            }
            //rendering collection areas
            if (trackingScreenViewModel.collectionInstance.collectionDivision.isNotEmpty()) {
                for (collectionArea in trackingScreenViewModel.collectionInstance.collectionDivision) {
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
                    Polygon(
                        points = list,
                        strokeColor = Color(red, green, blue, alpha = 255),
                        fillColor = Color(red, green, blue, alpha = 127)
                    )
                }
            }
        }
    }
}

@Composable
fun AdminMenu(
    onClosingCollection: () -> Unit,
    onNavigateToParticipantScreen: () -> Unit,
    trackingScreenViewModel: TrackingScreenViewModel,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(text = "End Collection") },
                onClick = { onClosingCollection() })
            HorizontalDivider()
            DropdownMenuItem(text = { Text(text = "Manage Groups") }, onClick = {
                trackingScreenViewModel.updateParticipantScreenViewModel()
                onNavigateToParticipantScreen()
            })
        }
        FloatingActionButton(modifier = Modifier.padding(10.dp), onClick = { expanded = true }) {
            Icon(Icons.Filled.MoreVert, contentDescription = "open collection management")
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
                Text(text = joinLink)
            }
            Button(onClick = { onShareButtonClick(joinLink) }) {
                Text(text = "Share")
            }
        }
    }
}