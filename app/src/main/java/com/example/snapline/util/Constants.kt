package com.example.snapline.util

import androidx.datastore.preferences.core.stringPreferencesKey

object Constants {

    const val SPLASH_SCREEN_DURATION = 1500L
    const val USER_DATASTORE_NAME = "user_data_store"
    const val STORY_DATABASE_NAME = "story_db"
    val NAME_KEY = stringPreferencesKey("name")
    val EMAIL_KEY = stringPreferencesKey("email")
    val TOKEN_KEY = stringPreferencesKey("token")
}