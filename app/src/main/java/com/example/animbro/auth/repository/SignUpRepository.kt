package com.example.animbro.auth.repository

import AuthException
import com.example.animbro.Constants
import com.example.animbro.auth.service.AuthService
import com.example.animbro.core.FirestoreException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class SignUpRepository {
    private var auth: AuthService = AuthService();

    fun signUp(
        userName: String,
        email: String,
        password: String,
        confirmPassword: String,
        onValidatorError: (error: String, cause: ErrorCause) -> Unit,
        onSuccess: (user: FirebaseUser) -> Unit,
        onFailure: (message: String) -> Unit,
    ) {
        if (userName.isEmpty()) {
            onValidatorError("UserName Required", ErrorCause.userName);
        } else if (email.isEmpty()) {
            onValidatorError("Email Required", ErrorCause.email);
        } else if (password.isEmpty()) {
            onValidatorError("Password Required", ErrorCause.password);
        } else if (confirmPassword.isEmpty()) {
            onValidatorError("Password Confirmation Required", ErrorCause.password);
        } else if (!Constants.emailRegex.matches(email)) {
            onValidatorError("Invalid Email", ErrorCause.email);
        } else if (!Constants.lengthRegex.matches(password)) {
            onValidatorError("Password Length must be more than 6 Characters", ErrorCause.password);
        } else if (!Constants.twoDigitsRegex.matches(password)) {
            onValidatorError("Password must Contain at least 2 Numbers", ErrorCause.password);
        } else if (!Constants.uppercaseRegex.matches(password)) {
            onValidatorError(
                "Password Length be Contain at least one Uppercase",
                ErrorCause.password
            );
        } else if (!Constants.specialCharRegex.matches(password)) {
            onValidatorError(
                "Password Length be Contain at least special Character",
                ErrorCause.password
            );
        } else if (confirmPassword != password) {
            onValidatorError("Password Confirmation is Invalid", ErrorCause.confirm);
        } else {
            auth.signUp(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = it.result.user!!;
                    user.updateProfile(
                        UserProfileChangeRequest.Builder().setDisplayName(userName).build()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            setUser(
                                user.uid,
                                userName,
                                email,
                                {
                                    onSuccess(user);
                                },
                                {
                                    onFailure(it);
                                },
                            );
                        } else {
                            onFailure(AuthException(it.exception).message);
                        }
                    }
                } else {
                    onFailure(AuthException(it.exception).message);
                }
            };
        }
    }

    fun sendVerificationEmail(
        user: FirebaseUser,
        onSuccess: () -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        val res = auth.sendVerificationEmail(user);
        res?.addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess();
            } else {
                onFailure(AuthException(it.exception).message);
            }
        }
    }

    fun setUser(
        userID: String,
        userName: String,
        email: String,
        onSuccess: () -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        auth.setUser(userID, userName, email).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess();
            } else {
                onFailure(FirestoreException(it.exception).message);
            }
        }
    }
}