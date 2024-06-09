package di

import data.repository.AuthRepository
import data.repository.RoomRepository
import data.repository.impl.AuthRepositoryImpl
import data.repository.impl.RoomRepositoryImpl
import data.setting.SettingManager
import data.setting.impl.SettingManagerImpl
import io.ktor.client.HttpClient
import network.KtorClient
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import presentation.authentication.login.LoginViewModel
import presentation.authentication.register.RegisterViewModel
import presentation.room.call.CallViewModel
import presentation.room.chat.RoomViewModel
import presentation.room.list.ListRoomViewModel

private val settingModule = module {
    singleOf(::SettingManagerImpl) {
        bind<SettingManager>()
    }
}

private val networkModule = module {
    single<HttpClient> {
        KtorClient.createClient(settingManager = get())
    }
}

private val repositoryModule = module {
    singleOf(::AuthRepositoryImpl) {
        bind<AuthRepository>()
    }
    singleOf(::RoomRepositoryImpl) {
        bind<RoomRepository>()
    }
}

private val viewModelModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::ListRoomViewModel)
    viewModelOf(::RoomViewModel)
    viewModelOf(::CallViewModel)
}

internal fun appModules() = listOf(
    settingModule,
    networkModule,
    repositoryModule,
    viewModelModule,
)