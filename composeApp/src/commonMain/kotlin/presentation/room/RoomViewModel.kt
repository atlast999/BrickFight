package presentation.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.dto.RoomDto
import data.dto.toChatMessage
import data.dto.toRoom
import data.repository.RoomRepository
import data.setting.SettingManager
import domain.ChatMessage
import domain.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RoomViewModel(
    private val roomRepository: RoomRepository,
    private val settingManager: SettingManager,
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    private val _leaveRoomChannel = Channel<Unit>()
    val flowLeaveRoom = _leaveRoomChannel.receiveAsFlow()

    init {
        roomRepository.flowChatMessages().onEach { message ->
            val currentRoom = state.value.room ?: return@onEach
            var sender = currentRoom.members.firstOrNull { it.id == message.userId }
            if (sender == null) { //room might be out of date
                loadRoomInternal(roomId = currentRoom.id)
                val reloadedRoom = state.value.room ?: return@onEach
                sender = reloadedRoom.members.firstOrNull { it.id == message.userId }
                    ?: throw IllegalStateException("Sender: ${message.userId} not found, should never happen")
            }
            //todo user join -> send a message -> then leave ??
            if (sender.id == settingManager.getUserId()) { //now sender is found, if it is my message, reset it to null
                sender = null
            }
            val incomingMessage = message.toChatMessage(sender = sender)
            _state.update {
                it.copy(incomingMessage = incomingMessage)
            }
        }.flowOn(context = Dispatchers.Default).launchIn(scope = viewModelScope)
    }

    fun loadRoom(roomId: Int) = runBlockingTask {
        loadRoomInternal(roomId = roomId)
    }

    fun leaveRoom() = runBlockingTask {
        val roomId = _state.value.room?.id ?: return@runBlockingTask
        roomRepository.leaveRoom(roomId = roomId)
        _leaveRoomChannel.send(Unit)
    }

    fun sendMessage(message: String) = runTask {
        roomRepository.sendMessage(message = message)
    }

    private suspend fun loadRoomInternal(roomId: Int) {
        val room = roomRepository.getRoom(roomId = roomId).let(RoomDto::toRoom)
        _state.update {
            it.copy(room = room)
        }
    }

    private fun runBlockingTask(block: suspend () -> Unit) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        block()
    }.also { job ->
        job.invokeOnCompletion {
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun runTask(block: suspend () -> Unit) = viewModelScope.launch {
        block()
    }

    private val _stateImageData = MutableStateFlow<ByteArray?>(null)
    val stateImageData: StateFlow<ByteArray?> = _stateImageData.asStateFlow()

    data class State(
        val isLoading: Boolean = false,
        val room: Room? = null,
        val incomingMessage: ChatMessage? = null,
    )
}