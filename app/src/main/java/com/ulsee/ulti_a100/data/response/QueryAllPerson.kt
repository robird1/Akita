package com.ulsee.ulti_a100.data.response

data class QueryAllPerson(
    val command: Int,
    val `data`: List<AllPerson>,
    val detail: String,
    val status: Int,
    val transmit_cast: Int
)

data class AllPerson(
    val accessInfo: AccessInfo2,
    val address: String,
    val age: Int,
    val certificateNumber: String,
    val certificateType: Int,
    val email: String,
    val gender: String,
    val groupId: String,
    val name: String,
    val personId: String,
    val phone: String,
    val updateTime: String,
    val userId: String,
    var checked: Boolean,          // set by the application, not from the original response
    var faceImg: String       // set by the application, not from the original response
)

data class AccessInfo2(
    val authType: Int,
    val cardNum: String,
    val password: String,
    val roleType: Int
)