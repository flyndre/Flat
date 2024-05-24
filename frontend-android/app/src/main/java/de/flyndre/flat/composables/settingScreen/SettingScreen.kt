package de.flyndre.flat.composables.settingScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    onNavigateToInitialScreen: () -> Unit,
    settingScreenViewModel: SettingScreenViewModel
){
    val basePath = settingScreenViewModel.basePath.collectAsState()
    val userName = settingScreenViewModel.userName.collectAsState()
    val userId = settingScreenViewModel.userId.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "Einstellungen") },
                navigationIcon = {
                    IconButton(onClick = { onNavigateToInitialScreen() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "back to start screen"
                        )
                    }
                })
        }
    ){innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            TextField(
                modifier = Modifier.padding(10.dp),
                value = basePath.value,
                onValueChange = { settingScreenViewModel.updateBasePath(it) },
                label = { Text("Serveradresse:") }
            )
            TextField(
                modifier = Modifier.padding(10.dp),
                value = userName.value,
                onValueChange = { settingScreenViewModel.updateUserName(it) },
                label = { Text("Username:") }
            )
            TextField(
                modifier = Modifier.padding(10.dp),
                value = userId.value,
                onValueChange = {},
                label = { Text(text = "UserId:")},
                readOnly = true
            )
            ExtendedFloatingActionButton(
                modifier = Modifier.padding(10.dp),
                onClick = {settingScreenViewModel.updateUserId()},

            ) {
                Icon(Icons.Filled.Refresh, contentDescription = "refresh userId")
                Text(text = "UserId erneuern")
            }
        }
    }
}