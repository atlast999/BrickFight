package presentation.room.call

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.port
import io.ktor.websocket.CloseReason
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.send
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CallViewModel(
    private val httpClient: HttpClient,
) : ViewModel() {

    private val _frameData = MutableStateFlow<ByteArray?>(null)
    val frameData = _frameData.asStateFlow()

    private var webSocketSession: WebSocketSession? = null

    init {
        viewModelScope.launch(Dispatchers.Default) {
            httpClient.webSocketSession(urlString = "call") {
                port = 8080
            }.also {
                webSocketSession = it
            }.incoming.consumeEach { frame ->
                _frameData.update { frame.data }
            }
        }
    }

    fun streamImageByteArray(byteArray: ByteArray) = viewModelScope.launch(Dispatchers.Default) {
        webSocketSession?.send(content = byteArray)
    }

    fun stopStream() = viewModelScope.launch(Dispatchers.Default) {
        webSocketSession?.close(
            reason = CloseReason(
                code = CloseReason.Codes.NORMAL,
                message = "Client left"
            )
        )
    }
}