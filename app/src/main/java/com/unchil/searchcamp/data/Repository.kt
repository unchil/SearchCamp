package com.unchil.searchcamp.data


import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import com.google.android.gms.maps.model.LatLng
import com.unchil.searchcamp.BuildConfig
import com.unchil.searchcamp.api.GoCampingInterface
import com.unchil.searchcamp.api.OpenWeatherInterface
import com.unchil.searchcamp.api.RetrofitAdapter
import com.unchil.searchcamp.api.VWorldInterface
import com.unchil.searchcamp.db.SearchCampDB
import com.unchil.searchcamp.db.entity.CampSite_TBL
import com.unchil.searchcamp.db.entity.NearCampSite_TBL
import com.unchil.searchcamp.db.entity.SiDo_TBL
import com.unchil.searchcamp.db.entity.SiGunGu_TBL
import com.unchil.searchcamp.db.entity.SiteImage_TBL
import com.unchil.searchcamp.model.CURRENTWEATHER_TBL
import com.unchil.searchcamp.model.GoCampingRecvItem
import com.unchil.searchcamp.model.GoCampingResponse
import com.unchil.searchcamp.model.GoCampingResponseImage
import com.unchil.searchcamp.model.GoCampingResponseStatus
import com.unchil.searchcamp.model.GoCampingResponseStatusList
import com.unchil.searchcamp.model.GoCampingSyncStatus
import com.unchil.searchcamp.model.SiteDefaultData
import com.unchil.searchcamp.model.VWorldResponse
import com.unchil.searchcamp.model.getDesc
import com.unchil.searchcamp.model.toCURRENTWEATHER_TBL
import com.unchil.searchcamp.shared.UnixTimeToString
import com.unchil.searchcamp.shared.yyyyMMdd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import java.net.URLDecoder


enum class AdministrativeDistrictSiDo {
    SIDO_11,
    SIDO_26, SIDO_27, SIDO_28, SIDO_29, SIDO_30, SIDO_31, SIDO_36,
    SIDO_41, SIDO_43, SIDO_44, SIDO_45, SIDO_46, SIDO_47, SIDO_48,
    SIDO_50, SIDO_51
}

fun AdministrativeDistrictSiDo.getCodeString():String {
    return when(this){
        AdministrativeDistrictSiDo.SIDO_11 -> "11"
        AdministrativeDistrictSiDo.SIDO_26 -> "26"
        AdministrativeDistrictSiDo.SIDO_27 -> "27"
        AdministrativeDistrictSiDo.SIDO_28 -> "28"
        AdministrativeDistrictSiDo.SIDO_29 -> "29"
        AdministrativeDistrictSiDo.SIDO_30 -> "30"
        AdministrativeDistrictSiDo.SIDO_31 -> "31"
        AdministrativeDistrictSiDo.SIDO_36 -> "36"
        AdministrativeDistrictSiDo.SIDO_41 -> "41"
        AdministrativeDistrictSiDo.SIDO_43 -> "43"
        AdministrativeDistrictSiDo.SIDO_44 -> "44"
        AdministrativeDistrictSiDo.SIDO_45 -> "45"
        AdministrativeDistrictSiDo.SIDO_46 -> "46"
        AdministrativeDistrictSiDo.SIDO_47 -> "47"
        AdministrativeDistrictSiDo.SIDO_48 -> "48"
        AdministrativeDistrictSiDo.SIDO_50 -> "50"
        AdministrativeDistrictSiDo.SIDO_51 -> "51"
    }
}
fun AdministrativeDistrictSiDo.getExtentionNameList():Array<String>{
    return when(this){
        AdministrativeDistrictSiDo.SIDO_11 -> arrayOf("서울특별시", "서울시", "서울")
        AdministrativeDistrictSiDo.SIDO_26 -> arrayOf("부산광역시", "부산시", "부산")
        AdministrativeDistrictSiDo.SIDO_27 ->  arrayOf("대구광역시", "대구시", "대구")
        AdministrativeDistrictSiDo.SIDO_28 -> arrayOf("인천광역시", "인천시", "인천")
        AdministrativeDistrictSiDo.SIDO_29 -> arrayOf("광주광역시", "광주시", "광주")
        AdministrativeDistrictSiDo.SIDO_30 -> arrayOf("대전광역시", "대전시", "대전")
        AdministrativeDistrictSiDo.SIDO_31 -> arrayOf("울산광역시", "울산시", "울산")
        AdministrativeDistrictSiDo.SIDO_36 ->  arrayOf("세종특별자치시", "세종시", "세종")
        AdministrativeDistrictSiDo.SIDO_41 -> arrayOf("경기도", "경기")
        AdministrativeDistrictSiDo.SIDO_43 ->   arrayOf("충청북도", "충북")
        AdministrativeDistrictSiDo.SIDO_44 ->  arrayOf("충청남도", "충남")
        AdministrativeDistrictSiDo.SIDO_45 -> arrayOf("전라북도", "전북")
        AdministrativeDistrictSiDo.SIDO_46 -> arrayOf("전라남도", "전남")
        AdministrativeDistrictSiDo.SIDO_47 -> arrayOf("경상북도", "경북")
        AdministrativeDistrictSiDo.SIDO_48 -> arrayOf("경상남도", "경남")
        AdministrativeDistrictSiDo.SIDO_50 -> arrayOf("제주특별자치도", "제주도", "제주시", "제주")
        AdministrativeDistrictSiDo.SIDO_51 ->  arrayOf("강원특별자치도", "강원도", "강원")
    }
}

val AdministrativeDistrictSiDoList = listOf(
    AdministrativeDistrictSiDo.SIDO_11,
    AdministrativeDistrictSiDo.SIDO_26,
    AdministrativeDistrictSiDo.SIDO_27,
    AdministrativeDistrictSiDo.SIDO_28,
    AdministrativeDistrictSiDo.SIDO_29,
    AdministrativeDistrictSiDo.SIDO_30,
    AdministrativeDistrictSiDo.SIDO_31,
    AdministrativeDistrictSiDo.SIDO_36,
    AdministrativeDistrictSiDo.SIDO_41,
    AdministrativeDistrictSiDo.SIDO_43,
    AdministrativeDistrictSiDo.SIDO_44,
    AdministrativeDistrictSiDo.SIDO_45,
    AdministrativeDistrictSiDo.SIDO_46,
    AdministrativeDistrictSiDo.SIDO_47,
    AdministrativeDistrictSiDo.SIDO_48,
    AdministrativeDistrictSiDo.SIDO_50,
    AdministrativeDistrictSiDo.SIDO_51
)


enum class VWorldService {
    LT_C_ADSIDO_INFO, LT_C_ADSIGG_INFO
}

enum class GoCampingService {
    CAMPSITE, NEARCAMPSITE,SEARCH,SITEIMAGE,SYNC
}


enum class CollectType {
    LT_C_ADSIDO_INFO, LT_C_ADSIGG_INFO,  CAMPSITE, NEARCAMPSITE, SITEIMAGE, SYNC
}

val CollectTypeList = listOf(
    CollectType.LT_C_ADSIDO_INFO,
    CollectType.LT_C_ADSIGG_INFO,
    CollectType.CAMPSITE,
    CollectType.NEARCAMPSITE,
    CollectType.SITEIMAGE,
    CollectType.SYNC
)



fun GoCampingService.getPoint():String {
    return when(this){
        GoCampingService.CAMPSITE -> "/basedList"
        GoCampingService.NEARCAMPSITE -> "/locationBasedList"
        GoCampingService.SEARCH -> "/searchList"
        GoCampingService.SITEIMAGE -> "/imageList"
        GoCampingService.SYNC -> "/basedSyncList"
    }
}

class Repository {

    lateinit var database: SearchCampDB

    val OPENWEATHER_URL = "https://api.openweathermap.org/data/2.5/"

    val VWORLD_URL = "https://api.vworld.kr/req/"
    val VWORLD_KEY = BuildConfig.WORLD_API_KEY
    val request = "getfeature"
    val geomfilter = "BOX(13663271.680031825,3894007.9689600193,14817776.555251127,4688953.0631258525)"
    val size = "1000"
    val geometry = "false"
    val crs = "EPSG:3857"

    val GOCAMPING_URL = "https://apis.data.go.kr/B551011/GoCamping/"
    val numOfRows = "10000"
    val pageNo = "1"
    val MobileOS = "AND"
    val MobileApp = "campsite"
    val datatype = "json"

    val nearMaxRadius = "15000"  // 반경 15Km

    val currentLatLng: MutableStateFlow<LatLng> = MutableStateFlow(LatLng(0.0, 0.0))

    val _currentWeather: MutableStateFlow<CURRENTWEATHER_TBL?> = MutableStateFlow(null)

    val sidoListStateFlow:MutableStateFlow<List<SiDo_TBL>>  = MutableStateFlow(listOf())
    val sigunguListStateFlow:MutableStateFlow<List<SiGunGu_TBL>>  = MutableStateFlow(listOf())

    val siteImageListStateFlow:MutableStateFlow<List<SiteImage_TBL>> = MutableStateFlow(listOf())

    val siteDefaultDataStateFlow:MutableStateFlow<SiteDefaultData?> = MutableStateFlow(null)

    var currentListDataStateFlow:MutableStateFlow<List<CampSite_TBL>> = MutableStateFlow(listOf())


    suspend fun getNearCampSiteList( facltNm:String){
        database.nearCampSiteDao.select_Search_Flow(facltNm).collectLatest {
            currentListDataStateFlow.emit(it)
        }
    }

    suspend fun getCampSiteList(
        donm: Array<String>,
        sigungNm: String,
        addr1: String,
        facltNm:String
    ) {

        database.campSiteDao.select_Search_Flow(
            donm,
            sigungNm,
            addr1,
            facltNm
        ).collectLatest {
            currentListDataStateFlow.emit(it)
        }

    }


     suspend fun getCampSiteListFlowNew(
        administrativeDistrictSiDoCode:String,
        administrativeDistrictSiGunGu:String,
        searchTitle:String? = null): Int{

        val facltNm =  if(searchTitle.isNullOrEmpty()){
            "%%"
        } else {
            "%" +  searchTitle.replace(' ','%' )  + "%"
        }

        val donm = if (administrativeDistrictSiDoCode == "0"){
            emptyArray()
        } else {
            AdministrativeDistrictSiDoList.first{
                it.getCodeString() == administrativeDistrictSiDoCode
            }.getExtentionNameList()
        }

        val administrativeDistrictSiGunGuSplitArray = administrativeDistrictSiGunGu.split(" ", limit = 2)

        val sigungNm = administrativeDistrictSiGunGuSplitArray[0]

        val addr1 = if(administrativeDistrictSiGunGuSplitArray.size == 2) {
            "%" +  administrativeDistrictSiGunGuSplitArray[1] + "%"
        } else {
            "%%"
        }

         val result =  if(administrativeDistrictSiDoCode == "0"){
             // database.nearCampSiteDao.select_Search_Flow(facltNm)
             database.nearCampSiteDao.select_Search(facltNm)
         }else {
             database.campSiteDao.select_Search(
                 donm,
                 sigungNm,
                 addr1,
                 facltNm
             )
         }
         currentListDataStateFlow.emit(result)

         return result.size
    }






    suspend fun getCampSiteListFlow(
        administrativeDistrictSiDoCode:String,
        administrativeDistrictSiGunGu:String,
        searchTitle:String? = null){

        val facltNm =  if(searchTitle.isNullOrEmpty()){
            "%%"
        } else {
            "%" +  searchTitle.replace(' ','%' )  + "%"
        }

        val donm = if (administrativeDistrictSiDoCode == "0"){
            emptyArray()
        } else {
            AdministrativeDistrictSiDoList.first{
                it.getCodeString() == administrativeDistrictSiDoCode
            }.getExtentionNameList()
        }

        val administrativeDistrictSiGunGuSplitArray = administrativeDistrictSiGunGu.split(" ", limit = 2)

        val sigungNm = administrativeDistrictSiGunGuSplitArray[0]

        val addr1 = if(administrativeDistrictSiGunGuSplitArray.size == 2) {
            "%" +  administrativeDistrictSiGunGuSplitArray[1] + "%"
        } else {
            "%%"
        }

         if(administrativeDistrictSiDoCode == "0"){
             getNearCampSiteList(   facltNm)
        }else {
             getCampSiteList(
                donm,
                sigungNm,
                addr1,
                facltNm
            )
        }

    }





    fun getNearCampSiteListPaging( facltNm:String): Flow<PagingData<CampSite_TBL>> {

        return  Pager(
            config = PagingConfig(pageSize = 5, enablePlaceholders = false),
            pagingSourceFactory = {
                database.nearCampSiteDao.select_Search_Paging(facltNm)
            }
        ).flow
    }


    fun getCampSiteListPaging(
        donm: Array<String>,
        sigungNm: String,
        addr1: String,
        facltNm:String
    ): Flow<PagingData<CampSite_TBL>> {

        return  Pager(
            config = PagingConfig(pageSize = 5, enablePlaceholders = false),
            pagingSourceFactory = {
                database.campSiteDao.select_Search_Paging(
                    donm,
                    sigungNm,
                    addr1,
                    facltNm
                )
            }
        ).flow

    }



    fun getCampSiteListStream(
        administrativeDistrictSiDoCode:String,
        administrativeDistrictSiGunGu:String,
        searchTitle:String? = null):Flow<PagingData<CampSite_TBL>>{

        val facltNm =  if(searchTitle.isNullOrEmpty()){
            "%%"
        } else {
            "%" +  searchTitle.replace(' ','%' )  + "%"
        }

        val donm = if (administrativeDistrictSiDoCode == "0"){
            emptyArray()
        }  else {
            AdministrativeDistrictSiDoList.first{
                it.getCodeString() == administrativeDistrictSiDoCode
            }.getExtentionNameList()
        }

        val administrativeDistrictSiGunGuSplitArray = administrativeDistrictSiGunGu.split(" ", limit = 2)

        val sigungNm = administrativeDistrictSiGunGuSplitArray[0]

        val addr1 = if(administrativeDistrictSiGunGuSplitArray.size == 2) {
           "%" +  administrativeDistrictSiGunGuSplitArray[1] + "%"
        } else {
            "%%"
        }

        return  if(administrativeDistrictSiDoCode == "0"){
            getNearCampSiteListPaging(   facltNm)
        }else {

            getCampSiteListPaging(
                donm,
                sigungNm,
                addr1,
                facltNm
            )
        }

    }





    suspend fun setCurrentLatLng(data:LatLng){
        currentLatLng.emit(data)
    }

    suspend fun getSiteImageList(contentId:String){
        database.siteimageDao.select_Flow(contentId).collectLatest {
            siteImageListStateFlow.emit(it)
        }
    }


    suspend fun getSiDoList() {
        database.sidoDao.select_All_Flow().collectLatest {
            sidoListStateFlow.emit(it)
        }
    }

    suspend fun getSiGunGuList(upCode:String) {
        database.sigunguDao.select_Flow(upCode = upCode, length = upCode.length).collectLatest {
            sigunguListStateFlow.emit(it)
        }
    }


    suspend fun getCampSiteData(contentId: String) {
        database.campSiteDao.select_Flow(contentId).collectLatest {
            siteDefaultDataStateFlow.emit(CampSite_TBL.toSiteDefaultData(it))
        }
    }



    suspend fun getWeatherData(latitude: String, longitude: String){

        val OPENWEATHER_KEY = BuildConfig.OPENWEATHER_KEY
        val OPENWEATHER_UNITS = "metric"

        val service = RetrofitAdapter.create( service = OpenWeatherInterface::class.java, url = OPENWEATHER_URL)

        val apiResponse = service.getWeatherData(
            latitude = latitude,
            longitude = longitude,
            units = OPENWEATHER_UNITS,
            apiKey = OPENWEATHER_KEY
        )
        _currentWeather.emit(apiResponse.toCURRENTWEATHER_TBL())
    }


    fun chkCollectTime(type: CollectType): Boolean {

        val intervalTime = when(type){
            CollectType.LT_C_ADSIDO_INFO -> 86400000
            CollectType.LT_C_ADSIGG_INFO -> 86400000
            CollectType.CAMPSITE ->  86400000
            CollectType.NEARCAMPSITE -> 0
            CollectType.SITEIMAGE -> 0
            CollectType.SYNC -> 86400000
        }

        val beforeCollectTime = database.collectTimeDao.select(type.name)
        return if (beforeCollectTime + intervalTime <= System.currentTimeMillis()) true else false
    }

    suspend fun updateCollectTime(type:String){
        database.collectTimeDao.update(
            type = type,
            time = System.currentTimeMillis()
        )
    }

    suspend fun recvVWorldData(serviceType: VWorldService){

        if( CollectTypeList.find {
                it.name == serviceType.name}?.let { chkCollectTime(it) } == true  ) {

            val service = RetrofitAdapter.create(service = VWorldInterface::class.java, url = VWORLD_URL)

            val apiResponse: VWorldResponse = service.recvVWORLD(
                apiKey = VWORLD_KEY,
                request = request,
                data = serviceType.name,
                geomfilter = geomfilter,
                size = size,
                geometry = geometry,
                crs = crs
            )

            when (serviceType) {
                VWorldService.LT_C_ADSIDO_INFO -> {
                    val resultList: MutableList<SiDo_TBL> = mutableListOf()
                    apiResponse.response.result?.featureCollection?.features?.forEach {
                        resultList.add(
                            SiDo_TBL(
                                ctprvn_cd = it.properties.ctprvn_cd ?: "",
                                ctp_kor_nm = it.properties.ctp_kor_nm ?: "",
                                ctp_eng_nm = it.properties.ctp_eng_nm ?: ""
                            )
                        )
                    }


                    database.withTransaction {
                        database.sidoDao.trancate()
                        database.sidoDao.insert_List(resultList)
                        updateCollectTime(serviceType.name)
                        /*
                        database.collectTimeDao.update(
                            type = serviceType.name,
                            time = System.currentTimeMillis()
                        )

                         */
                        /****
                        Transaction 내에  동일한 테이블을 로드하는 Flow 를 넣으면  Transaction 이 종료 되지 않음 (무한 루프)
                        getSiDoList()
                         ***/
                    }
                }

                VWorldService.LT_C_ADSIGG_INFO -> {
                    val resultList: MutableList<SiGunGu_TBL> = mutableListOf()
                    apiResponse.response.result?.featureCollection?.features?.forEach {
                        resultList.add(
                            SiGunGu_TBL(
                                full_nm = it.properties.full_nm ?: "",
                                sig_cd = it.properties.sig_cd ?: "",
                                sig_kor_nm = it.properties.sig_kor_nm ?: "",
                                sig_eng_nm = it.properties.sig_eng_nm ?: ""
                            )
                        )
                    }
                    database.withTransaction {
                        database.sigunguDao.trancate()
                        database.sigunguDao.insert_List(resultList)
                        updateCollectTime(serviceType.name)
                        /*
                        database.collectTimeDao.update(
                            type = serviceType.name,
                            time = System.currentTimeMillis()
                        )

                         */
                    }
                }
            }
        }

    }

    suspend fun recvGoCampingData(
        serviceType:GoCampingService,
        mapX:String? = null,
        mapY:String?= null,
        keyword:String? = null,
        contentId:String? = null
    ) {

        if( CollectTypeList.find {
                it.name == serviceType.name}?.let { chkCollectTime(it) } == true  ) {

            val serviceKey  = withContext(Dispatchers.IO) {
                URLDecoder.decode(BuildConfig.GOCAMPING_API_KEY, "UTF-8")
            }

            val service = RetrofitAdapter.create(
                service = GoCampingInterface::class.java,
                url = GOCAMPING_URL
            )

            try {

                val apiResponse: Any = when (serviceType) {
                    GoCampingService.CAMPSITE -> {
                        service.getDefault(
                            serviceKey = serviceKey,
                            numOfRows = numOfRows,
                            pageNo = pageNo,
                            MobileOS = MobileOS,
                            MobileApp = MobileApp,
                            _type = datatype
                        )
                    }
                    GoCampingService.NEARCAMPSITE -> {
                        if ( !mapX.isNullOrEmpty() && !mapY.isNullOrEmpty()) {
                            service.getLocation(
                                serviceKey = serviceKey,
                                numOfRows = numOfRows,
                                pageNo = pageNo,
                                MobileOS = MobileOS,
                                MobileApp = MobileApp,
                                _type = datatype,
                                mapX = mapX,
                                mapY = mapY,
                                radius = nearMaxRadius
                            )
                        }else {}

                    }
                    GoCampingService.SYNC -> {
                        service.getSync(
                            serviceKey = serviceKey,
                            numOfRows = numOfRows,
                            pageNo = pageNo,
                            MobileOS = MobileOS,
                            MobileApp = MobileApp,
                            _type = datatype,
                            syncModTime = UnixTimeToString( (System.currentTimeMillis() ), yyyyMMdd)
                        )
                    }
                    GoCampingService.SITEIMAGE -> {

                        if (!contentId.isNullOrEmpty()) {
                            service.getImage(
                                serviceKey = serviceKey,
                                numOfRows = numOfRows,
                                pageNo = pageNo,
                                MobileOS = MobileOS,
                                MobileApp = MobileApp,
                                _type = datatype,
                                contentId = contentId
                            )
                        } else {

                        }

                    }
                    GoCampingService.SEARCH -> {
                        if(!keyword.isNullOrEmpty()){
                            service.getSearch(
                                serviceKey = BuildConfig.GOCAMPING_API_KEY,
                                numOfRows = numOfRows ,
                                pageNo = pageNo,
                                MobileOS = MobileOS,
                                MobileApp = MobileApp,
                                _type = datatype,
                                keyword = keyword
                            )
                        } else {

                        }
                    }
                }

                val resultStatus = GoCampingResponseStatusList.first {
                    it.getDesc().first == when(serviceType){
                        GoCampingService.CAMPSITE,
                        GoCampingService.NEARCAMPSITE,
                        GoCampingService.SEARCH ,
                        GoCampingService.SYNC-> {
                            (apiResponse as GoCampingResponse).response?.header?.resultCode
                        }
                        GoCampingService.SITEIMAGE -> {
                            (apiResponse as GoCampingResponseImage).response?.header?.resultCode
                        }
                    }
                }

               /*
                val resultStatus = GoCampingResponseStatusList.first {
                    it.getDesc().first == (apiResponse as GoCampingResponse).response?.header?.resultCode
                }

                */

                when (resultStatus) {

                    GoCampingResponseStatus.OK -> {

                        when (serviceType) {

                            GoCampingService.CAMPSITE -> {
                                val resultList: MutableList<CampSite_TBL> = mutableListOf()
                                (apiResponse as GoCampingResponse).response?.body?.items?.item?.forEach {
                                    resultList.add(
                                        GoCampingRecvItem.toCampSite_TBL(it)
                                    )
                                }

                                database.withTransaction {
                                    database.campSiteDao.trancate()
                                    database.campSiteDao.insert_List(resultList)
                                    updateCollectTime(serviceType.name)
                                }
                            }
                            GoCampingService.NEARCAMPSITE -> {
                                val resultList: MutableList<NearCampSite_TBL> = mutableListOf()
                                (apiResponse as GoCampingResponse).response?.body?.items?.item?.forEach {
                                    resultList.add(
                                        GoCampingRecvItem.toNearCampSite_TBL(it)
                                    )
                                }

                                database.withTransaction {
                                    database.nearCampSiteDao.trancate()
                                    database.nearCampSiteDao.insert_List(resultList)
                                    updateCollectTime(serviceType.name)
                                }


                            }
                            GoCampingService.SYNC -> {

                                val resultUpdateList: MutableList<CampSite_TBL> = mutableListOf()
                                val resultAppendList: MutableList<CampSite_TBL> = mutableListOf()
                                val resultDeleteList: MutableList<String> = mutableListOf()
                        //        val resultUnknownList: MutableList<CampSite_TBL> = mutableListOf()

                                (apiResponse as GoCampingResponse).response?.body?.items?.item?.forEach {
                                    when (it.syncStatus) {
                                        GoCampingSyncStatus.A.name -> {
                                            resultAppendList.add(
                                                GoCampingRecvItem.toCampSite_TBL(it)
                                            )
                                        }

                                        GoCampingSyncStatus.U.name -> {
                                            resultUpdateList.add(
                                                GoCampingRecvItem.toCampSite_TBL(it)
                                            )
                                        }

                                        GoCampingSyncStatus.D.name -> {
                                            resultDeleteList.add(
                                                it.contentId
                                            )
                                        }

                                        else -> {
                                            /*
                                            resultUnknownList.add(
                                                GoCampingRecvItem.toCampSite_TBL(it)
                                            )

                                             */
                                        }
                                    }
                                }

                                database.withTransaction {
                                    database.campSiteDao.insert_List(resultAppendList)
                                }

                                database.withTransaction {
                                    resultUpdateList.forEach {
                                        database.campSiteDao.delete(contentId = it.contentId)
                                    }
                                    database.campSiteDao.insert_List(resultUpdateList)
                                }

                                database.withTransaction {
                                    resultDeleteList.forEach {
                                        database.campSiteDao.delete(contentId = it)
                                    }
                                }
                                database.withTransaction {
                                    updateCollectTime(serviceType.name)
                                }

                            }
                            GoCampingService.SITEIMAGE -> {
                                val resultList: MutableList<SiteImage_TBL> = mutableListOf()
                                (apiResponse as GoCampingResponseImage).response?.body?.items?.item?.forEach {
                                    resultList.add(
                                        SiteImage_TBL(
                                            contentId = it.contentId,
                                            createdtime = it.createdtime,
                                            imageUrl = it.imageUrl,
                                            modifiedtime = it.modifiedtime,
                                            serialnum = it.serialnum
                                        )
                                    )
                                }

                                database.withTransaction {
                                    database.siteimageDao.trancate()
                                    database.siteimageDao.insert_List(resultList)
                                }

                            }
                            GoCampingService.SEARCH -> {}

                        }
                    }

                    else -> {}
                }


            } catch (e: Exception){
                val errMsg = e.localizedMessage
            }

        }

    }



}

