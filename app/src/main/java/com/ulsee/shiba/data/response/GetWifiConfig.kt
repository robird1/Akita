package com.ulsee.shiba.data.response

data class GetWifiConfig(
    val command: Int,
    val `data`: Data5,
    val detail: String,
    val status: Int,
    val transmit_cast: Int
)

data class Data5(
    val wifiConfig: WifiConfig
)

data class WifiConfig(
    val ap: Ap,
    val mode: String,
    val sta: Sta
)

data class Ap(
    val DefaultGateway: String,
    val MACAddress: String,
    val ipAddress: String,
    val password: String,
    val ssid: String,
    val subnetMask: String
)

data class Sta(
    val DefaultGateway: String,
    val MACAddress: String,
    val dhcp: String,
    val ipAddress: String,
    val password: String,
    val ssid: String,
    val status: Int,
    val subnetMask: String
)