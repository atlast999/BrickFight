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
import kotlinx.coroutines.flow.asStateFlow
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
                _state.update { it.copy(message = message.toString()) }
            }
        }
    }

    fun sendMessage(message: String) = viewModelScope.launch {
        socketChannel?.send(message)
    }

    private val _stateImageData = MutableStateFlow<ByteArray?>(null)
    val stateImageData: StateFlow<ByteArray?> = _stateImageData.asStateFlow()

    fun startCamera() = viewModelScope.launch {
        socketChannel = roomRepository.startChat(roomId = 5)
        launch {
            socketChannel!!.receiveAsFlow().collect {
                _stateImageData.value = it.data
            }
        }
    }

    fun endCamera() = viewModelScope.launch {
        socketChannel?.close()
        socketChannel = null
    }

    fun sendImageData(byteArray: ByteArray) = viewModelScope.launch {
        socketChannel?.send(byteArray = byteArray)
    }

    data class State(
        val isLoading: Boolean = true,
        val room: Room? = null,
        val message: String? = null,
    )
}