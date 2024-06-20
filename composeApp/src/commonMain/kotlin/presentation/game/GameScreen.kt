package presentation.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import presentation.recomposeHighlighter

@Composable
fun GameUI(
    uiState: GameViewModel.State,
    onUp: () -> Unit,
    onDown: () -> Unit,
    onLeft: () -> Unit,
    onRight: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        PlayBoard(
            modifier = Modifier.width(250.dp).aspectRatio(1f),
            self = uiState.self,
        )
        Spacer(modifier = Modifier.size(50.dp))
        GameController(
            modifier = Modifier.width(150.dp).aspectRatio(1f),
            onUp = onUp,
            onDown = onDown,
            onLeft = onLeft,
            onRight = onRight,
        )
    }
}

@Composable
fun PlayBoard(
    modifier: Modifier = Modifier,
    self: Player,
) {
    BoxWithConstraints(
        modifier = modifier.background(
            color = MaterialTheme.colorScheme.onSurface,
        ).recomposeHighlighter(),
        contentAlignment = Alignment.TopStart,
    ) {
        val xOffset = maxWidth.div(100).times(self.position.x)
        val yOffset = maxHeight.div(100).times(self.position.y)
        Player(
            modifier = Modifier.size(70.dp).offset {
                IntOffset(
                    x = xOffset.roundToPx(),
                    y = yOffset.roundToPx(),
                )
            }.recomposeHighlighter(),
            playerName = self.name,
        )
    }
}

@Composable
fun Player(
    modifier: Modifier = Modifier,
    playerName: String,
) {
    Box(
        modifier = modifier.background(
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            shape = CircleShape,
        ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = playerName,
        )
    }
}


@Composable
fun GameController(
    modifier: Modifier = Modifier,
    onUp: () -> Unit,
    onDown: () -> Unit,
    onLeft: () -> Unit,
    onRight: () -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        ControllerButton(
            modifier = Modifier.size(50.dp).align(Alignment.TopCenter),
            icon = Icons.Default.ArrowUpward,
            onClick = onUp,
        )
        ControllerButton(
            modifier = Modifier.size(50.dp).align(Alignment.BottomCenter),
            icon = Icons.Default.ArrowDownward,
            onClick = onDown,
        )
        ControllerButton(
            modifier = Modifier.size(50.dp).align(Alignment.CenterStart),
            icon = Icons.AutoMirrored.Filled.ArrowLeft,
            onClick = onLeft,
        )
        ControllerButton(
            modifier = Modifier.size(50.dp).align(Alignment.CenterEnd),
            icon = Icons.AutoMirrored.Filled.ArrowRight,
            onClick = onRight,
        )
    }
}

@Composable
fun ControllerButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
        )
    }
}
