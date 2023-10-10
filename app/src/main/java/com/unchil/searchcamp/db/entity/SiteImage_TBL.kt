package com.unchil.searchcamp.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SiteImage_TBL", primaryKeys = arrayOf("contentId", "serialnum"))
data class SiteImage_TBL(
    var contentId:String,
    var serialnum:String,
    var createdtime:String,
    var imageUrl:String,
    var modifiedtime:String

)
