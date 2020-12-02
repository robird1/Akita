package com.ulsee.shiba.data.response

data class SetWifiConfig(
    val command: Int,
    val detail: String,
    val status: Int,
    val transmit_cast: Int
)