package ricardo.ch.login.ui.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ricardo.ch.login.R
import ricardo.ch.login.data.login.LoginAction
import ricardo.ch.login.data.login.LoginInteractor
import ricardo.ch.login.ui.login.LoginFormState.*

class LoginViewModel(
    private val loginInteractor: LoginInteractor,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Unconfined
) : ViewModel() {

    val loginForm = MutableStateFlow<LoginFormState>(Pristine)

    val loginResult = MutableSharedFlow<LoginResult>(replay = 1)

    fun login(username: String, password: String) {
        viewModelScope.launch(dispatcher) {
            loginInteractor.login(username, password).collect {
                when (it) {
                    is LoginAction.StartLogin -> Unit //could show loading indicator
                    is LoginAction.LoginFailed -> loginResult.tryEmit(
                        LoginResult(error = R.string.login_failed)
                    )
                    is LoginAction.LoginSuccessful -> loginResult.tryEmit(
                        LoginResult(success = LoggedInUserView(displayName = it.username))
                    )
                }
            }
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            loginForm.tryEmit(Error(usernameError = R.string.invalid_username))
        } else if (!isPasswordValid(password)) {
            loginForm.tryEmit(Error(passwordError = R.string.invalid_password))
        } else {
            loginForm.tryEmit(Valid)
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    private fun isPasswordValid(password: String): Boolean = password.length > 5
}
