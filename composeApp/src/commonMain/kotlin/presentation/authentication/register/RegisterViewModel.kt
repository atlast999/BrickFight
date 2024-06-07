package presentation.authentication.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.dto.SignupRequest
import data.repository.AuthRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _state.update { it.copy(errorMessage = throwable.message) }
    }

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private val _navHomeChannel = Channel<Unit>()
    val flowNavHome = _navHomeChannel.receiveAsFlow()

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
        authRepository.signup(
            request = SignupRequest(
                email = _state.value.email,
                username = _state.value.username,
                password = _state.value.password
            )
        )
        _navHomeChannel.send(Unit)
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