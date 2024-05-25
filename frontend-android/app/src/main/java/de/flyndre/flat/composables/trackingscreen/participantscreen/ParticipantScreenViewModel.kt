package de.flyndre.flat.composables.trackingscreen.participantscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import de.flyndre.flat.interfaces.IConnectionService
import de.flyndre.flat.models.CollectionInstance
import de.flyndre.flat.models.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ParticipantScreenViewModel(connectionService: IConnectionService): ViewModel() {
    private lateinit var collectionInstance: CollectionInstance
    private val connectionService = connectionService

    private val _users = MutableStateFlow(arrayListOf<UserModel>())
    val users = _users.asStateFlow()

    fun initialValues(collectionInstance: CollectionInstance){
        this.collectionInstance = collectionInstance

        //remove admin from users
        val userList: ArrayList<UserModel> = arrayListOf()
        for(user in collectionInstance.confirmedUsers){
            if(!user.clientId.equals(collectionInstance.clientId)){
                userList.add(user)
            }
        }

        _users.value = userList
    }

    fun kickUser(userModel: UserModel){
        viewModelScope.launch {
            connectionService.kickUser(collectionInstance, userModel.clientId)

            //remove kicked user from local list
            _users.value.remove(userModel)
        }
    }
}

class CreateParticipantScreenViewModelFactory(
    val connectionService: IConnectionService
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ParticipantScreenViewModel(connectionService) as T
    }
}