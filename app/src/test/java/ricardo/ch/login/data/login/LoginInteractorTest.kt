package ricardo.ch.login.data.login

import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.check.checkModules
import org.koin.test.inject
import ricardo.ch.login.data.Result
import ricardo.ch.login.data.login.model.LoggedInUser
import ricardo.ch.login.di.loginModule
import ricardo.ch.login.di.viewModelModule
import java.util.*


class LoginInteractorTest : KoinTest {
    private val loginInteractor: LoginInteractor by inject()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(module {
            single<LoginDataSource> { FakeLoginDataSource() }
            single { LoginRepository(get()) }
            single { LoginInteractor(get()) }
        }, viewModelModule)
    }

    @Before
    fun setUp() {
    }

    @Test
    fun verifyKoinApp() {
        koinApplication {
            modules(loginModule, viewModelModule)
            checkModules()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `login loads then succeeds`() = runTest {
        val loginActions = loginInteractor.login("tata", "toto")
            .toList()

        val expected = listOf(LoginAction.StartLogin, LoginAction.LoginSuccessful("Jane Doe"))
        withClue("login should load then succeed") { loginActions shouldBe expected }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `login loads then fails`() = runTest {
        val loginActions = loginInteractor.login("a failed attempt", "toto").toList()
        val expected = listOf(LoginAction.StartLogin, LoginAction.LoginFailed)
        withClue("login should load then fail") { loginActions shouldBe expected }
    }
}

class FakeLoginDataSource : LoginDataSource {
    override suspend fun login(username: String, password: String): Result<LoggedInUser> {
        return Result.Success(LoggedInUser(UUID.randomUUID().toString(), "Jane Doe"))
    }

    override fun logout() = Unit
}