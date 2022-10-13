package ricardo.ch.login.ui.login

/**
 * Data validation state of the login form.
 */
sealed interface LoginFormState {
    data class Error(
        val usernameError: Int? = null,
        val passwordError: Int? = null
    ) : LoginFormState

    object Valid: LoginFormState

    object Pristine : LoginFormState
}
