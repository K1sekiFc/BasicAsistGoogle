package com.example.basicasistgoogle.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.basicasistgoogle.domain.entity.UserData

import com.google.firebase.firestore.auth.User
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context : Context
){
    private val prefs: SharedPreferences= context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    companion object{
        private const val KEY_EMAIL = "key_email"
        private const val KEY_DISPLAY_NAME = "key_display_name"
        private const val KEY_PROFILE_PICTURE = "key_profile_picture"
    }
    fun saveUserData(userData: UserData) {
        prefs.edit().apply {
            putString(KEY_EMAIL, userData.email)
            putString(KEY_DISPLAY_NAME, userData.displayName)
            putString(KEY_PROFILE_PICTURE, userData.profilePictureUrl)
        }.apply()
    }
    fun getUserData(): UserData?{
            val email=prefs.getString(KEY_EMAIL,null)?: return null
            val  displayName = prefs.getString(KEY_DISPLAY_NAME,null)
            val  profilePicture = prefs.getString(KEY_PROFILE_PICTURE,null)

            return UserData(
                email=email,
                displayName= displayName,
                profilePictureUrl = profilePicture
            )
        }
    fun clearSession(){
        prefs.edit().clear().apply()
    }
}