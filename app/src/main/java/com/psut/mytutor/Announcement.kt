package com.psut.mytutor

data class Announcement(
    val topic: Topic = Topic(),
    val userName: String = "",
    val userID: String = ""
)
