package com.example.snapline.data.util

import com.example.snapline.data.remote.response.ErrorResponse
import com.google.gson.Gson

class GlobalErrorParser(
    private val gson: Gson
) {

    fun parse(errorString: String?): String? {
        val errorBody =  gson.fromJson(errorString, ErrorResponse::class.java)
        return errorBody.message
    }
}