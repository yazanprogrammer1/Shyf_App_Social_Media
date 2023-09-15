package com.example.shyf_.model

import com.google.gson.annotations.SerializedName

class Result_AddRequest(
    @SerializedName("error")
    public var error: Boolean? = null,

    @SerializedName("message")
    public val message: String? = null,

    @SerializedName("user")
    public val Request: Request? = null
)