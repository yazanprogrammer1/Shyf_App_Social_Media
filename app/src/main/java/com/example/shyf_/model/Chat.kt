package com.example.shyf_.model

class Chat {
    var sender: String? = null
    var reciever: String? = null
    var message: String? = null
    var date: String? = null
    var seen = false
    var timestamp: Long = 0
    var lastTimeMessage:String = ""


    constructor() {}
    constructor(
        sender: String?,
        reciever: String?,
        message: String?,
        date: String?,
        seen: Boolean,
        timestamp: Long,
        lastTimeMessage: String
    ) {
        this.sender = sender
        this.reciever = reciever
        this.message = message
        this.date = date
        this.seen = seen
        this.timestamp = timestamp
        this.lastTimeMessage = lastTimeMessage
    }


}