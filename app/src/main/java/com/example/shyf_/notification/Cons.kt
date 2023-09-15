package com.example.shyf_.notification


object Cons {
    private const val remote_msg_authorization = "Authorization"
    private const val content_type = "Content-Type"
    var remoteData = "data"
    var registeration_id = "registration_ids"
    var hashMap: HashMap<String, String>? = null

    fun getHeader(): HashMap<String, String>? {
        if (hashMap == null) {
            hashMap = java.util.HashMap()
            hashMap!![Cons.remote_msg_authorization] =
                "key=AAAA7a0kVB0:APA91bEXboeXkOI1qvGjk3qpbCz2wmE68ZRD7hHMR6UgBNmDhcvtfmFiAIIdgsfa8Um3pLjjGe9Eno_BRoVUrwSwCqSo7XlJSfZYUgNHrUa1uf8pDeZV1BlcASJtgZRV07vPnHvaPCb5"
            hashMap!![content_type] = "application/json"
        }
        return hashMap
    }
}