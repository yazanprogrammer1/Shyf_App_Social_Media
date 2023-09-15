package com.example.shyf_.model

import com.google.gson.annotations.SerializedName

class Result_AddStory(
    @SerializedName("error")
    public var error: Boolean? = null,

    @SerializedName("message")
    public val message: String? = null,

    @SerializedName("story")
    public val story: Storys? = null
)