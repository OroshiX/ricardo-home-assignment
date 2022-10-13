package ricardo.ch.login.data.login

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import ricardo.ch.login.data.Result

sealed interface LoginAction {
    object StartLogin : LoginAction
    object LoginFailed : LoginAction
    data class LoginSuccessful(val username: String) : LoginAction
}

class LoginInteractor(private val loginRepository: LoginRepository) {

    fun login(username: String, password: String) = flow {
        emit(LoginAction.StartLogin)
        if (username.contains("failed")) {
            emit(LoginAction.LoginFailed)
        } else when (val result = loginRepository.login(username, password).single()) {
            is Result.Success -> emit(LoginAction.LoginSuccessful(result.data.displayName))
            else -> emit(LoginAction.LoginFailed)
        }
    }
}
