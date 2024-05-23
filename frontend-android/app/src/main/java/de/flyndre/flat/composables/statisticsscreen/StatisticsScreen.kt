package de.flyndre.flat.composables.statisticsscreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateToInitialScreen: () -> Unit,
    statisticsScreenViewModel: StatisticsScreenViewModel
){

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "Statistics") },
                navigationIcon = {
                    IconButton(onClick = { onNavigateToInitialScreen() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "back to start screen"
                        )
                    }
                })
        }
    ) {
        innerPadding ->
        Modifier.padding(innerPadding)
    }
}