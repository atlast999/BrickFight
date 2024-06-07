import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

actual fun getPlatform(): Platform = Platform.Desktop

actual fun ByteArray.toImageBitmap(): ImageBitmap =
    Image.makeFromEncoded(this).toComposeImageBitmap()