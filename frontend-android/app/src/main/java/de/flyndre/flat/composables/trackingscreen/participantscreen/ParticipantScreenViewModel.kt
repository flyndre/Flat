package de.flyndre.flat.composables.trackingscreen.participantscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.flyndre.flat.models.CollectionInstance
import de.flyndre.flat.models.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ParticipantScreenViewModel: ViewModel() {
    private lateinit var collectionInstance: CollectionInstance

    private val _users = MutableStateFlow(arrayListOf<UserModel>())
    val users = _users.asStateFlow()

    fun initialValues(collectionInstance: CollectionInstance){
        this.collectionInstance = collectionInstance

        _users.value = collectionInstance.confirmedUsers
    }

    fun kickUser(userModel: UserModel){

    }
}

class CreateParticipantScreenViewModelFactory() : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ParticipantScreenViewModel() as T
    }
}