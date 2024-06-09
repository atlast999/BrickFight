package presentation.room.call

import CameraImage
import Platform
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import getPlatform
import toImageBitmap

@Composable
fun CallUI(
    incomingFrame: ByteArray?,
    onFrameReceived: (ByteArray) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (incomingFrame != null) {
            Image(
                modifier = Modifier.fillMaxSize(),
                bitmap = incomingFrame.toImageBitmap(),
                contentDescription = null,
            )
        }
        if (getPlatform() == Platform.Android) {
            CameraImage(onFrameReceived)
        }
    }
}