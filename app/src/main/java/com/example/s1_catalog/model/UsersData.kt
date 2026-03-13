package com.example.s1_catalog.model

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import android.util.Log

data class UserData(
    val id: String = "",
    val role: String = "user",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = ""
)

object UserRepository {
    private val db by lazy { Firebase.firestore }
    private const val COLLECTION_NAME = "Users"
    private const val PREFS_NAME = "UserPrefs"
    private const val KEY_USER_ID = "logged_user_id"

    var currentUser by mutableStateOf(UserData())
        private set

    fun init(context: Context, userId: String = "") {
        var idToUse = userId
        
        if (idToUse.isEmpty()) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            idToUse = prefs.getString(KEY_USER_ID, "") ?: ""
        }
        
        if (idToUse.isNotEmpty()) {
            listenToUserChanges(idToUse)
        }
    }

    fun setStayConnected(context: Context, userId: String, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (enabled) {
            prefs.edit().putString(KEY_USER_ID, userId).apply()
        } else {
            prefs.edit().remove(KEY_USER_ID).apply()
        }
    }

    fun isUserLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_USER_ID, "")?.isNotEmpty() == true
    }

    fun logout(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_USER_ID).apply()
        currentUser = UserData()
    }

    private fun listenToUserChanges(userId: String) {
        db.collection(COLLECTION_NAME).document(userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("UserRepository", "Listen failed", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    currentUser = snapshot.toObject(UserData::class.java) ?: UserData(id = userId)
                } else {
                    currentUser = UserData(id = userId)
                }
            }
    }

    fun findUserByEmail(email: String, onResult: (UserData?) -> Unit) {
        db.collection(COLLECTION_NAME)
            .whereEqualTo("email", email.trim())
            .get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.documents.firstOrNull()?.toObject(UserData::class.java)
                onResult(user)
            }
            .addOnFailureListener { e ->
                Log.e("UserRepository", "Error finding user", e)
                onResult(null)
            }
    }

    fun saveUser(user: UserData) {
        val docRef = if (user.id.isEmpty()) {
            db.collection(COLLECTION_NAME).document()
        } else {
            db.collection(COLLECTION_NAME).document(user.id)
        }

        val userToSave = if (user.id.isEmpty()) {
            user.copy(id = docRef.id)
        } else {
            user
        }

        docRef.set(userToSave)
            .addOnSuccessListener {
                Log.d("UserRepository", "User saved successfully with ID: ${userToSave.id}")
                if (currentUser.id == "" || currentUser.id == userToSave.id) {
                    currentUser = userToSave
                }
            }
            .addOnFailureListener { e ->
                Log.e("UserRepository", "Error saving user", e)
            }
    }

    fun deleteUser(userId: String) {
        if (userId.isEmpty()) return
        db.collection(COLLECTION_NAME).document(userId)
            .delete()
            .addOnSuccessListener {
                Log.d("UserRepository", "User deleted successfully")
            }
            .addOnFailureListener { e ->
                Log.e("UserRepository", "Error deleting user", e)
            }
    }
}
