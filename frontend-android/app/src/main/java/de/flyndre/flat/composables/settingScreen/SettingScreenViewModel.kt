package de.flyndre.flat.composables.settingScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.flyndre.flat.interfaces.ISettingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingScreenViewModel(val settingService: ISettingService) : ViewModel() {

    private val _basePath = MutableStateFlow(settingService.getServerBaseUrl())
    val basePath :StateFlow<String> = _basePath.asStateFlow()

    private val _userName = MutableStateFlow(settingService.getDefaultUserName())
    val userName : StateFlow<String> = _userName.asStateFlow()

    private val _userId = MutableStateFlow(settingService.getClientId().toString())
    val userId : StateFlow<String> = _userId.asStateFlow()

    fun updateBasePath(path: String){
        settingService.setServerBaseUrl(path)
        _basePath.value = path
    }

    fun updateUserName(userName: String){
        settingService.setDefaultUserName(userName)
        _userName.value = userName
    }

    fun updateUserId(){
        _userId.value = settingService.resetClientId().toString()
    }
}

class CreateSettingScreenViewModelFactory(
    val settingService: ISettingService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingScreenViewModel(settingService) as T
    }
}