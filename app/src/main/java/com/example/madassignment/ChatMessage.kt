package com.example.madassignment

class ChatMessage {
    var userId: String? = null
        private set
    var displayName: String? = null
        private set
    var message: String? = null
        private set
    var timestamp: Long = 0
        private set

    // Required default constructor for Firestore
    constructor()

    constructor(userId: String?, displayName: String?, message: String?, timestamp: Long) {
        this.userId = userId
        this.displayName = displayName
        this.message = message
        this.timestamp = timestamp
    }
}
