package com.unchil.searchcamp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.unchil.searchcamp.data.GoCampingService
import com.unchil.searchcamp.data.Repository
import com.unchil.searchcamp.db.entity.CampSite_TBL
import com.unchil.searchcamp.db.entity.SiteImage_TBL
import com.unchil.searchcamp.model.GoCampingResponseStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class SearchScreenViewModel   (
    val repository: Repository,
    val administrativeDistrictSiDoCode: String,
    val administrativeDistrictSiGunGu: String,
    val searchTitle: String? = null


) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)

    val currentListDataCntStateFlow: MutableStateFlow<Int> = MutableStateFlow(0)

    val currentListDataStateFlow  = repository.currentListDataStateFlow


    private val _recvSiteImageListState:MutableStateFlow<RecvSiteImageListState>
        = MutableStateFlow(RecvSiteImageListState.Success(emptyList()))

    val recvSiteImageListState: StateFlow<RecvSiteImageListState> = _recvSiteImageListState


    private val _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect

    val campSiteListPaging: Flow<PagingData<CampSite_TBL>>
    val searchQueryFlow: Flow<Event.Search>
    val eventHandler: (Event) -> Unit



    init{

        viewModelScope.launch {
            repository._recvSiteImageListState.collect{
                _recvSiteImageListState.value = it
            }
        }


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

                if( event.servicetype == GoCampingService.SITEIMAGE){
                    event.contentId?.let {
                        recvGoCampingDataImageList( it )
                    }
                }else {
                    recvGoCampingData(
                        event.servicetype,
                        event.mapX,
                        event.mapY,
                        event.keyword,
                        event.contentId
                    )
                }

            }

            Event.InitRecvSiteImageList -> {
                initRecvSiteImageList()
            }
        }
    }

    private fun initRecvSiteImageList(){
        repository.setLoadingStateRecvSiteImageListState()
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


    fun recvGoCampingDataImageList(
        contentId:String
    ){
        viewModelScope.launch {
            repository.recvGoCampingDataImageList(
                contentId
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

        object InitRecvSiteImageList: Event()
    }

    sealed class RecvSiteImageListState {
        data class Success(val dataList: List<SiteImage_TBL>) : RecvSiteImageListState()
        object Loading: RecvSiteImageListState()
        data class Error(val responseStatus: GoCampingResponseStatus) :  RecvSiteImageListState()
    }


    sealed class Effect {
         object QueryResultCount: Effect()

    }


}