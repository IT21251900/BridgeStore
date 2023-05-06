package com.example.bridgestore.model

data class ChatMessage(
    var id:String?,
    val senderId: String = "",
    val recipientId: String = "",
    val message: String = "",
    val timestamp: Long = 0
)
