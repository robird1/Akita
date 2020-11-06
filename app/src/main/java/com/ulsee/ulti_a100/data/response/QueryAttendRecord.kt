package com.ulsee.ulti_a100.data.response

data class GetAttendRecordCount(
    val command: Int,
    val detail: String,
    val startId: Int,
    val status: Int,
    val totalCount: Int,
    val transmit_cast: Int
)

data class QueryAttendRecord(
    val command: Int,
    val data: List<AttendRecord>?,
    val detail: String,
    val recordCount: Int,
    val status: Int,
    val transmit_cast: Int
)

data class AttendRecord(
    val address: String,
    val age: Int,
    val birth: String,
    val bodyTemperature: String,
    val certificateNumber: String,
    val certificateType: Int,
    val country: String,
    val deviceid: String,
    val email: String,
    val gender: String,
    val groupId: String,
    val id: Int,
    val img: String,
    val name: String,
    val nation: String,
    val personId: String,
    val phone: String,
    val respirator: Int,
    val timestamp: String,
    val userid: String
)