package com.ulsee.ulti_a100.data.response

data class GetTime(
    val command: Int,
    val `data`: Data3,
    val detail: String,
    val status: Int,
    val transmit_cast: Int
)

data class Data3(
    val day: Int,
    val hour: Int,
    val min: Int,
    val month: Int,
    val sec: Int,
    val usec: Int,
    val year: Int
)