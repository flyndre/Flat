package de.flyndre.flat.composables.trackingscreen.participantscreen

import androidx.lifecycle.ViewModel
import de.flyndre.flat.models.CollectionArea
import de.flyndre.flat.models.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ParticipantScreenViewModel: ViewModel() {
    private val _users = MutableStateFlow(arrayListOf<UserModel>())
    val users = _users.asStateFlow()

    private val _divisions = MutableStateFlow(arrayListOf<CollectionArea>())
    val divisions = _divisions.asStateFlow()

    fun setUsers(users: List<UserModel>){
        _users.value = arrayListOf()
        _users.value.addAll(users)
    }

    fun setDivisions(divisions: List<CollectionArea>){
        _divisions.value = arrayListOf()
        _divisions.value.addAll(divisions)
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