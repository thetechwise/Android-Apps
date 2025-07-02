package com.example.testapp

class msgModelclass {
    var message: String? = null
    var senderid: String? = null
    var timeStamp: Long = 0

    constructor()

    constructor(message: String?, senderid: String?, timeStamp: Long) {
        this.message = message
        this.senderid = senderid
        this.timeStamp = timeStamp
    }
}
