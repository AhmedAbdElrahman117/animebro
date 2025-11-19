package com.example.animbro.core

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException

class FirestoreException(exception: Exception?) : AppException(exception) {

    override val message: String
        get() = getErrorMessage();

    override fun getErrorMessage(): String {
        val e = original ?: return "Database error. Please try again."

        if (e is FirebaseNetworkException) {
            return "No internet connection. Please check your network."
        }

        if (e is FirebaseFirestoreException) {
            return when (e.code) {

                FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                    "You don’t have permission to perform this action."

                FirebaseFirestoreException.Code.NOT_FOUND ->
                    "The requested item could not be found."

                FirebaseFirestoreException.Code.ALREADY_EXISTS ->
                    "This item already exists."

                FirebaseFirestoreException.Code.UNAVAILABLE ->
                    "Service unavailable. Please try again shortly."

                FirebaseFirestoreException.Code.ABORTED ->
                    "The operation was interrupted."

                FirebaseFirestoreException.Code.DEADLINE_EXCEEDED ->
                    "The server took too long to respond."

                FirebaseFirestoreException.Code.CANCELLED ->
                    "The action was cancelled."

                FirebaseFirestoreException.Code.INVALID_ARGUMENT ->
                    "Some of the data you entered is not valid."

                FirebaseFirestoreException.Code.OUT_OF_RANGE ->
                    "You made a request outside the allowed limits."

                FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED ->
                    "Too many requests. Please try again later."

                FirebaseFirestoreException.Code.UNAUTHENTICATED ->
                    "Please log in again."

                FirebaseFirestoreException.Code.FAILED_PRECONDITION ->
                    "This action cannot be completed due to a system requirement."

                FirebaseFirestoreException.Code.INTERNAL ->
                    "Something went wrong on the server."

                FirebaseFirestoreException.Code.DATA_LOSS ->
                    "Some data could not be retrieved."

                FirebaseFirestoreException.Code.UNIMPLEMENTED ->
                    "This feature is not supported."

                else -> "An unexpected database error occurred."
            }
        }

        if (e is FirebaseException) {
            return "A Firebase error occurred. Please try again."
        }

        return e.localizedMessage ?: "Something went wrong. Please try again."
    }
}
