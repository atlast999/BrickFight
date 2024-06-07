package network

import data.setting.SettingManager
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


object KtorClient {
    fun createClient(settingManager: SettingManager) = HttpClient {
        defaultRequest {
            url {
                protocol = URLProtocol.HTTP
                host = "192.168.91.102"
//                host = "192.168.1.165"
                port = 8080
//                    path("api/")
                path("/")
            }
        }
        installLogging()
        installTimeOut()
        installRetry()
        install(HttpCache)
//            install(Resources)
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
        installAuth(settingManager = settingManager)
        installContentNegotiation()
    }

    private fun HttpClientConfig<*>.installLogging() {
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Napier.i(
                        message = message,
                        tag = "HTTP Client",
                    )
                }

            }
            level = LogLevel.ALL
        }
    }

    private fun HttpClientConfig<*>.installTimeOut() {
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 60_000
        }
    }

    private fun HttpClientConfig<*>.installRetry() {
//        install(HttpRequestRetry) {
//            retryOnServerErrors(maxRetries = 2)
//            exponentialDelay()
//        }
    }

    private fun HttpClientConfig<*>.installAuth(settingManager: SettingManager) {
        install(Auth) {
            bearer {
                loadTokens {
                    Napier.i("loadTokens: ${settingManager.getToken()}")
                    BearerTokens(
                        accessToken = settingManager.getToken() ?: "",
                        refreshToken = "",
                    )
                }
                refreshTokens {
//                        this.client
                    Napier.i("refreshTokens: ${settingManager.getToken()}")
                    BearerTokens(
                        accessToken = settingManager.getToken() ?: "",
                        refreshToken = "",
                    )
                }
                sendWithoutRequest { true }
            }
        }
    }

    private fun HttpClientConfig<*>.installContentNegotiation() {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

}