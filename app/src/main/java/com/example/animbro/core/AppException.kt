package com.example.animbro.core

abstract class AppException(
    val original: Exception?
) {
    abstract fun getErrorMessage(): String

    abstract val message: String;
}