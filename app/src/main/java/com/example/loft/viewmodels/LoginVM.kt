package com.example.loft.viewmodels

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.Executor

class LoginVM: ViewModel() {
    companion object {
        @JvmStatic private val LOGGER_TAG = "LoginVM"
        @JvmStatic private val INVALIDATE_USERNAME_MESSAGE = "Please provide a valid username."
        @JvmStatic private val INVALIDATE_PASSWORD_MESSAGE = "Please provide a valid password."
        @JvmStatic private val UNABLE_TO_LOGIN = "Unable to login, please try again."
    }

    val currentState = MutableLiveData<LoginStates>(LoginStates.Idle)
    private val auth = FirebaseAuth.getInstance()

    fun startLogin(
        username: String?,
        password: String?,
        activity: Activity
    ) {
        currentState.value = LoginStates.Loading
        val isParamsValid = validateLoginParameters(username, password)

        if (!isParamsValid) {
            return
        }

        if (username != null && password != null) {
            auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        Log.d(LOGGER_TAG, "signInWithEmail:success")
                        currentState.value = LoginStates.Success
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(LOGGER_TAG, "signInWithEmail:failure", task.exception)
                        currentState.value = LoginStates.Error(UNABLE_TO_LOGIN)
                    }
                }
        }
    }

    private fun validateLoginParameters(
        username: String?,
        password: String?
    ): Boolean {

        if(username.isNullOrEmpty()) {
            currentState.value = LoginStates.Error(INVALIDATE_USERNAME_MESSAGE)
            return false
        }

        if(password.isNullOrEmpty()) {
            currentState.value = LoginStates.Error(INVALIDATE_PASSWORD_MESSAGE)
            return false
        }

        return true
    }
}

sealed class LoginStates() {
    object Loading: LoginStates()
    object Idle: LoginStates()
    object Success: LoginStates()
    data class Error(val errorText: String): LoginStates()
}
