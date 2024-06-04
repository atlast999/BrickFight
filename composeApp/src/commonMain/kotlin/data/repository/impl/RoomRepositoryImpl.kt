package data.repository.impl

import data.dto.CreateRoomRequest
import data.dto.CreateRoomResponse
import data.dto.RoomDto
import data.dto.wrapper.AppResponse
import data.dto.wrapper.PagingModel
import data.repository.RoomRepository
import domain.Room
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

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
}