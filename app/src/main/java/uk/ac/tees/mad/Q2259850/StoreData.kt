package uk.ac.tees.mad.Q2259850

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoreData(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("storeData")
        val USERNAME = stringPreferencesKey("store_email")
        val NAME = stringPreferencesKey("store_name")
        val ID = stringPreferencesKey("store_id")

    }

    val getId: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[ID] ?: ""
        }
    val getUsername: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USERNAME] ?: ""
        }
    val getName: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[NAME] ?: ""
        }
    suspend fun saveUsername(username: String) {
        context.dataStore.edit { preferences ->
            preferences[USERNAME] = username
        }
    }
    suspend fun saveName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[NAME] = name
        }
    }
    suspend fun saveId(id: String) {
        context.dataStore.edit { preferences ->
            preferences[ID] = id
        }
    }
}