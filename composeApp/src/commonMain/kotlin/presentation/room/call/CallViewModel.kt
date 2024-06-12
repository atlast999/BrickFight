package presentation.room.call

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.port
import io.ktor.websocket.CloseReason
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.send
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CallViewModel(
    private val httpClient: HttpClient,
) : ViewModel() {

    private val _frameData = MutableStateFlow<ByteArray?>(null)
    val frameData = _frameData.asStateFlow()

    private var webSocketSession: WebSocketSession? = null

    private val streamChannel = Channel<ByteArray>(
        capacity = 30,
        onBufferOverflow = BufferOverflow.DROP_LATEST,
    )
    private val frameQueue = ArrayDeque<ByteArray>()
    private var started: Boolean = false
    init {
        viewModelScope.launch(Dispatchers.Default) {
            httpClient.webSocketSession(urlString = "call") {
                port = 8080
            }.also {
                webSocketSession = it
            }.incoming.consumeEach { frame ->
                Napier.d("Send byte: ${frame.data.takeLast(5).take(3).joinToString()}")
                frameQueue.addLast(frame.data)
                if (frameQueue.size > 30 && !started) {
                    launch {
                        while (true) {
                            if (frameQueue.isNotEmpty()) {
                                _frameData.update {
                                    frameQueue.removeFirst()
                                }
                            } else {
                                delay(100)
                            }
                        }
                    }
                }
            }
        }
        viewModelScope.launch(Dispatchers.Default) {

        }
        streamChannel.receiveAsFlow().onEach {
            Napier.d("Send byte: ${it.takeLast(5).take(3).joinToString()}")
            webSocketSession?.send(content = it)
        }.flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    fun streamImageByteArray(byteArray: ByteArray) = viewModelScope.launch(Dispatchers.Default) {
        streamChannel.send(byteArray)
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