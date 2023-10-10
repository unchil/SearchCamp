package com.unchil.searchcamp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unchil.searchcamp.db.entity.SiDo_TBL
import com.unchil.searchcamp.db.entity.SiGunGu_TBL
import kotlinx.coroutines.flow.Flow


@Dao
interface SiGunGu_Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(it: SiGunGu_TBL)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert_List(it: List<SiGunGu_TBL>)

    @Query("DELETE FROM SiGunGu_TBL")
    suspend fun trancate()

    @Query("SELECT * FROM SiGunGu_TBL ")
    fun select_All_Flow(): Flow<List<SiGunGu_TBL>>


    @Query(
        "SELECT * FROM SiGunGu_TBL WHERE substr(sig_cd, 1, :length) =  :upCode  ORDER BY sig_cd "
    )
    fun select_Flow(upCode:String, length:Int): Flow<List<SiGunGu_TBL>>
}