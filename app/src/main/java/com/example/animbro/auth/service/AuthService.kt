package com.example.animbro.auth.service

import android.content.Context
import android.widget.Toast
import androidx.activity.result.ActivityResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class AuthService {
    private val auth: FirebaseAuth = Firebase.auth;
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance();

    fun login(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password);
    }

    fun signUp(email: String, password: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password);
    }

    fun sendVerificationEmail(user: FirebaseUser): Task<Void>? {
        if (!user.isEmailVerified) {
            return user.sendEmailVerification();
        }

        return null;
    }

    fun sendResetPasswordEmail(email: String): Task<Void> {
        return auth.sendPasswordResetEmail(email);
    }


    fun signWithGoogle(result: ActivityResult, context: Context) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { idToken ->
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).addOnSuccessListener {
                    if (it.user?.displayName == null) {
                        auth.currentUser?.updateProfile(
                            UserProfileChangeRequest.Builder()
                                .setDisplayName(account.displayName)
                                .build()
                        );
                    }
                }
            }
        } catch (e: ApiException) {
            Toast.makeText(context, "Google Login Failed", Toast.LENGTH_SHORT).show()
        }
    }

    fun setUser(userID: String, userName: String, email: String): Task<Void> {
        val data = mapOf<String, String>(
            "id" to userID,
            "username" to userName,
            "email" to email,
        );
        return firestore.collection("users").document(userID).set(data);
    }
}