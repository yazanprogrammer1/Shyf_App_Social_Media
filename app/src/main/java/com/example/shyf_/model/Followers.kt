package com.example.shyf_.model

import com.google.gson.annotations.SerializedName

class Followers(
    @SerializedName("id") var id: Int? = null,

    @SerializedName("idUser") val idUser: Int? = null,

    @SerializedName("idUserFollow") val idUserFollow: Int? = null,

    @SerializedName("userId") var userId: Int? = null,

    @SerializedName("nameUser") val nameUser: String? = null,

    @SerializedName("imageUser") val imageUser: String? = null,

    @SerializedName("BackgroundImage") var BackgroundImage: String? = null,

    @SerializedName("emailUser") val emailUser: String? = null,

    @SerializedName("WhatsappLink") val WhatsappLink: String? = null,
    @SerializedName("FacebookLink") val FacebookLink: String? = null,
    @SerializedName("Information") val Information: String? = null,
    @SerializedName("numFollowers") val numFollowers: Int? = null,
    @SerializedName("numFollowing") val numFollowing: Int? = null
)