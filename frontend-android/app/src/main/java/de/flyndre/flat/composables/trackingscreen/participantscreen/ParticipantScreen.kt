package de.flyndre.flat.composables.trackingscreen.participantscreen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantScreen(
    onNavigateToTrackingScreen: () -> Unit,
    participantScreenViewModel: ParticipantScreenViewModel
){
    val users by participantScreenViewModel.users.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "Teilnehmer") },
                navigationIcon = {
                    IconButton(onClick = { onNavigateToTrackingScreen() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "back to tracking screen")
                    }
                })
        }
    ) {
        innerPadding ->
        LazyColumn(Modifier.padding(innerPadding)) {
            items(users){
                ListItem(headlineContent = { Text(text = it.username) },
                    trailingContent = {
                        IconButton(onClick = { participantScreenViewModel.kickUser(it) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "kick participant"
                            )
                        }
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "user icon")
                    })
            }
        }
    }
}