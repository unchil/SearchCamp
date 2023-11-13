package com.unchil.searchcamp.api

import com.unchil.searchcamp.model.GoCampingResponse
import com.unchil.searchcamp.model.GoCampingResponseImage
import com.unchil.searchcamp.model.GoCampingResponseImageEmpty
import retrofit2.http.GET
import retrofit2.http.Query

interface GoCampingInterface {

    @GET("basedList" )
    suspend fun getDefault(
        @Query("serviceKey")serviceKey:String,
        @Query("numOfRows")numOfRows:String,
        @Query("pageNo")pageNo:String,
        @Query("MobileOS")MobileOS:String,
        @Query("MobileApp")MobileApp:String,
        @Query("_type")_type:String
    ): GoCampingResponse

    @GET("locationBasedList")
    suspend fun getLocation(
        @Query("serviceKey")serviceKey:String,
        @Query("numOfRows")numOfRows:String,
        @Query("pageNo")pageNo:String,
        @Query("MobileOS")MobileOS:String,
        @Query("MobileApp")MobileApp:String,
        @Query("_type")_type:String,

        @Query("mapX")mapX:String,
        @Query("mapY")mapY:String,
        @Query("radius")radius:String

    ): GoCampingResponse

    @GET("searchList")
    suspend fun getSearch(
        @Query("serviceKey")serviceKey:String,
        @Query("numOfRows")numOfRows:String,
        @Query("pageNo")pageNo:String,
        @Query("MobileOS")MobileOS:String,
        @Query("MobileApp")MobileApp:String,
        @Query("_type")_type:String,

        @Query("keyword")keyword:String
    ): GoCampingResponse

    @GET("basedSyncList")
    suspend fun getSync(
        @Query("serviceKey")serviceKey:String,
        @Query("numOfRows")numOfRows:String,
        @Query("pageNo")pageNo:String,
        @Query("MobileOS")MobileOS:String,
        @Query("MobileApp")MobileApp:String,
        @Query("_type")_type:String,

       // @Query("syncStatus")syncStatus:String,
        @Query("syncModTime")syncModTime:String
    ): GoCampingResponse

    @GET("imageList")
    suspend fun getImage(
        @Query("serviceKey")serviceKey:String,
        @Query("numOfRows")numOfRows:String,
        @Query("pageNo")pageNo:String,
        @Query("MobileOS")MobileOS:String,
        @Query("MobileApp")MobileApp:String,
        @Query("_type")_type:String,
        @Query("contentId")contentId:String
    ): GoCampingResponseImage

    @GET("imageList")
    suspend fun getImageEmpty(
        @Query("serviceKey")serviceKey:String,
        @Query("numOfRows")numOfRows:String,
        @Query("pageNo")pageNo:String,
        @Query("MobileOS")MobileOS:String,
        @Query("MobileApp")MobileApp:String,
        @Query("_type")_type:String,
        @Query("contentId")contentId:String
    ): GoCampingResponseImageEmpty


}