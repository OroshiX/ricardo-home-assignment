package ricardo.ch.login.data.login

import ricardo.ch.login.data.Result
import ricardo.ch.login.data.login.model.LoggedInUser

/**
 * Interface that will be implemented by classes that handle authentication
 */
interface LoginDataSource {
    suspend fun login(username: String, password: String): Result<LoggedInUser>
    fun logout()
}