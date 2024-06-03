package data.repository

import data.dto.LoginRequest
import data.dto.SignupRequest
import data.dto.SignupResponse

interface AuthRepository {

    suspend fun signup(request: SignupRequest): SignupResponse
}