package de.flyndre.flat.composables.trackingscreen.participantscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ParticipantScreenViewModel: ViewModel() {

}

class CreateParticipantScreenViewModelFactory() : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ParticipantScreenViewModel() as T
    }
}