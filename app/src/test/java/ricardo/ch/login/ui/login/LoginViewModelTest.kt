package ricardo.ch.login.ui.login

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkClass
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.get
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import ricardo.ch.login.R
import ricardo.ch.login.data.login.FakeLoginDataSource
import ricardo.ch.login.data.login.LoginDataSource
import ricardo.ch.login.data.login.LoginInteractor
import ricardo.ch.login.data.login.LoginRepository
import java.util.regex.Pattern

class LoginViewModelTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(module {
            single<LoginDataSource> { FakeLoginDataSource() }
            single { LoginRepository(get()) }
            single { LoginInteractor(get()) }
        })
    }

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz -> mockkClass(clazz) }

    private lateinit var loginViewModel: LoginViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        val validator = declareMock<MailValidator>()
        every { validator.emailPattern() } returns Pattern.compile("""[a-zA-Z0-9+._%\-]{1,256}@[a-zA-Z0-9][a-zA-Z0-9\-]{0,64}(\.[a-zA-Z0-9][a-zA-Z0-9\-]{0,25})+""")
        loginViewModel = LoginViewModel(get(), validator, UnconfinedTestDispatcher())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `login should give a LoginResult success`() = runTest {
        val result = arrayListOf<LoginResult>()
        val job = launch(UnconfinedTestDispatcher()) {
            loginViewModel.loginResult.toList(result)
        }
        loginViewModel.login("a", "a")
        result shouldBe listOf(LoginResult(success = LoggedInUserView(displayName = "Jane Doe")))
        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `login should give a LoginResult failed`() = runTest {
        val result = arrayListOf<LoginResult>()
        val job = launch(UnconfinedTestDispatcher()) {
            loginViewModel.loginResult.toList(result)
        }
        loginViewModel.login("failed", "password")
        result shouldBe listOf(LoginResult(error = R.string.login_failed))
        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `loginDataChanged with invalid then valid data should have the right order of states`() =
        runTest {
            val result = arrayListOf<LoginFormState>()
            val job = launch(UnconfinedTestDispatcher()) {
                loginViewModel.loginForm.toList(result)
            }
            loginViewModel.loginDataChanged("abc@", "def")
            loginViewModel.loginDataChanged("abc", "def")
            loginViewModel.loginDataChanged("abc", "defghi")
            result shouldBe listOf(
                LoginFormState.Pristine,
                LoginFormState.Error(usernameError = R.string.invalid_username),
                LoginFormState.Error(passwordError = R.string.invalid_password),
                LoginFormState.Valid
            )
            job.cancel()
        }
}



