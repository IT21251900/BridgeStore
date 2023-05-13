package com.example.bridgestore.model

import com.google.firebase.Timestamp

data class Message(
    var id:String? = null,
    val text: String = "",
    val senderId: String = "",
    val timestamp: Timestamp? = null
)
