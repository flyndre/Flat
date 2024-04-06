package de.flyndre.flat.composables.creategroupscreen

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.room.Room
import de.flyndre.flat.database.AppDatabase
import de.flyndre.flat.database.entities.Preset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.transform

class CreateGroupScreenViewModel(db: AppDatabase) : ViewModel() {
    private val _createGroupState = MutableStateFlow(CreateGroupScreenState())
    val createGroupState: StateFlow<CreateGroupScreenState> = _createGroupState.asStateFlow()
}