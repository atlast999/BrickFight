package data.repository.impl

import data.dto.ChatMessageDto
import data.dto.CreateRoomRequest
import data.dto.CreateRoomResponse
import data.dto.RoomDto
import data.dto.wrapper.AppResponse
import data.dto.wrapper.PagingModel
import data.repository.RoomRepository
import data.setting.SettingManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.ClientWebSocketSession
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.converter
import io.ktor.client.plugins.websocket.sendSerialized
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.get
import io.ktor.client.request.port
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.deserialize
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.send
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.supervisorScope

class RoomRepositoryImpl(
    private val client: HttpClient,
    private val settingManager: SettingManager,
) : RoomRepository {

    override suspend fun fetchRooms(): PagingModel<RoomDto> {
        return client.get("room")
            .body<AppResponse<PagingModel<RoomDto>>>()
            .data!!
    }

    override suspend fun createRoom(request: CreateRoomRequest): CreateRoomResponse {
        return client.post("room") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<AppResponse<CreateRoomResponse>>().data!!
    }

    override suspend fun getRoom(roomId: Int): RoomDto {
        return client.get("room/$roomId")
            .body<AppResponse<RoomDto>>().data!!
    }

    override suspend fun joinRoom(roomId: Int) {
        client.put("room/$roomId/join")
        //connect to socket
    }

    override suspend fun leaveRoom(roomId: Int) = supervisorScope {
        client.put("room/$roomId/leave")
        closeConnection()
    }

    override suspend fun startChat(roomId: Int): SocketChannel {
        return SocketChannel(session = client.webSocketSession(
            "ws/5/mem1"
        ) {
            port = 8080
        })
    }

    private var socketSession: DefaultClientWebSocketSession? = null
    override fun flowChatMessages(roomId: Int): Flow<ChatMessageDto> = flow {
        client.webSocketSession(urlString = "chat/$roomId") {
            port = 8080
        }.run {
            this@RoomRepositoryImpl.socketSession = this
            //todo handle connection closed
            incoming.consumeEach { frame ->
                converter?.deserialize<ChatMessageDto>(
                    content = frame
                )?.let { chatMessageDto ->
                    emit(chatMessageDto)
                }
            }
        }
    }

    override suspend fun sendMessage(message: String) {
        if (socketSession == null) {
            throw IllegalStateException("Connection is not established")
        }
        val userId =
            settingManager.getUserId() ?: throw IllegalStateException("Should never happen")
        kotlin.runCatching {
            socketSession!!.sendSerialized(
                data = ChatMessageDto(
                    userId = userId,
                    content = message,
                )
            )
        }.onFailure {
            closeConnection()
            throw IllegalStateException("Connection is closed")
        }
    }

    private suspend fun closeConnection() {
        socketSession?.close()
        socketSession = null
    }
}

class SocketChannel(private val session: ClientWebSocketSession) {
    fun receiveAsFlow() =
        session.incoming.receiveAsFlow()

    suspend fun send(value: String) {
        session.send(Frame.Text(text = value))
    }

    suspend fun send(byteArray: ByteArray) {
        session.send(content = byteArray)
    }

    suspend fun close() = session.close(
        reason = CloseReason(
            code = CloseReason.Codes.NORMAL,
            message = "Client close"
        )
    )
}