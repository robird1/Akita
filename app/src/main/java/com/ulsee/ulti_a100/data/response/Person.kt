package com.ulsee.ulti_a100.data.response

data class Person(
    val accessInfo: AccessInfo,
    val age: Int,
    val certificateNumber: String,
    val certificateType: Int,
    val email: String,
    val gender: String,
    val groupId: String,
    val images: List<Image>,
    val name: String,
    val personId: String,
    val phone: String,
    val updateTime: String,
    val userId: String
)

data class AccessInfoPerson(
    val authType: Int,
    val cardNum: String,
    val password: String,
    val roleType: Int
)

data class Image(
    val faceId: String,
    val orgimg: String
)