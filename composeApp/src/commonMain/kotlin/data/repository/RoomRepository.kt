package data.repository

import data.dto.ChatMessageDto
import data.dto.CreateRoomRequest
import data.dto.CreateRoomResponse
import data.dto.RoomDto
import data.dto.wrapper.PagingModel
import data.repository.impl.SocketChannel
import kotlinx.coroutines.flow.Flow

interface RoomRepository {
    suspend fun fetchRooms(): PagingModel<RoomDto>
    suspend fun createRoom(request: CreateRoomRequest): CreateRoomResponse
    suspend fun getRoom(roomId: Int): RoomDto
    suspend fun joinRoom(roomId: Int)
    suspend fun leaveRoom(roomId: Int)
    suspend fun startChat(roomId: Int): SocketChannel
    fun flowChatMessages(roomId: Int): Flow<ChatMessageDto>
    suspend fun sendMessage(message: String)
}
