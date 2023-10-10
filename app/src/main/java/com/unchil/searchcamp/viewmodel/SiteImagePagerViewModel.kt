package com.unchil.searchcamp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unchil.searchcamp.data.GoCampingService
import com.unchil.searchcamp.data.Repository
import com.unchil.searchcamp.db.entity.SiteImage_TBL
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SiteImagePagerViewModel  (val repository: Repository) : ViewModel() {

    val siteImageListStateFlow: MutableStateFlow<List<SiteImage_TBL>>
        = repository.siteImageListStateFlow


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

            is Event.GetImageList -> {
                getSiteImageList(contentId = event.contentId)
            }
        }

    }

    fun getSiteImageList(contentId:String){
        viewModelScope.launch {
            repository.getSiteImageList(contentId = contentId)
        }
    }

    fun recvGoCampingData(
        serviceType: GoCampingService,
        mapX:String? = null,
        mapY:String?=null,
        keyword:String? = null,
        contentId:String
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

    sealed class Event {
        data class RecvGoCampingData(
            val servicetype: GoCampingService,
            val mapX:String? = null,
            val mapY:String? = null,
            val keyword:String? = null,
            val  contentId:String
        ):Event()

        data class GetImageList(val contentId:String):Event()

    }


}