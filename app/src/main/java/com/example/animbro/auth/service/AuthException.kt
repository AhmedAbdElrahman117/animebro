import com.google.firebase.auth.*

class AuthException(exception: Exception) {

    val message: String = createFriendlyMessage(exception)

    private fun createFriendlyMessage(e: Exception): String {
        return when (e) {

            // Invalid credentials: wrong password, phone code, etc.
            is FirebaseAuthInvalidCredentialsException -> {
                when (e.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "Please enter a valid email address."
                    "ERROR_WRONG_PASSWORD" -> "The password you entered is incorrect."
                    "ERROR_INVALID_VERIFICATION_CODE" -> "The verification code is not correct. Please try again."
                    "ERROR_INVALID_VERIFICATION_ID" -> "Something went wrong with verification. Please try again."
                    else -> "We couldn't verify your information. Please check and try again."
                }
            }

            // User does not exist, disabled, expired, etc.
            is FirebaseAuthInvalidUserException -> {
                when (e.errorCode) {
                    "ERROR_USER_DISABLED" -> "This account has been disabled. Please contact support if this is a mistake."
                    "ERROR_USER_NOT_FOUND" -> "No account was found with this email."
                    "ERROR_USER_TOKEN_EXPIRED" -> "Your session has expired. Please sign in again."
                    "ERROR_INVALID_USER_TOKEN" -> "We couldn't verify your identity. Please sign in again."
                    else -> "There seems to be an issue with your account. Please try signing in again."
                }
            }

            // Email already exists
            is FirebaseAuthUserCollisionException -> {
                "This email is already registered. Try logging in or use a different email."
            }

            // Weak password
            is FirebaseAuthWeakPasswordException -> {
                "Your password is too weak. Try combining letters, numbers, and special characters."
            }

            // Email sending (verification / reset)
            is FirebaseAuthEmailException -> {
                "We couldn't send the email. Please check your connection and try again."
            }

            is FirebaseAuthActionCodeException -> {
                "This link is no longer valid. Please request a new one."
            }

            // Multi-factor authentication required
            is FirebaseAuthMultiFactorException -> {
                "For security reasons, additional verification is required."
            }

            // Sensitive action (change password/email)
            is FirebaseAuthRecentLoginRequiredException -> {
                "For your safety, please sign in again before continuing."
            }

            // Captcha missing (mostly internal Android issue)
            is FirebaseAuthMissingActivityForRecaptchaException -> {
                "Verification couldn't be completed. Please try again."
            }

            // Fallback for rare/unknown exceptions
            else -> "Something went wrong. Please try again in a moment."
        }
    }
}
