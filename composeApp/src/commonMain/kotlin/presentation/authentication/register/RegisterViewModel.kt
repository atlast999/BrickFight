package presentation.authentication.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.dto.SignupRequest
import data.repository.AuthRepository
import data.repository.impl.AuthRepositoryImpl
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _state.update { it.copy(errorMessage = throwable.message) }
    }

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    fun onEmailChanged(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onUsernameChanged(username: String) {
        _state.update { it.copy(username = username) }
    }

    fun onPasswordChanged(password: String) {
        _state.update { it.copy(password = password) }
    }

    fun onRegisterClick() = viewModelScope.launch(coroutineExceptionHandler) {
        _state.update { it.copy(isLoading = true) }
        val response = authRepository.signup(
            request = SignupRequest(
                email = state.value.email,
                username = state.value.username,
                password = state.value.password
            )
        )
    }.invokeOnCompletion {
        _state.update { it.copy(isLoading = false) }
    }

    data class State(
        val isLoading: Boolean = false,
        val email: String = "hoannt@gmail.com",
        val username: String = "hoannt",
        val password: String = "hoannt",
        val errorMessage: String? = null
    )
}