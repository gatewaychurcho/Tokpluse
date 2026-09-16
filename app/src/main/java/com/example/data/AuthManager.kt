package com.example.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AuthState(
    val isAuthenticated: Boolean = false,
    val currentUser: User? = null,
    val authMethod: String = "", // "Google Sign-In" or "Guest"
    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthManager(private val context: Context) {

    private val prefs = context.getSharedPreferences("tokpulse_auth", Context.MODE_PRIVATE)

    private val _authState = MutableStateFlow(loadInitialState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private fun loadInitialState(): AuthState {
        val isAuth = prefs.getBoolean("is_auth", true) // default logged in with Google for smooth demo
        val name = prefs.getString("user_name", "Alex Boya") ?: "Alex Boya"
        val email = prefs.getString("user_email", "boya5313@gmail.com") ?: "boya5313@gmail.com"
        val username = prefs.getString("username", "boya_creator") ?: "boya_creator"

        return if (isAuth) {
            AuthState(
                isAuthenticated = true,
                currentUser = User(
                    id = "current_user_1",
                    name = name,
                    username = username,
                    email = email,
                    bio = "Creating short videos with TokPulse 🎥 Powered by Asifofc watermark",
                    followersCount = 1240,
                    followingCount = 48,
                    likesCount = 18200,
                    isVerified = false
                ),
                authMethod = "Google Sign-In"
            )
        } else {
            AuthState(isAuthenticated = false, currentUser = null)
        }
    }

    fun signInWithGoogle(email: String = "boya5313@gmail.com", name: String = "Alex Boya") {
        prefs.edit()
            .putBoolean("is_auth", true)
            .putString("user_name", name)
            .putString("user_email", email)
            .putString("username", email.substringBefore("@"))
            .apply()

        val newUser = User(
            id = "google_user_${System.currentTimeMillis()}",
            name = name,
            username = email.substringBefore("@"),
            email = email,
            bio = "Verified Google Creator on TokPulse 🎬",
            followersCount = 1240,
            followingCount = 48,
            likesCount = 18200,
            isVerified = true
        )

        _authState.value = AuthState(
            isAuthenticated = true,
            currentUser = newUser,
            authMethod = "Google Sign-In"
        )
    }

    fun continueAsGuest() {
        val guest = User(
            id = "guest_${System.currentTimeMillis()}",
            name = "Guest User",
            username = "guest_${(1000..9999).random()}",
            email = "",
            bio = "Exploring TokPulse Feed ✨",
            followersCount = 0,
            followingCount = 12,
            likesCount = 0
        )
        _authState.value = AuthState(
            isAuthenticated = true,
            currentUser = guest,
            authMethod = "Guest"
        )
    }

    fun signOut() {
        prefs.edit().putBoolean("is_auth", false).apply()
        _authState.value = AuthState(isAuthenticated = false, currentUser = null)
    }
}
