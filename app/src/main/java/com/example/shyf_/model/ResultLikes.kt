package com.example.shyf_.model

import com.google.gson.annotations.SerializedName

class ResultLikes(
    @SerializedName("error")
    public var error: Boolean? = null,

    @SerializedName("message")
    public val message: String? = null
)