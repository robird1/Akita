package com.ulsee.ulti_a100.data.response

data class AllPerson(
    val `data`: List<Data>,
    val detail: String,
    val status: Int
)

data class Data(
    val accessInfo: AccessInfo,
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
    val userId: String
)

data class AccessInfo(
    val authType: Int,
    val cardNum: String,
    val password: String,
    val roleType: Int
)