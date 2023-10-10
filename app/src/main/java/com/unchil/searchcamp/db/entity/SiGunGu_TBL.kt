package com.unchil.searchcamp.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SiGunGu_TBL")
data class SiGunGu_TBL (
    @PrimaryKey(autoGenerate = false)
    var sig_cd:String,
    var sig_kor_nm:String,
    var sig_eng_nm: String,
    var full_nm: String
)