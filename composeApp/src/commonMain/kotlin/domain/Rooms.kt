package domain

typealias RoomId = Int

data class Room(
    val id: RoomId = -1,
    val name: String,
    val members: List<RoomMember>,
)

data class RoomMember(
    val id: Int,
    val name: String,
)