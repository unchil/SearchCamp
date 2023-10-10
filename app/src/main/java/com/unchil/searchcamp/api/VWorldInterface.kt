package com.unchil.searchcamp.api

import com.unchil.searchcamp.model.VWorldResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface VWorldInterface {

    @GET("data")
    suspend fun  recvVWORLD(
        @Query("key")apiKey:String,
        @Query("request")request:String,
        @Query("data")data:String,
        @Query("geomfilter")geomfilter:String,
        @Query("size")size:String,
        @Query("geometry")geometry:String,
        @Query("crs")crs:String,
    //    @Query("attrFilter")attrFilter:String
        ):VWorldResponse

}

