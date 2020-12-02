package com.ulsee.shiba.data.response

data class ModifyPerson(
    val command: Int,
    val detail: String,
    val status: Int,
    val transmit_cast: Int
)