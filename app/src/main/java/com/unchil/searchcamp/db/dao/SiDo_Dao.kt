package com.unchil.searchcamp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unchil.searchcamp.db.entity.SiDo_TBL
import kotlinx.coroutines.flow.Flow

@Dao
interface SiDo_Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(it: SiDo_TBL)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert_List(it: List<SiDo_TBL>)

    @Query("DELETE FROM SiDo_TBL")
    suspend fun trancate()

    @Query("SELECT * FROM SiDo_TBL ")
    fun select_All_Flow(): Flow<List<SiDo_TBL>>


}
