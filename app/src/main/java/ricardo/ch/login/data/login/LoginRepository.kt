package ricardo.ch.login.data.login

import kotlinx.coroutines.flow.flow
import ricardo.ch.login.data.Result
import ricardo.ch.login.data.login.model.LoggedInUser

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        user = null
    }

    fun logout() {
        user = null
        RemoteLoginDataSource().logout()
    }

    fun login(username: String, password: String) = flow {
        val result = RemoteLoginDataSource().login(username, password)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        emit(result)
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        user = loggedInUser
    }
}
