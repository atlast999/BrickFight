package presentation.room.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.dto.CreateRoomRequest
import data.dto.RoomDto
import data.dto.toRoom
import data.repository.RoomRepository
import domain.Room
import domain.RoomId
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListRoomViewModel(
    private val roomRepository: RoomRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private val _joinedRoomChannel = Channel<RoomId>()
    val flowJoinedRoom = _joinedRoomChannel.receiveAsFlow()

    init {
        loadRooms()
    }

    fun loadRooms() = viewModelScope.launch {
        val rooms = roomRepository.fetchRooms().items.map(RoomDto::toRoom)
        _state.update {
            it.copy(
                isLoading = false,
                rooms = rooms
            )
        }
    }

    fun createRoom(name: String) = viewModelScope.launch {
        _state.update {
            it.copy(
                isLoading = true
            )
        }
        val response = roomRepository.createRoom(
            request = CreateRoomRequest(
                name = name,
            )
        )
        _joinedRoomChannel.send(response.roomId)
    }

    fun joinRoom(room: Room) = viewModelScope.launch {

    }

    data class State(
        val isLoading: Boolean = true,
        val rooms: List<Room> = emptyList(),
        val error: String? = null
    )
}