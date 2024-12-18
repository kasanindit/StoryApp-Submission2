package com.dicoding.picodiploma.storyappdicoding.data.pref

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoryPreference (private val dataStore: DataStore<Preferences>) {

    suspend fun saveLastFetchedTime(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_FETCHED_KEY] = timestamp
        }
    }

    fun getLastFetchedTime(): Flow<Long> = dataStore.data.map { preferences ->
        preferences[LAST_FETCHED_KEY] ?: 0L
    }

    companion object {
        private val LAST_FETCHED_KEY = longPreferencesKey("last_fetched_time")

        @Volatile
        private var instance: StoryPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): StoryPreference {
            return instance ?: synchronized(this) {
                instance ?: StoryPreference(dataStore)
            }.also { instance = it }
        }
    }
}