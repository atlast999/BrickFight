package data.dto

import domain.ChatMessage
import domain.Room
import domain.RoomMember
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CreateRoomRequest(
    @SerialName("name") val name: String,
)

@Serializable
data class CreateRoomResponse(
    @SerialName("id") val roomId: Int,
)

@Serializable
data class RoomDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("members") val members: List<MemberDto>,
) {
    @Serializable
    data class MemberDto(
        @SerialName("id") val id: Int,
        @SerialName("name") val name: String,
    )
}

fun RoomDto.MemberDto.toMember(): RoomMember = RoomMember(
    id = id,
    name = name,
)

fun RoomDto.toRoom(): Room = Room(
    id = id,
    name = name,
    members = members.map(RoomDto.MemberDto::toMember),
)


@Serializable
data class ChatMessageDto(
    @SerialName("user_id") val userId: Int,
    @SerialName("content") val content: String,
    @SerialName("timestamp") val timestamp: Long,
)

fun ChatMessageDto.toChatMessage(sender: RoomMember?): ChatMessage = ChatMessage(
    sender = sender,
    content = content,
    timestamp = timestamp,
)


