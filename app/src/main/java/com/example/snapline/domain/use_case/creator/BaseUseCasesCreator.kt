package com.example.snapline.domain.use_case.creator

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

abstract class BaseUseCasesCreator {
    abstract fun create(
        context: Context,
        dataStore: DataStore<Preferences>
    ): BaseUseCases
}