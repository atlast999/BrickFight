package presentation.authentication.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.dto.LoginRequest
import data.repository.AuthRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _state.update { it.copy(errorMessage = throwable.message) }
    }

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private val _navHomeChannel = Channel<Unit>()
    val flowNavHome = _navHomeChannel.receiveAsFlow()

    fun onUsernameChanged(username: String) = _state.update {
        it.copy(
            username = username
        )
    }

    fun onPasswordChanged(password: String) = _state.update {
        it.copy(
            password = password
        )
    }

    fun onLoginClick() = viewModelScope.launch(coroutineExceptionHandler) {
        _state.update { it.copy(isLoading = true) }
        authRepository.login(
            request = LoginRequest(
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
        val username: String = "hoannt",
        val password: String = "hoannt",
        val errorMessage: String? = null
    )
}