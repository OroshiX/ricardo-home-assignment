package ricardo.ch.login.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ricardo.ch.login.data.login.LoginDataSource
import ricardo.ch.login.data.login.LoginInteractor
import ricardo.ch.login.data.login.LoginRepository
import ricardo.ch.login.data.login.RemoteLoginDataSource
import ricardo.ch.login.ui.login.LoginViewModel

val loginModule = module {
    single<LoginDataSource> { RemoteLoginDataSource() }
    single { LoginRepository(get()) }
    single { LoginInteractor(get()) }
}

val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
}