package com.example.taskflow_android.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "taskdays_preferences")

class SessionManager(private val context: Context) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val REMEMBER_ME_KEY = booleanPreferencesKey("remember_me")
        private val THEME_COLOR_KEY = stringPreferencesKey("theme_color")
        private val PROFILE_IMAGE_URI_KEY = stringPreferencesKey("profile_image_uri")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    val userIdFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }

    val usernameFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USERNAME_KEY]
    }

    val emailFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[EMAIL_KEY]
    }

    val themeColorFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME_COLOR_KEY] ?: "Azul"
    }

    val profileImageUriFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PROFILE_IMAGE_URI_KEY]
    }

    val rememberMeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[REMEMBER_ME_KEY] ?: false
    }

    suspend fun saveThemeColor(color: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_COLOR_KEY] = color
        }
    }

    suspend fun saveProfileImageUri(uri: String) {
        context.dataStore.edit { preferences ->
            preferences[PROFILE_IMAGE_URI_KEY] = uri
        }
    }

    suspend fun saveSession(
        token: String,
        userId: String,
        username: String,
        email: String,
        rememberMe: Boolean
    ) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[USER_ID_KEY] = userId
            preferences[USERNAME_KEY] = username
            preferences[EMAIL_KEY] = email
            preferences[REMEMBER_ME_KEY] = rememberMe
        }
    }

    suspend fun setRememberMe(rememberMe: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[REMEMBER_ME_KEY] = rememberMe
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = ""
            preferences[USER_ID_KEY] = ""
            preferences[USERNAME_KEY] = ""
            preferences[EMAIL_KEY] = ""
            preferences[REMEMBER_ME_KEY] = false
        }
    }

    suspend fun getToken(): String? {
        val preferences = context.dataStore.data.first()
        return preferences[TOKEN_KEY].takeIf { it?.isNotEmpty() == true }
    }

    suspend fun isSessionValid(): Boolean {
        val preferences = context.dataStore.data.first()
        return !preferences[TOKEN_KEY].isNullOrEmpty()
    }
}

