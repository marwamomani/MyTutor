package com.psut.mytutor

data class UserData(
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val major: String = "",
    val password: String = "",
    val isAdmin: Boolean = false,
    val userUID: String = "",
    val rates: MutableList<String> = mutableListOf(),
    val rateAverage: String = "0.0",
    val topics: MutableList<Topic> = mutableListOf()
)
