package com.example.loft.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.loft.R
import com.example.loft.viewmodels.SignUpStates
import com.example.loft.viewmodels.SignUpVM
import com.google.android.material.snackbar.Snackbar

class SignUpFragment : Fragment() {

    private val signUpVM: SignUpVM by navGraphViewModels(R.id.nav_graph)

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var retypePasswordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_sign_up, container, false)
        setupLoginFields(rootView)
        return rootView
    }

    private fun setupLoginFields(rootView: View) {
        usernameEditText = rootView.findViewById(R.id.username_edit_text)
        handleEditTextUnFocus(usernameEditText)
        passwordEditText = rootView.findViewById(R.id.password_edit_text)
        handleEditTextUnFocus(passwordEditText)
        retypePasswordEditText = rootView.findViewById(R.id.retype_password_edit_text)
        handleEditTextUnFocus(retypePasswordEditText)

        signUpButton = rootView.findViewById(R.id.sign_up_button)
        progressBar = rootView.findViewById(R.id.progressBar)

        signUpButton.setOnClickListener {
            signUpButton.isEnabled = false
            hideKeyboard(it)
            signUpVM.register(
                usernameEditText.text.toString(),
                passwordEditText.text.toString(),
                retypePasswordEditText.text.toString(),
                requireActivity()
            )
        }

        signUpVM.currentState.observe(viewLifecycleOwner, Observer {
            handleState(it)
        })
    }

    private fun handleState(signUpStates: SignUpStates) {
        when (signUpStates) {
            SignUpStates.Loading -> {
                handleLoadingState(true)
            }
            SignUpStates.Idle -> {
                handleLoadingState(false)
            }
            SignUpStates.Success -> {
                handleLoadingState(false)
                handleSuccessState()
            }
            is SignUpStates.Error -> {
                handleLoadingState(false)
                handleErrorState(signUpStates.errorText)
            }
        }
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

    private fun handleSuccessState() {
        findNavController().navigate(R.id.action_start_home_fragment)
    }

    private fun handleLoadingState(isLoading: Boolean) {
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
            signUpButton.isEnabled = false
        } else {
            progressBar.visibility = View.GONE
            signUpButton.isEnabled = true
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = requireActivity().getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun handleEditTextUnFocus(editText: EditText) {
        editText.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(view)
            }
        }
    }
}
