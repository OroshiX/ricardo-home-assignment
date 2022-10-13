package ricardo.ch.login.data.login.model

import Role

/**
 * Class that captures user information for logged in users retrieved from LoginRepository
 */
class LoggedInUser(
    val userId: String,
    val displayName: String,
    val role: Role = Role.Engineer
)
