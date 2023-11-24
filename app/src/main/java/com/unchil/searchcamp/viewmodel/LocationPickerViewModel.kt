package com.unchil.searchcamp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.unchil.searchcamp.data.GoCampingService
import com.unchil.searchcamp.data.Repository
import com.unchil.searchcamp.data.VWorldService
import com.unchil.searchcamp.db.entity.SiDo_TBL
import com.unchil.searchcamp.db.entity.SiGunGu_TBL
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationPickerViewModel (val repository: Repository) : ViewModel() {


    private val _sidoListStateFlow: MutableStateFlow<List<SiDo_TBL>> = MutableStateFlow(emptyList())
    val sidoListStateFlow:StateFlow<List<SiDo_TBL>> = _sidoListStateFlow

    private val _sigunguListStateFlow: MutableStateFlow<List<SiGunGu_TBL>> = MutableStateFlow(emptyList())
    val sigunguListStateFlow:StateFlow<List<SiGunGu_TBL>> = _sigunguListStateFlow


    init {
        viewModelScope.launch {
            repository.sidoListStateFlow.collect{
                _sidoListStateFlow.value = it
            }
        }

        viewModelScope.launch {
            repository.sigunguListStateFlow.collect{
                _sigunguListStateFlow.value = it
            }
        }
    }


    fun onEvent(event: Event) {
        when (event) {

            is Event.GetSiGunGu -> {
                getSiGunGu(event.upCode)
            }

            is Event.RecvAdministrativeDistrict -> {
                recvAdministrativeDistrict(event.servicetype)
            }

            Event.GetSiDo -> {
                getSiDo()
            }

            is Event.RecvGoCampingData -> {
                recvGoCampingData(
                    event.servicetype,
                    event.mapX,
                    event.mapY,
                    event.keyword,
                    event.contentId
                )
            }

            is Event.SetCurrenttLatLng -> {
                setCurrentLatLng(event.data)
            }

            else -> {}
        }
    }


    fun setCurrentLatLng( data:LatLng){
        viewModelScope.launch {
            repository.setCurrentLatLng(data)
        }
    }

    fun recvGoCampingData(
        serviceType: GoCampingService,
        mapX:String? = null,
        mapY:String?=null,
        keyword:String? = null,
        contentId:String? = null
        ){
        viewModelScope.launch {
            repository.recvGoCampingData(
                serviceType,
                mapX,
                mapY,
                keyword,
                contentId
            )
        }
    }


    fun recvAdministrativeDistrict(serviceType: VWorldService){
        viewModelScope.launch {
            repository.recvVWorldData(serviceType)
        }
    }

    fun getSiGunGu(upCode:String){
        viewModelScope.launch {
            repository.getSiGunGuList(upCode)
        }
    }

    fun getSiDo(){
        viewModelScope.launch {
            repository.getSiDoList()
        }
    }

    sealed class Event {
        data class SetCurrenttLatLng(  val data: LatLng ): Event()

        data class RecvAdministrativeDistrict(val servicetype: VWorldService): Event()

        data class GetSiGunGu(val upCode:String): Event()

        object  GetSiDo:Event()

        data class RecvGoCampingData(
            val servicetype: GoCampingService,
            val mapX:String? = null,
            val mapY:String? = null,
            val keyword:String? = null,
            val  contentId:String? = null
            ): Event()



    }

}