package de.flyndre.flat.composables.creategroupscreen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CreateGroupScreenViewModel: ViewModel() {
    private val _createGroupState = MutableStateFlow(CreateGroupScreenState())
    val createGroupState: StateFlow<CreateGroupScreenState> = _createGroupState.asStateFlow()
}