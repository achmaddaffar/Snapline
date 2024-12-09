package com.example.snapline.data.local.ds

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.snapline.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object DataStoreConfig {
    @Volatile
    private var INSTANCE : DataStore<Preferences>? = null

    @JvmStatic
    fun getInstance(context: Context): DataStore<Preferences> {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: run {
                PreferenceDataStoreFactory.create(
                    corruptionHandler = ReplaceFileCorruptionHandler(
                        produceNewData = { emptyPreferences() }
                    ),
                    migrations = listOf(SharedPreferencesMigration(context, Constants.USER_DATASTORE_NAME)),
                    scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                    produceFile = { context.preferencesDataStoreFile(Constants.USER_DATASTORE_NAME) }
                )
            }
        }
    }
}