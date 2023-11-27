package com.unchil.searchcamp.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CURRENTWEATHER_TBL")
data class CURRENTWEATHER_TBL(
    @PrimaryKey(autoGenerate = false)
//        var writeTime: Long,
    var dt: Long,
    var base: String ,
    var visibility: Int,
    var timezone: Long,
    var name: String,
    var latitude: Float,
    var longitude: Float,
    //       var altitude: Float,
    var main: String,
    var description: String,
    var icon : String,
    var temp: Float,
    var feels_like: Float,
    var pressure: Float,
    var humidity: Float,
    var temp_min: Float,
    var temp_max: Float,
    var speed : Float,
    var deg : Float,
    var all  : Int,
    var type : Int,
    var country : String,
    var sunrise : Long,
    var sunset : Long

)

