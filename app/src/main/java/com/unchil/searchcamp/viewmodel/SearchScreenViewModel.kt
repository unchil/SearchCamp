package com.unchil.searchcamp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unchil.searchcamp.data.GoCampingService
import com.unchil.searchcamp.data.Repository
import com.unchil.searchcamp.model.CURRENTWEATHER_TBL
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchScreenViewModel   (val repository: Repository) : ViewModel() {


    val currentListDataCntStateFlow: MutableStateFlow<Int> = MutableStateFlow(0)

    private val _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect

    fun onEvent(event: Event){
        when(event){
            is Event.Search -> {
                getCampSiteListFlow(
                    event.administrativeDistrictSiDoCode,
                    event.administrativeDistrictSiGunGu,
                    event.searchTitle
                )
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

        }
    }

    fun getCampSiteListFlow(
        administrativeDistrictSiDoCode:String,
        administrativeDistrictSiGunGu:String,
        searchTitle:String? = null
    ){
        viewModelScope.launch {

           val resultCount =  repository.getCampSiteListFlow(
                administrativeDistrictSiDoCode,
                administrativeDistrictSiGunGu,
                searchTitle
            )

            currentListDataCntStateFlow.emit(resultCount)
            _effect.emit(Effect.QueryResultCount)

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

    sealed class Event{

        data class Search(
            val administrativeDistrictSiDoCode:String,
            val administrativeDistrictSiGunGu:String,
            val searchTitle:String? = null
        ):Event()

        data class RecvGoCampingData(
            val servicetype: GoCampingService,
            val mapX:String? = null,
            val mapY:String? = null,
            val keyword:String? = null,
            val  contentId:String? = null
        ): Event()


    }


    sealed class Effect {
         object QueryResultCount: Effect()
    }


}