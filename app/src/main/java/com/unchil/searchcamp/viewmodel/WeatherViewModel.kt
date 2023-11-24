package com.unchil.searchcamp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.unchil.searchcamp.data.Repository
import com.unchil.searchcamp.db.entity.CURRENTWEATHER_TBL
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(val repository: Repository) : ViewModel() {



    private val   _currentWeatheStaterFlow: MutableStateFlow<CURRENTWEATHER_TBL> = MutableStateFlow(
        CURRENTWEATHER_TBL( 0L, "", 0, 0L,"",0f, 0f,
            "", "", "", 0f,0f,0f,0f,0f,
            0f,0f,0f, 0, 0, "", 0L , 0L)
    )
    val  currentWeatheStaterFlow:StateFlow<CURRENTWEATHER_TBL> = _currentWeatheStaterFlow

    init {
        viewModelScope.launch {
            repository._currentWeather.collect{
                _currentWeatheStaterFlow.value = it
            }
        }
    }


      fun searchWeather(location: LatLng) {
         viewModelScope.launch {
             repository.getWeatherData(
                 location.latitude.toString(), location.longitude.toString()
             )
         }
    }


}