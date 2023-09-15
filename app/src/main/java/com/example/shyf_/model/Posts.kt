package com.example.shyf_.model

import com.google.gson.annotations.SerializedName

class Posts(
    @SerializedName("id") var id: Int? = null,

    @SerializedName("country") val country: String? = null,

    @SerializedName("city") val city: String? = null,

    @SerializedName("street") var street: String? = null,

    @SerializedName("description") val description: String? = null,

    @SerializedName("price") val price: Double? = null,

    @SerializedName("timeAdd") val TimeAdd: String? = null,

    @SerializedName("image") val image: String? = null,
    @SerializedName("userId") val userId: Int? = null,
    @SerializedName("numLikes") var numLikes: Int? = null,
    @SerializedName("numComments") val numComments: Int? = null,


    @SerializedName("error_post")
    var error_post: Boolean = false,
    @SerializedName("message_post")
    var message_post: String? = null,


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