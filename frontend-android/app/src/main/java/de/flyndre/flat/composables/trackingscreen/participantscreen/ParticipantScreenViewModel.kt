package de.flyndre.flat.composables.trackingscreen.participantscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.flyndre.flat.interfaces.IConnectionService
import de.flyndre.flat.models.CollectionArea
import de.flyndre.flat.models.CollectionInstance
import de.flyndre.flat.models.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ParticipantScreenViewModel(connectionService: IConnectionService): ViewModel() {
    private val connectionService = connectionService
    private lateinit var collectionInstance: CollectionInstance

    private var _initialUsers = arrayListOf<UserModel>()
    private val _users = MutableStateFlow(arrayListOf<UserModel>())
    val users = _users.asStateFlow()

    private var _initialDivisions = arrayListOf<CollectionArea>()
    private val _divisions = MutableStateFlow(arrayListOf<CollectionArea>())
    val divisions = _divisions.asStateFlow()

    fun initialValues(collectionInstance: CollectionInstance){
        _users.value = arrayListOf()
        _users.value.addAll(collectionInstance.confirmedUsers)
        _initialUsers.clear()
        _initialUsers.addAll(collectionInstance.confirmedUsers)

        _divisions.value = arrayListOf()
        _divisions.value.addAll(collectionInstance.collectionDivision)
        _initialDivisions.clear()
        _initialDivisions.addAll(collectionInstance.collectionDivision)

        this.collectionInstance = collectionInstance
    }

    fun saveAssignments(){
        viewModelScope.launch {
            for(iDiv in _initialDivisions){
                val newDiv = _divisions.value.find { it.id.equals(iDiv.id) }
                if(newDiv!!.clientId != null){
                    if(!newDiv.clientId!!.equals(iDiv.clientId)){
                        connectionService.assignCollectionArea(collectionInstance.id!!, iDiv, newDiv.clientId)
                    }
                }
            }
        }
    }

    fun setUserOfDivision(division: CollectionArea, user: UserModel){
        var tempList = arrayListOf<CollectionArea>()
        tempList.addAll(_divisions.value)
        val index = tempList.indexOf(division)
        tempList.remove(division)
        division.clientId = user.clientId
        tempList.add(index, division)
        _divisions.value = tempList
    }
}