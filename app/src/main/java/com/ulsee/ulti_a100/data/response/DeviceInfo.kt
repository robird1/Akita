package com.ulsee.ulti_a100.data.response

import com.google.gson.annotations.SerializedName

data class DeviceInfo(
    @field:SerializedName("status") val status: Int,
    @field:SerializedName("detail") val detail: String,
    @field:SerializedName("data") val data: Info)

data class Info (
    var chipid: String = "",
    var devkey : String = "",
    var distance : String = "",
    var lic : String = "",
    var mac : String = "",
    var pid: String = "",
    var productsn: String = "",
    var sn: String = "",
    var tyLic: String = "",
    var tyPadLic: String = ""
)