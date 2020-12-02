package com.ulsee.shiba.data.response

data class ClearAttendRecord(
    val command: Int,
    val detail: String,
    val status: Int,
    val transmit_cast: Int
)