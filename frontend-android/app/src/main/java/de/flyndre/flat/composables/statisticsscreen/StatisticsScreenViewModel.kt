package de.flyndre.flat.composables.statisticsscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StatisticsScreenViewModel() : ViewModel() {

}

class CreateStatisticsScreenViewModelFactory() : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StatisticsScreenViewModel() as T
    }
}