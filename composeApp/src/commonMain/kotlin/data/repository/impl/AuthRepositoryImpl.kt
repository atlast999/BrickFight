package data.repository.impl

import data.dto.LoginRequest
import data.dto.LoginResponse
import data.dto.SignupRequest
import data.dto.SignupResponse
import data.dto.wrapper.AppResponse
import data.repository.AuthRepository
import data.setting.SettingManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthRepositoryImpl(
    private val client: HttpClient,
    private val settingManager: SettingManager,
) : AuthRepository {

    override suspend fun signup(request: SignupRequest): SignupResponse {
        val response = client.post("register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<AppResponse<SignupResponse>>().data!!
        client.clearToken()
        settingManager.saveToken(response.token)
        settingManager.saveUserId(response.userId)
        return response
    }

    override suspend fun login(request: LoginRequest): LoginResponse {
        val response = client.post("login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<AppResponse<LoginResponse>>().data!!
        client.clearToken()
        settingManager.saveToken(response.token)
        settingManager.saveUserId(response.userId)
        return response
    }

    private fun HttpClient.clearToken() {
        authProvider<BearerAuthProvider>()?.clearToken()
    }
}