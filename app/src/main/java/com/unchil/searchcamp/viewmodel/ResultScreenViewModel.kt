package com.unchil.searchcamp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.unchil.searchcamp.data.GoCampingService
import com.unchil.searchcamp.data.Repository
import com.unchil.searchcamp.db.entity.CampSite_TBL
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ResultScreenViewModel   (
    val repository: Repository,
    administrativeDistrictSiDoCode:String,
    administrativeDistrictSiGunGu:String,
    searchTitle:String? = null
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)

    val campSiteListPaging: Flow<PagingData<CampSite_TBL>>
    val searchQueryFlow: Flow<Event.Search>
    val eventHandler: (Event) -> Unit


    init{
        val eventStateFlow = MutableSharedFlow<Event>()

        //  1. fun onEvent(event: Event)
        eventHandler = {
            viewModelScope.launch {
                eventStateFlow.emit(it)
            }
        }

        //  2.   when (event) { is Event.Search ->   }
        searchQueryFlow = eventStateFlow
            .filterIsInstance<Event.Search>()
            .distinctUntilChanged()
            .onStart {
                emit(
                    Event.Search(
                        administrativeDistrictSiDoCode = administrativeDistrictSiDoCode,
                        administrativeDistrictSiGunGu = administrativeDistrictSiGunGu,
                        searchTitle = searchTitle
                    )
                )
            }




        //  3.  viewModelScope.launch { searchMemo() }
        campSiteListPaging = searchQueryFlow
            .flatMapLatest {
                searchCampSite(
                    it.administrativeDistrictSiDoCode,
                    it.administrativeDistrictSiGunGu,
                    it.searchTitle
                )
            }.cachedIn(viewModelScope)
    }

    private fun searchCampSite(
        administrativeDistrictSiDoCode:String,
        administrativeDistrictSiGunGu:String,
        searchTitle:String? = null
    ): Flow<PagingData<CampSite_TBL>> {
        _isRefreshing.value = true
        val result = repository.getCampSiteListStream(
            administrativeDistrictSiDoCode,
            administrativeDistrictSiGunGu,
            searchTitle
        )
        _isRefreshing.value = false
        return result
    }

    fun onEvent(event: Event){
        when(event){
            is Event.RecvGoCampingData -> {
                recvGoCampingData(
                    event.servicetype,
                    event.mapX,
                    event.mapY,
                    event.keyword,
                    event.contentId
                )
            }
            is Event.Search -> {
                getCampSiteListFlow(
                    event.administrativeDistrictSiDoCode,
                    event.administrativeDistrictSiGunGu,
                    event.searchTitle
                )
            }

            else -> {}
        }
    }


    fun getCampSiteListFlow(
        administrativeDistrictSiDoCode:String,
        administrativeDistrictSiGunGu:String,
        searchTitle:String? = null
    ){
        viewModelScope.launch {
            repository.getCampSiteListFlow(
                administrativeDistrictSiDoCode,
                administrativeDistrictSiGunGu,
                searchTitle
            )
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

}