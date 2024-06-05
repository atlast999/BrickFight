package data.repository.impl

import data.dto.LoginRequest
import data.dto.LoginResponse
import data.dto.SignupRequest
import data.dto.SignupResponse
import data.dto.wrapper.AppResponse
import data.repository.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import network.KtorClient

class AuthRepositoryImpl(
    private val client: HttpClient,
) : AuthRepository {

    override suspend fun signup(request: SignupRequest): SignupResponse {
        return client.post("register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<AppResponse<SignupResponse>>().data!!
    }

    override suspend fun login(request: LoginRequest): LoginResponse {
        return client.post("login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<AppResponse<LoginResponse>>().data!!
    }
}