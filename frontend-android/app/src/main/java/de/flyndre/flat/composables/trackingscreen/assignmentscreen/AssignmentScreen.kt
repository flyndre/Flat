package de.flyndre.flat.composables.trackingscreen.assignmentscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.painterResource
import de.flyndre.flat.R
import de.flyndre.flat.models.CollectionArea
import de.flyndre.flat.models.UserModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentScreen(
    assignmentScreenViewModel: AssignmentScreenViewModel,
    onNavigateToTrackingScreen: () -> Unit,
) {
    val divisions by assignmentScreenViewModel.divisions.collectAsState()
    val users by assignmentScreenViewModel.users.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            assignmentScreenViewModel.saveAssignments()
                            onNavigateToTrackingScreen()
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.save_fill),
                                contentDescription = "save and return to preset view"
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        assignmentScreenViewModel.restoreAssignments()
                        onNavigateToTrackingScreen()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "back to tracking screen"
                        )
                    }
                })
        }
    ) { innerPadding ->
        LazyColumn(Modifier.padding(innerPadding)) {
            items(divisions) {
                ListItem(
                    headlineContent = { Text(text = it.name) },
                    trailingContent = {
                        DropDownUserSelection(
                            assignmentScreenViewModel,
                            it,
                            users
                        )
                    })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownUserSelection(
    assignmentScreenViewModel: AssignmentScreenViewModel,
    division: CollectionArea,
    users: List<UserModel>,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedUser: UserModel? by remember {
        mutableStateOf(users.find {
            it.clientId.equals(
                division.clientId
            )
        })
    }
    var userDisplay by remember { mutableStateOf("") }
    if (selectedUser != null) {
        userDisplay = selectedUser!!.username
    }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        TextField(
            value = userDisplay,
            onValueChange = {},
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            users.forEach { user ->
                DropdownMenuItem(
                    text = { Text(text = user.username) },
                    onClick = { assignmentScreenViewModel.setUserOfDivision(division, user); userDisplay = user.username })
            }
        }
    }
}