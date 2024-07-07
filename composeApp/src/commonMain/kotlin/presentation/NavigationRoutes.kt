package presentation

import kotlinx.serialization.Serializable

object Route {
    @Serializable
    object Login

    @Serializable
    object SignUp

    @Serializable
    object RoomList

    @Serializable
    data class Room(val id: Int)

    @Serializable
    object Game

    @Serializable
    object Call
}

