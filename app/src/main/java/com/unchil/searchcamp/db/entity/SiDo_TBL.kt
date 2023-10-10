package com.unchil.searchcamp.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SiDo_TBL")
data class SiDo_TBL (
    @PrimaryKey(autoGenerate = false)
    var ctprvn_cd:String,
    var ctp_kor_nm:String,
    var ctp_eng_nm: String
)
