package com.example.animbro.auth.repository

import AuthException
import com.example.animbro.Constants
import com.example.animbro.auth.service.AuthService

class LoginRepository {
    private var auth: AuthService = AuthService();

    fun login(
        email: String,
        password: String,
        onValidatorError: (error: String, cause: ErrorCause) -> Unit,
        onSuccess: (isVerified: Boolean) -> Unit?,
        onFailure: (error: String) -> Unit?,
    ) {
        if (email.isEmpty()) {
            onValidatorError("Email Required", ErrorCause.email);
        } else if (password.isEmpty()) {
            onValidatorError("Password Required", ErrorCause.password);
        } else if (!Constants.emailRegex.matches(email)) {
            onValidatorError("Invalid Email", ErrorCause.password)
        } else if (!Constants.lengthRegex.matches(password)) {
            onValidatorError("Invalid Password", ErrorCause.password);
        } else {
            auth.login(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess(it.result.user?.isEmailVerified ?: false);
                } else {
                    onFailure(AuthException(it.exception!!).message);
                }
            };
        }

    }

    fun sendResetPasswordEmail(
        email: String,
        onValidatorError: (String, ErrorCause) -> Unit,
        onSuccess: () -> Unit,
        onFailure: (message: String) -> Unit,
    ) {
        if (email.isEmpty()) {
            onValidatorError("Email Required", ErrorCause.email);
        } else if (!Constants.emailRegex.matches(email)) {
            onValidatorError("Invalid Email", ErrorCause.email);
        } else {
            auth.sendResetPasswordEmail(email).addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess();
                } else {
                    onFailure(AuthException(it.exception!!).message);
                }
            }
        }
    }
}

enum class ErrorCause {
    userName,
    email,
    password,
    confirm,
    none,
}