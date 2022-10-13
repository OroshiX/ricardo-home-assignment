package ricardo.ch.login

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import ricardo.ch.login.di.loginModule
import ricardo.ch.login.di.viewModelModule

class LoginApplication : Application() {
    internal lateinit var koinApplication: KoinApplication

    override fun onCreate() {
        super.onCreate()
        koinApplication = startKoin {
            androidContext(this@LoginApplication)
            modules(loginModule, viewModelModule)
        }
    }
}
