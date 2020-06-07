package com.example.loft.viewmodels

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SignUpVM: ViewModel() {
    companion object {
        @JvmStatic private val LOGGER_TAG = "SignUpVM"
        @JvmStatic private val INVALIDATE_USERNAME_MESSAGE = "Please provide a valid username."
        @JvmStatic private val INVALIDATE_PASSWORD_MESSAGE = "Please provide a valid password."
        @JvmStatic private val PASSWORDS_DOES_NOT_MATCH_MESSAGE = "Please retype same password."
        @JvmStatic private val UNABLE_TO_SIGN_UP = "Unable to register, please try again."
    }

    val currentState = MutableLiveData<SignUpStates>(SignUpStates.Idle)
    private val auth = FirebaseAuth.getInstance()

    fun register(
        username: String?,
        password: String?,
        reTypedPassword: String?,
        activity: Activity
    ) {
        currentState.value = SignUpStates.Loading
        val isParamsValid = validateLoginParameters(username, password, reTypedPassword)

        if (!isParamsValid) {
            return
        }

        if (username != null && password != null) {
            auth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        Log.d(LOGGER_TAG, "createUserWithEmailAndPassword:success")
                        currentState.value = SignUpStates.Success
                    } else {
                        Log.w(LOGGER_TAG, "createUserWithEmailAndPassword:failure", task.exception)
                        currentState.value = SignUpStates.Error(UNABLE_TO_SIGN_UP)
                    }
                }
        }
    }

    private fun validateLoginParameters(
        username: String?,
        password: String?,
        reTypedPassword: String?
    ): Boolean {

        if(username.isNullOrEmpty()) {
            currentState.value = SignUpStates.Error(INVALIDATE_USERNAME_MESSAGE)
            return false
        }

        if(password.isNullOrEmpty()) {
            currentState.value = SignUpStates.Error(INVALIDATE_PASSWORD_MESSAGE)
            return false
        }

        if(password != reTypedPassword) {
            currentState.value = SignUpStates.Error(PASSWORDS_DOES_NOT_MATCH_MESSAGE)
            return false
        }

        return true
    }
}

sealed class SignUpStates() {
    object Loading: SignUpStates()
    object Idle: SignUpStates()
    object Success: SignUpStates()
    data class Error(val errorText: String): SignUpStates()
}
