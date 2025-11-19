package com.example.animbro.core

abstract class AppException(
    val original: Exception?
) {
    abstract fun getMessage(): String

    val message: String
        get() = getMessage()
}