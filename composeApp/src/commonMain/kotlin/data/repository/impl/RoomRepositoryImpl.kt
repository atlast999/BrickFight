package data.repository.impl

import data.dto.CreateRoomRequest
import data.dto.CreateRoomResponse
import data.dto.RoomDto
import data.dto.wrapper.AppResponse
import data.dto.wrapper.PagingModel
import data.repository.RoomRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.ClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.get
import io.ktor.client.request.port
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow

class RoomRepositoryImpl(
    private val client: HttpClient,
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
    }

    override suspend fun leaveRoom(roomId: Int) {
        client.put("room/$roomId/leave")
    }

    override suspend fun startChat(roomId: Int): SocketChannel {
        return SocketChannel(session = client.webSocketSession(
            "ws/5/mem1"
        ) {
            port = 8080
        })
    }
}

class SocketChannel(private val session: ClientWebSocketSession) {
    fun receiveAsFlow() =
        session.incoming.receiveAsFlow().mapNotNull { (it as? Frame.Text)?.readText() }

    suspend fun send(value: String) {
        session.send(Frame.Text(text = value))
    }

    suspend fun close() = session.close(
        reason = CloseReason(
            code = CloseReason.Codes.NORMAL,
            message = "Client close"
        )
    )
}