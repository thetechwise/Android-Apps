package com.example.testapp

class Users {
    var profilepic: String? = null
    var mail: String? = null
    var userName: String? = null
    var password: String? = null
    var userId: String? = null
    var lastMessage: String? = null
    var status: String? = null

    constructor()

    constructor(
        userId: String?,
        userName: String?,
        maill: String?,
        password: String?,
        profilepic: String?,
        status: String?
    ) {
        this.userId = userId
        this.userName = userName
        this.mail = maill
        this.password = password
        this.profilepic = profilepic
        this.status = status
    }
}
