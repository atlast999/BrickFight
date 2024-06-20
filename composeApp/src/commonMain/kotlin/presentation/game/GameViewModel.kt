package presentation.game

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {

    companion object {
        private const val BOARD_SIZE = 100
    }

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    fun moveUp() {
        val currentPosition = state.value.self.position
        val newPosition = currentPosition.copy(
            y = currentPosition.y.dec().coerceAtLeast(0)
        )
        move(newPosition)
    }

    fun moveDown() {
        val currentPosition = state.value.self.position
        val newPosition = currentPosition.copy(
            y = currentPosition.y.inc().coerceAtMost(BOARD_SIZE - 1)
        )
        move(newPosition)
    }

    fun moveLeft() {
        val currentPosition = state.value.self.position
        val newPosition = currentPosition.copy(
            x = currentPosition.x.dec().coerceAtLeast(0)
        )
        move(newPosition)
    }

    fun moveRight() {
        val currentPosition = state.value.self.position
        val newPosition = currentPosition.copy(
            x = currentPosition.x.inc().coerceAtMost(BOARD_SIZE - 1)
        )
        move(newPosition)
    }

    private fun move(position: BoardPosition) {
        _state.update {
            it.copy(self = it.self.copy(position = position))
        }
    }

    data class State(
        val self: Player = Player(
            name = "Player 1",
            position = BoardPosition(0, 0),
        ),
        val others: List<Player> = emptyList()
    )
}

data class Player(
    val name: String,
    val position: BoardPosition,
)

data class BoardPosition(
    val x: Int,
    val y: Int,
)