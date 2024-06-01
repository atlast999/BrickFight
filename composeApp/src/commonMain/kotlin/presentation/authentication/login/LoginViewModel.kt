package presentation.authentication.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

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
    data class State(
        val username: String = "",
        val password: String = "",
    )
}