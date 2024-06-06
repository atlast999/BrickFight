import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

//actual fun convertByteArrayToImageBitmap(image: CMPImage): ImageBitmap {
//    val bitmap = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888).apply {
//        copyPixelsFromBuffer(ByteBuffer.wrap(image.bytes))
//    }
//    return bitmap.asImageBitmap()
//}

actual fun convertByteArrayToImageBitmap(image: ByteArray): ImageBitmap {
    val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
    return bitmap.asImageBitmap()
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
actual fun CameraImage(
//    onImage: (CMPImage) -> Unit,
    onImage: (ByteArray) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(true) {
        cameraImage(context, lifecycleOwner, onImage)
    }
}

@RequiresApi(Build.VERSION_CODES.R)
private fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, stream)
    return stream.toByteArray()
}

@RequiresApi(Build.VERSION_CODES.R)
private suspend fun cameraImage(
    context: Context,
    lifecycleOwner: LifecycleOwner,
//    onNewImage: (CMPImage) -> Unit.
    onNewImage: (ByteArray) -> Unit,
) {
    val lensFacing = CameraSelector.LENS_FACING_FRONT
    val imageAnalysis = ImageAnalysis.Builder()
        .setOutputImageFormat(ImageAnalysis.OUTPUT) //default YUV
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .apply {
            setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                imageProxy.toBitmap().toByteArray().let(onNewImage)
//                onNewImage(
//                    CMPImage(
//                        imageProxy.width,
//                        imageProxy.height,
//                        imageProxy.planes[0].buffer.moveToByteArray()
//                    )
//                )
                imageProxy.close()
            }
        }
    val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    context.getCameraProvider().apply {
        unbindAll()
        bindToLifecycle(
            lifecycleOwner = lifecycleOwner,
            cameraSelector = cameraSelector,
            imageAnalysis,
        )
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { cont ->
        ProcessCameraProvider.getInstance(this).apply {
            addListener(
                {
                    cont.resume(get())
                },
                ContextCompat.getMainExecutor(this@getCameraProvider)
            )
        }
    }