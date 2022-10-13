package ricardo.ch.login.data.login

import kotlinx.coroutines.delay
import ricardo.ch.login.data.Result
import ricardo.ch.login.data.login.model.LoggedInUser
import java.io.IOException
import java.util.*

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class RemoteLoginDataSource: LoginDataSource {

    override suspend fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            delay(1000L) //Simulate network request
            val fakeUser = LoggedInUser(UUID.randomUUID().toString(), "Jane Doe")
            return Result.Success(fakeUser)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    override fun logout() = Unit
}
