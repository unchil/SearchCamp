package com.unchil.searchcamp.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unchil.searchcamp.db.entity.CampSite_TBL
import com.unchil.searchcamp.db.entity.NearCampSite_TBL
import com.unchil.searchcamp.db.entity.SiGunGu_TBL
import kotlinx.coroutines.flow.Flow

@Dao
interface CampSite_Dao {
    @Query("DELETE FROM CampSite_TBL")
    suspend fun trancate()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(it: CampSite_TBL)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert_List(it: List<CampSite_TBL>)



    @Query("DELETE FROM CampSite_TBL WHERE contentId = :contentId" )
    suspend fun delete(contentId: String)


    @Query( "SELECT * FROM CampSite_TBL WHERE contentId = :contentId" )
    fun select_Flow(contentId: String): Flow<CampSite_TBL>


    @Query("SELECT * FROM CampSite_TBL " +
            " WHERE " +
            "doNm IN (:donm) "+
            "AND sigunguNm LIKE :sigungNm " +
            "AND addr1 LIKE :addr1 " +
            "AND facltNm LIKE :facltNm " +
            "ORDER BY contentId")
    fun select_Search(
        donm: Array<String>,
        sigungNm: String,
        addr1: String,
        facltNm:String):  List<CampSite_TBL>


    @Query("SELECT * FROM CampSite_TBL " +
            " WHERE " +
            "doNm IN (:donm) "+
            "AND sigunguNm LIKE :sigungNm " +
            "AND addr1 LIKE :addr1 " +
            "AND facltNm LIKE :facltNm " +
            "ORDER BY contentId")
    fun select_Search_Flow(
        donm: Array<String>,
        sigungNm: String,
        addr1: String,
        facltNm:String):  Flow<List<CampSite_TBL>>


    /*
    @Query("SELECT * FROM CampSite_TBL ORDER BY contentId")
    fun select_Search_Paging(): PagingSource<Int, CampSite_TBL>


     */

    @Query("SELECT * FROM CampSite_TBL " +
            " WHERE " +
            "doNm IN (:donm) "+
            "AND sigunguNm LIKE :sigungNm " +
            "AND addr1 LIKE :addr1 " +
            "AND facltNm LIKE :facltNm " +
            "ORDER BY contentId")
    fun select_Search_Paging(
        donm: Array<String>,
        sigungNm: String,
        addr1: String,
        facltNm:String): PagingSource<Int, CampSite_TBL>

    @Query("SELECT * FROM CampSite_TBL ORDER BY contentId")
    fun select_All_Flow(): Flow<List<CampSite_TBL>>


}

