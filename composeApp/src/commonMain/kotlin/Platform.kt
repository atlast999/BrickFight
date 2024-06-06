import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun convertByteArrayToImageBitmap(image: ByteArray): ImageBitmap


@Composable
expect fun CameraImage(
//    onImage: (CMPImage) -> Unit,
    onImage: (ByteArray) -> Unit,
)