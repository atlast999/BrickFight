import androidx.compose.ui.graphics.ImageBitmap

enum class Platform {
    Android,
    Ios,
    Desktop,
    Web,
}


expect fun getPlatform(): Platform

//@Composable
//expect fun CameraImage(
//    onImage: (ByteArray) -> Unit,
//)

expect fun ByteArray.toImageBitmap(): ImageBitmap