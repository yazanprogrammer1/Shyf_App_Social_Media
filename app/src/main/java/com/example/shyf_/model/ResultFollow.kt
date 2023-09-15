package com.example.shyf_.model

import com.google.gson.annotations.SerializedName

class ResultFollow(
    @SerializedName("error")
    public var error: Boolean? = null,

    @SerializedName("message")
    public val message: String? = null,

    @SerializedName("user")
    public val followers: Followers? = null
)