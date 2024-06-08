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
import io.ktor.websocket.close
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.supervisorScope
import kotlinx.datetime.Clock

class RoomRepositoryImpl(
    private val client: HttpClient,
    private val settingManager: SettingManager,
) : RoomRepository {

    private var socketSession: DefaultClientWebSocketSession? = null

    override suspend fun fetchRooms(): PagingModel<RoomDto> {
        return client.get("room")
            .body<AppResponse<PagingModel<RoomDto>>>()
            .data!!
    }

    /**
     * Create new room along with opening socket connection to that room
     */
    override suspend fun createRoom(request: CreateRoomRequest): CreateRoomResponse {
        val response = client.post("room") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<AppResponse<CreateRoomResponse>>().data!!
        openConnection(roomId = response.roomId)
        return response
    }

    override suspend fun getRoom(roomId: Int): RoomDto {
        return client.get("room/$roomId")
            .body<AppResponse<RoomDto>>().data!!
    }

    /**
     * Join the room along with opening socket connection to that room
     */
    override suspend fun joinRoom(roomId: Int) {
        client.put("room/$roomId/join")
        openConnection(roomId = roomId)
    }

    /**
     * Leave the room also closing the socket connection
     */
    override suspend fun leaveRoom(roomId: Int) = supervisorScope {
        client.put("room/$roomId/leave")
        closeConnection()
    }

    /**
     * Receive messages from the socket connection when be in a room
     */
    override fun flowChatMessages(): Flow<ChatMessageDto> {
        val session = socketSession ?: throw IllegalStateException("Connection is not established")
        //todo handle connection closed
        return session.incoming.receiveAsFlow().mapNotNull { frame ->
            session.converter?.deserialize<ChatMessageDto>(
                content = frame
            )
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
                    timestamp = Clock.System.now().toEpochMilliseconds(),
                )
            )
        }.onFailure {
            closeConnection()
            throw IllegalStateException("Connection is closed")
        }
    }

    private suspend fun openConnection(roomId: Int) {
        if (socketSession != null) {
            throw IllegalStateException("Connection is already established")
        }
        val userId =
            settingManager.getUserId() ?: throw IllegalStateException("Should never happen")
        socketSession = client.webSocketSession(urlString = "chat/$roomId/$userId") {
            port = 8080
        }
    }

    private suspend fun closeConnection(
        reason: CloseReason = CloseReason(
            code = CloseReason.Codes.NORMAL,
            message = "Client close"
        )
    ) {
        socketSession?.close(reason = reason)
        socketSession = null
    }
}