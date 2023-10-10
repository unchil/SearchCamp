package com.unchil.searchcamp.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.unchil.searchcamp.model.SiteDefaultData

@Entity(tableName = "CampSite_TBL")
data class CampSite_TBL (
    @PrimaryKey(autoGenerate = false)
    var contentId:String,
    var addr1:String,
    var addr2:String,
    var allar:String,
    var animalCmgCl:String,
    var autoSiteCo:String,
    var bizrno:String,
    var brazierCl:String,
    var caravAcmpnyAt:String,
    var caravInnerFclty:String,
    var caravSiteCo:String,
    var clturEvent:String,
    var clturEventAt:String,

    var createdtime:String,
    var direction:String,
    var doNm:String,
    var eqpmnLendCl:String,
    var exprnProgrm:String,
    var exprnProgrmAt:String,
    var extshrCo:String,
    var facltDivNm:String,
    var facltNm:String,
    var featureNm:String,
    var fireSensorCo:String,
    var firstImageUrl:String,
    var frprvtSandCo:String,
    var frprvtWrppCo:String,
    var glampInnerFclty:String,
    var glampSiteCo:String,
    var gnrlSiteCo:String,
    var homepage:String,
    var hvofBgnde:String,
    var hvofEnddle:String,
    var induty:String,
    var indvdlCaravSiteCo:String,
    var insrncAt:String,
    var intro:String,
    var lctCl:String,
    var lineIntro:String,
    var manageNmpr:String,
    var manageSttus:String,
    var mangeDivNm:String,
    var mapX:String,
    var mapY:String,
    var mgcDiv:String,
    var modifiedtime:String,
    var operDeCl:String,
    var operPdCl:String,
    var posblFcltyCl:String,
    var posblFcltyEtc:String,
    var prmisnDe:String,
    var resveCl:String,
    var resveUrl:String,
    var sbrsCl:String,
    var sbrsEtc:String,
    var sigunguNm:String,
    var siteBottomCl1:String,
    var siteBottomCl2:String,
    var siteBottomCl3:String,
    var siteBottomCl4:String,
    var siteBottomCl5:String,
    var sitedStnc:String,
    var siteMg1Co:String,
    var siteMg1Vrticl:String,
    var siteMg1Width:String,
    var siteMg2Co:String,
    var siteMg2Vrticl:String,
    var siteMg2Width:String,
    var siteMg3Co:String,
    var siteMg3Vrticl:String,
    var siteMg3Width:String,
    var swrmCo:String,
    var tel:String,
    var themaEnvrnCl:String,
    var toiletCo:String,
    var tooltip:String,
    var tourEraCl:String,
    var trlerAcmpnyAt:String,
    var trsagntNo:String,
    var wtrplCo:String,
    var zipcode:String


){
    companion object {

        fun toSiteDefaultData(self:CampSite_TBL): SiteDefaultData {

            val latitude:Double =  self.mapY.toDouble()
            val longitude:Double = self.mapX.toDouble()

            return SiteDefaultData(contentId= self.contentId, facltNm =  self.facltNm  ,  lineIntro = self.lineIntro ,
            intro = self.intro , lctCl= self.lctCl , animalCmgCl= self.animalCmgCl ,
            glampInnerFclty= self.glampInnerFclty , sbrsCl= self.sbrsCl , eqpmnLendCl= self.eqpmnLendCl ,
            resveCl= self.resveCl , resveUrl= self.resveUrl , tel= self.tel , facltDivNm= self.facltDivNm ,
            induty= self.induty , doNm= self.doNm , sigunguNm= self.sigunguNm , addr1= self.addr1 ,
            firstImageUrl= self.firstImageUrl , homepage= self.homepage ,
            latitude= latitude , longitude= longitude
            )

        }

    }
}