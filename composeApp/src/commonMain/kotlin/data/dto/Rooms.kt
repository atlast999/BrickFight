package data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CreateRoomRequest(
    @SerialName("name") val name: String,
)

@Serializable
data class CreateRoomResponse(
    @SerialName("id") val id: Int,
)

@Serializable
data class RoomDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("members") val members: List<MemberDto>,
)

@Serializable
data class MemberDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
)
