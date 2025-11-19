import com.example.animbro.core.AppException
import com.google.firebase.auth.*

class AuthException(exception: Exception?) : AppException(exception) {

    override fun getMessage(): String {
        val e = original ?: return "Authentication failed. Please try again."

        return when (e) {

            is FirebaseAuthInvalidCredentialsException -> {
                when (e.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "Please enter a valid email address."
                    "ERROR_WRONG_PASSWORD" -> "The password you entered is incorrect."
                    "ERROR_INVALID_VERIFICATION_CODE" -> "The verification code is incorrect."
                    "ERROR_INVALID_VERIFICATION_ID" -> "Something went wrong with verification."
                    else -> "We couldn't verify your information. Please try again."
                }
            }

            is FirebaseAuthInvalidUserException -> {
                when (e.errorCode) {
                    "ERROR_USER_DISABLED" -> "This account has been disabled. Please contact support."
                    "ERROR_USER_NOT_FOUND" -> "No account exists with this email."
                    "ERROR_USER_TOKEN_EXPIRED" -> "Your session has expired. Please sign in again."
                    "ERROR_INVALID_USER_TOKEN" -> "We couldn't verify your identity. Please sign in again."
                    else -> "There seems to be an issue with your account. Please try again."
                }
            }

            is FirebaseAuthUserCollisionException ->
                "This email is already registered. Try logging in instead."

            is FirebaseAuthWeakPasswordException ->
                "Your password is too weak. Try using letters, numbers, and symbols."

            is FirebaseAuthEmailException ->
                "We couldn’t send the email. Please check your connection."

            is FirebaseAuthActionCodeException ->
                "This link is no longer valid. Please request a new one."

            is FirebaseAuthMultiFactorException ->
                "Additional verification is required."

            is FirebaseAuthRecentLoginRequiredException ->
                "Please sign in again to continue."

            is FirebaseAuthMissingActivityForRecaptchaException ->
                "Verification could not be completed. Please try again."

            else -> "Something went wrong. Please try again."
        }
    }
}
