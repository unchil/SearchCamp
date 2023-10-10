package com.unchil.searchcamp.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "CollectTime_TBL")
data class CollectTime_TBL(
    @PrimaryKey(autoGenerate = false)
    var collectDataType: String,
    var collectTime:Long
)
