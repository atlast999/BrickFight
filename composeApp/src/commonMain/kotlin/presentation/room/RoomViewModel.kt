package presentation.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.dto.RoomDto
import data.dto.toRoom
import data.repository.RoomRepository
import data.repository.impl.SocketChannel
import domain.Room
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RoomViewModel(
    private val roomRepository: RoomRepository
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    private val _leaveRoomChannel = Channel<Unit>()
    val flowLeaveRoom = _leaveRoomChannel.receiveAsFlow()

    fun loadRoom(roomId: Int) = viewModelScope.launch {
        val room = roomRepository.getRoom(roomId = roomId).let(RoomDto::toRoom)
        _state.update {
            it.copy(
                isLoading = false,
                room = room,
            )
        }
        startChat(roomId = roomId)
    }

    fun leaveRoom() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        socketChannel?.close()
        val roomId = _state.value.room?.id ?: return@launch
        roomRepository.leaveRoom(roomId = roomId)
        _leaveRoomChannel.send(Unit)
    }.invokeOnCompletion {
        _state.update { it.copy(isLoading = false) }
    }

    private var socketChannel: SocketChannel? = null

    private fun startChat(roomId: Int) = viewModelScope.launch {
        socketChannel = roomRepository.startChat(roomId = roomId)
        launch {
            socketChannel!!.receiveAsFlow().collect { message ->
                _state.update { it.copy(message = message) }
            }
        }
    }

    fun sendMessage(message: String) = viewModelScope.launch {
        socketChannel?.send(message)
    }

    data class State(
        val isLoading: Boolean = true,
        val room: Room? = null,
        val message: String? = null,
    )
}