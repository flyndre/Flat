package de.flyndre.flat.composables.joinscreen

import android.app.AlertDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinScreen(
    modifier: Modifier = Modifier,
    onNavigateToInitialScreen: () -> Unit,
    onNavigateToTrackingScreen: () -> Unit,
    joinScreenViewModel: JoinScreenViewModel,
) {
    val joinLink by joinScreenViewModel.joinLink.collectAsState()
    val joinName by joinScreenViewModel.joinName.collectAsState()
    val lastCollections by joinScreenViewModel.lastCollections.collectAsState()
    //input error handling
    var isLinkEmpty by remember { mutableStateOf(false) }
    var isNameEmpty by remember { mutableStateOf(false) }
    //connection error handling
    val showConnectionError by joinScreenViewModel.showConnectionError.collectAsState()
    //timeout error handling
    val showTimeoutError by joinScreenViewModel.showTimeoutError.collectAsState()
    //server connection in progress
    var loading by remember { mutableStateOf(false) }

    if(showTimeoutError){
        TimeoutErrorDialog(onDecline = {
            loading = false
            joinScreenViewModel.hideTimeoutError()
        })
    }

    if (showConnectionError) {
        ConnectionErrorDialog(onDecline = {
            loading = false
            joinScreenViewModel.hideConnectionError()
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "") }, navigationIcon = {
                IconButton(onClick = { onNavigateToInitialScreen() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back to start screen"
                    )
                }
            })
        }) { innerPadding ->
        Column(
            modifier = modifier.padding(innerPadding),
            horizontalAlignment = Alignment.Start
        ) {
            val modifier = Modifier.padding(10.dp)
            TextField(
                modifier = modifier,
                value = joinLink,
                onValueChange = { joinScreenViewModel.updateJoinLink(it) },
                label = { Text(text = "Link for joining") },
                isError = isLinkEmpty,
                supportingText = {
                    if (isLinkEmpty) {
                        Text(text = "Darf nicht leer sein")
                    }
                })
            TextField(
                modifier = modifier,
                value = joinName,
                onValueChange = { joinScreenViewModel.updateJoinName(it) },
                label = { Text(text = "Name for joining") },
                isError = isNameEmpty,
                supportingText = {
                    if (isNameEmpty) {
                        Text(text = "Darf nicht leer sein")
                    }
                })
            Row {
                Button(modifier = modifier, onClick = {
                    if (joinLink.equals("")) {
                        isLinkEmpty = true
                    } else if (joinName.equals("")) {
                        isNameEmpty = true
                    } else {
                        loading = true
                        joinScreenViewModel.join { onNavigateToTrackingScreen() }
                    }
                }) {
                    Text(text = "Join")
                }
                if(loading){
                    CircularProgressIndicator(modifier.padding(5.dp))
                }
            }
            Column(Modifier.padding(15.dp)) {
                Text(text = "Verlauf")
                lastCollections.reversed().forEach {
                    ListItem(
                        headlineContent = { Text(text = it) },
                        modifier = Modifier.clickable { joinScreenViewModel.updateJoinLink(it) })
                }
            }
        }
    }
}

@Composable
fun ConnectionErrorDialog(onDecline: () -> Unit) {
    AlertDialog(onDismissRequest = { onDecline() }, confirmButton = {
        TextButton(onClick = { onDecline() }) {
            Text(text = "OK")
        }
    }, icon = {
        Icon(
            Icons.Default.Info,
            contentDescription = "error while trying to connect to server"
        )
    }, title = { Text(text = "Verbindungsfehler") },
        text = {
            Text(text = "Es konnte der Sammlung nicht beigetreten werden.")
        })
}

@Composable
fun TimeoutErrorDialog(onDecline: () -> Unit) {
    AlertDialog(onDismissRequest = { onDecline() }, confirmButton = {
        TextButton(onClick = { onDecline() }) {
            Text(text = "OK")
        }
    }, icon = {
        Icon(
            Icons.Default.Info,
            contentDescription = "timeout while trying to connect to server"
        )
    }, title = { Text(text = "Verbindungsfehler") },
        text = {
            Text(text = "Der Administrator hat die Anfrage nicht beantwortet. Versuche es erneut.")
        })
}