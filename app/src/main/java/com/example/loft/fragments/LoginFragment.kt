package com.example.loft.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.loft.R
import com.example.loft.viewmodels.LoginStates
import com.example.loft.viewmodels.LoginVM
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    private val loginVM: LoginVM by navGraphViewModels(R.id.nav_graph)

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_login, container, false)
        setupLoginFields(rootView)
        setupRegisterButton(rootView)
        return rootView
    }

    private fun setupLoginFields(rootView: View) {
        usernameEditText = rootView.findViewById(R.id.username_edit_text)
        handleEditTextUnFocus(usernameEditText)
        passwordEditText = rootView.findViewById(R.id.password_edit_text)
        handleEditTextUnFocus(passwordEditText)

        loginButton = rootView.findViewById(R.id.login_button)
        progressBar = rootView.findViewById(R.id.progressBar)

        loginButton.setOnClickListener {
            loginButton.isEnabled = false
            hideKeyboard(it)
            loginVM.startLogin(
                usernameEditText.text.toString(),
                passwordEditText.text.toString(),
                requireActivity()
            )
        }

        loginVM.currentState.observe(viewLifecycleOwner, Observer {
            handleState(it)
        })
    }

    private fun handleState(loginStates: LoginStates) {
        when (loginStates) {
            LoginStates.Loading -> {
                handleLoadingState(true)
            }
            LoginStates.Idle -> {
                handleLoadingState(false)
            }
            LoginStates.Success -> {
                handleLoadingState(false)
                handleSuccessState()
            }
            is LoginStates.Error -> {
                handleLoadingState(false)
                handleErrorState(loginStates.errorText)
            }
        }
    }

    private fun handleSuccessState() {
        findNavController().navigate(R.id.action_start_home_fragment)
    }

    private fun handleErrorState(errorMessage: String) {
        val snackBar = Snackbar.make(requireView(), errorMessage, Snackbar.LENGTH_INDEFINITE)
        snackBar
            .setActionTextColor(resources.getColor(R.color.result_label, null))
            .setAction(R.string.dismiss_snack_bar_label) {
                snackBar.dismiss()
            }
        snackBar.show()
    }

    private fun handleLoadingState(isLoading: Boolean) {
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
            loginButton.isEnabled = false
        } else {
            progressBar.visibility = View.GONE
            loginButton.isEnabled = true
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = requireActivity().getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun handleEditTextUnFocus(editText: EditText) {
        editText.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(view)
            }
        }
    }

    private fun setupRegisterButton(rootView: View) {
        val registerTextView = rootView.findViewById<TextView>(R.id.register_text_view)

        registerTextView.setOnClickListener {
            findNavController().navigate(R.id.action_register)
        }
    }
}
