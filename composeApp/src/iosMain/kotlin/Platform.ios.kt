import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

actual fun getPlatform(): Platform = Platform.Ios

actual fun ByteArray.toImageBitmap(): ImageBitmap =
    Image.makeFromEncoded(this).toComposeImageBitmap()

@Composable
actual fun CameraImage(
    onImage: (ByteArray) -> Unit,
) {
}