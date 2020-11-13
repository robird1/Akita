package com.ulsee.ulti_a100.data.response

data class AddFaces(
    val command: Int,
    val detail: String,
    val status: Int,
    val transmit_cast: Int
)