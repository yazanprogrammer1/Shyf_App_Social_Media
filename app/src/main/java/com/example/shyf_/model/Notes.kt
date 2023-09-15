package com.example.shyf_.model

import com.google.gson.annotations.SerializedName

class Notes(
    @SerializedName("id") var id: Int? = null,

    @SerializedName("note") val note: String? = null,

    @SerializedName("userId") val userId: String? = null,

    @SerializedName("error_notes") var error_notes: Boolean = false,
    @SerializedName("message_notes") var message_notes: String? = null,


    @SerializedName("idUser") var idUser: Int? = null,

    @SerializedName("nameUser") val nameUser: String? = null,

    @SerializedName("imageUser") val imageUser: String? = null,

    @SerializedName("BackgroundImage") var BackgroundImage: String? = null,

    @SerializedName("emailUser") val emailUser: String? = null,

    @SerializedName("WhatsappLink") val WhatsappLink: String? = null,
    @SerializedName("FacebookLink") val FacebookLink: String? = null,
    @SerializedName("Information") val Information: String? = null,
    @SerializedName("numFollowers") val numFollowers: Int? = null,
    @SerializedName("numFollowing") val numFollowing: Int? = null,
    @SerializedName("userToken") val userToken: String? = null
)