package com.example.shyf_.model

import java.io.Serializable

class UserChats : Serializable {
    var id: String? = null
    var nameUser: String? = null
    var email: String? = null
    var password: String? = null
    var ImgUrl: String? = null
    var status: String? = null
    var userToken: String? = null
    var whatsappLink: String? = null
    var facebookLink: String? = null
    var information: String? = null
    var timestamp: Long? = null

    constructor() {}
    constructor(
        id: String?,
        nameUser: String?,
        email: String?,
        password: String?,
        ImgUrl: String?,
        status: String?,
        userToken: String?,
        whatsappLink: String?,
        facebookLink: String?,
        information: String?,
        timestamp: Long?
    ) {
        this.id = id
        this.nameUser = nameUser
        this.email = email
        this.password = password
        this.ImgUrl = ImgUrl
        this.status = status
        this.userToken = userToken
        this.whatsappLink = whatsappLink
        this.facebookLink = facebookLink
        this.information = information
        this.timestamp = timestamp
    }


}