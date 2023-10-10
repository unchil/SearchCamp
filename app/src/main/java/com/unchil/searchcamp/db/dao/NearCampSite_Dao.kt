package com.unchil.searchcamp.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unchil.searchcamp.db.entity.CampSite_TBL
import com.unchil.searchcamp.db.entity.NearCampSite_TBL
import kotlinx.coroutines.flow.Flow

@Dao
interface NearCampSite_Dao {

    @Query("DELETE FROM NearCampSite_TBL")
    suspend fun trancate()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(it: NearCampSite_TBL)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert_List(it: List<NearCampSite_TBL>)


    @Query("SELECT * FROM NearCampSite_TBL ORDER BY contentId")
    fun select_All_Paging():PagingSource<Int, CampSite_TBL>


    @Query("SELECT * FROM NearCampSite_TBL WHERE facltNm LIKE :facltNm ORDER BY contentId")
    fun select_Search_Paging( facltNm:String):PagingSource<Int, CampSite_TBL>



    @Query("SELECT * FROM NearCampSite_TBL ORDER BY contentId")
    fun select_All_Flow(): Flow<List<CampSite_TBL>>

    @Query("SELECT * FROM NearCampSite_TBL WHERE facltNm LIKE :facltNm ORDER BY contentId")
    fun select_Search_Flow( facltNm:String):Flow<List<CampSite_TBL>>


    @Query("SELECT * FROM NearCampSite_TBL WHERE facltNm LIKE :facltNm ORDER BY contentId")
    fun select_Search( facltNm:String):List<CampSite_TBL>

}