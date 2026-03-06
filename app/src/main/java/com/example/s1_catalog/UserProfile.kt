package com.example.s1_catalog

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.gson.Gson

data class UserProfile(
    val name: String = "",
    val email: String = "",
    val phone: String = ""
)

object UserProfileRepository {
    private const val PREFS_NAME = "UserPrefs"
    private const val USER_KEY = "userProfile"
    private val gson = Gson()

    var userProfile by mutableStateOf(UserProfile())
        private set

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(USER_KEY, null)
        if (json != null) {
            userProfile = gson.fromJson(json, UserProfile::class.java)
        }
    }

    fun saveProfile(context: Context, profile: UserProfile) {
        userProfile = profile
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(profile)
        prefs.edit().putString(USER_KEY, json).apply()
    }
}
