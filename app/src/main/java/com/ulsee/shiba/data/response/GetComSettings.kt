package com.ulsee.shiba.data.response

data class GetComSettings(
    val command: Int,
    val `data`: Data4,
    val detail: String,
    val status: Int,
    val transmit_cast: Int
)

data class Data4(
    val commonSettings: CommonSettings
)

data class CommonSettings(
    val attendInterval: Int,
    val attrInterval: Int,
    val attributeRecog: String,
    val attributeTrack: String,
    val countAlgorithm: String,
    val debugLevel: Int,
    val drawTrackRect: String,
    val enableStoreAttendLog: String,
    val enableStoreStrangerAttLog: String,
    val faceAEEnabled: String,
    val hacknessThreshold: Double,
    val idReaderLinkMode: String,
    val normalLiveness: String,
    val recogBodyTemperature: String,
    val recogInterval: Int,
    val recogRespirator: String,
    val recogThreshold: Double,
    val scoringInterval: Int,
    val showBodyTemperatureImg: String,
    val thermalImaging: String
)