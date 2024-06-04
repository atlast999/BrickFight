package data.repository

import data.dto.CreateRoomRequest
import data.dto.CreateRoomResponse
import data.dto.RoomDto
import data.dto.wrapper.PagingModel

interface RoomRepository {
    suspend fun fetchRooms(): PagingModel<RoomDto>
    suspend fun createRoom(request: CreateRoomRequest): CreateRoomResponse
    suspend fun getRoom(roomId: Int): RoomDto
    suspend fun joinRoom(roomId: Int)
    suspend fun leaveRoom(roomId: Int)
}
