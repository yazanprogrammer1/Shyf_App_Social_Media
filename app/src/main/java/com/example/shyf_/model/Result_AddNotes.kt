package com.example.shyf_.model

import com.google.gson.annotations.SerializedName

class Result_AddNotes(
    @SerializedName("error")
    public var error: Boolean? = null,

    @SerializedName("message")
    public val message: String? = null,

    @SerializedName("user")
    public val Notes: Notes? = null
)