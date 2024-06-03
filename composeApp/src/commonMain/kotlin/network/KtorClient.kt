package network

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
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
    fun createClient() = HttpClient {
        defaultRequest {
            url {
                protocol = URLProtocol.HTTP
                host = "192.168.91.102"
                port = 8080
//                    path("api/")
                path("/")
            }
        }
        installLogging()
        installTimeOut()
        installRetry()
//            install(HttpCache)
//            install(Resources)
//            install(WebSockets) {
//                contentConverter = KotlinxWebsocketSerializationConverter(Json)
//            }
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
            level = LogLevel.BODY
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
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 2)
            exponentialDelay()
        }
    }

    private fun HttpClientConfig<*>.installAuth() {
//        bearer {
//            loadTokens {
//                BearerTokens("", "")
//            }
//            refreshTokens {
////                        this.client
//                BearerTokens("", "")
//            }
//            sendWithoutRequest { true }
//        }
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