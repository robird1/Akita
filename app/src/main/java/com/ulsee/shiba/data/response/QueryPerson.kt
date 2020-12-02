package com.ulsee.shiba.data.response

data class QueryPerson(
    val command: Int,
    val `data`: Data,
    val detail: String,
    val status: Int,
    val transmit_cast: Int
)

data class Data(
    val accessInfo: AccessInfo,
    val address: String,
    val age: Int,
    val certificateNumber: String,
    val certificateType: Int,
    val email: String,
    val faces: List<Face>,
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

data class Face(
    val faceId: String,
    val orgimg: String
)