package com.unchil.searchcamp.model

import com.google.gson.annotations.SerializedName



data class Service (
    @SerializedName("name") var name:String,
    @SerializedName("version") var version:String,
    @SerializedName("operation") var operation:String,
    @SerializedName("time") var time:String
)

data class Record (
    @SerializedName("total") var total:String,
    @SerializedName("current") var current:String
)

data class Page (
    @SerializedName("total") var total:String,
    @SerializedName("current") var current:String,
    @SerializedName("size") var size:String
)

data class Properties (
    @SerializedName("ctprvn_cd") var ctprvn_cd:String?,
    @SerializedName("ctp_kor_nm") var ctp_kor_nm:String?,
    @SerializedName("ctp_eng_nm") var ctp_eng_nm:String?,

    @SerializedName("full_nm") var full_nm:String?,

    @SerializedName("sig_cd") var sig_cd:String?,
    @SerializedName("sig_kor_nm") var sig_kor_nm:String?,
    @SerializedName("sig_eng_nm") var sig_eng_nm:String?,

    @SerializedName("emd_cd") var emd_cd:String?,
    @SerializedName("emd_kor_nm") var emd_kor_nm:String?,
    @SerializedName("emd_eng_nm") var emd_eng_nm:String?,

    @SerializedName("li_cd") var li_cd:String?,
    @SerializedName("li_kor_nm") var li_kor_nm:String?,
    @SerializedName("li_eng_nm") var li_eng_nm:String?
)

data class Feature(
    @SerializedName("type") var type:String,
    @SerializedName("properties") var properties:Properties,
    @SerializedName("id") var id:String
)

data class  FeatureCollection (
    @SerializedName("type") var type:String,
    @SerializedName("bbox") var bbox:Array<Float>,
    @SerializedName("features") var features:Array<Feature>
)

data class Result (
    @SerializedName("featureCollection") var featureCollection:FeatureCollection
)

data class Error(
    @SerializedName("level") var level:String,
    @SerializedName("code") var code:String,
    @SerializedName("text") var text:String
)

data class VWorldRecvData (
    @SerializedName("service") var service:Service,
    @SerializedName("status") var status:String,
    @SerializedName("record") var record:Record,
    @SerializedName("page") var page:Page,
    @SerializedName("result") var result:Result?,
    @SerializedName("error") var error:Error?
)

data class VWorldResponse(
    @SerializedName("response") var response:VWorldRecvData
)



