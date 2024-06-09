import android.Manifest
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.os.Build
import androidx.annotation.IntRange
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.min

actual fun getPlatform(): Platform = Platform.Android

class CustomLifecycle : LifecycleOwner {
    private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    init {;
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    fun markAsStart() {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
    }

    fun markAsDestroyed() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }

    override val lifecycle: Lifecycle = lifecycleRegistry
}

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.R)
@Composable
actual fun CameraImage(
    onImage: (ByteArray) -> Unit,
) {
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    if (cameraPermissionState.status.isGranted.not()) {
        if (cameraPermissionState.status.shouldShowRationale) {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Text(text = "No permission No camera")
            }
        }
        LaunchedEffect(cameraPermissionState) {
            cameraPermissionState.launchPermissionRequest()
        }
        return
    }
    val context = LocalContext.current
    val customLifecycle = remember { CustomLifecycle() }
    DisposableEffect(key1 = customLifecycle) {
        customLifecycle.markAsStart()
        onDispose {
            customLifecycle.markAsDestroyed()
        }
    }
    LaunchedEffect(true) {
        cameraImage(context, customLifecycle, onImage)
    }
}

fun yuvImageToJpegByteArray(
    image: ImageProxy,
    cropRect: Rect? = null,
    @IntRange(from = 1, to = 100) jpegQuality: Int = 100,
): ByteArray {
    require(image.format == ImageFormat.YUV_420_888) { "Incorrect image format of the input image proxy: " + image.format }

    val yuvBytes = yuv_420_888toNv21(image)
    val yuv = YuvImage(
        yuvBytes, ImageFormat.NV21, image.width, image.height,
        null
    )
    val byteArrayOutputStream = ByteArrayOutputStream()
    val rect = cropRect ?: Rect(0, 0, image.width, image.height)
    val success =
        yuv.compressToJpeg(rect, jpegQuality, byteArrayOutputStream)
    if (!success) {
        throw IllegalArgumentException("Failed to compress YUV image to JPEG")
    }
    return byteArrayOutputStream.toByteArray()
}

private fun yuv_420_888toNv21(image: ImageProxy): ByteArray {
    val yPlane = image.planes[0]
    val uPlane = image.planes[1]
    val vPlane = image.planes[2]

    val yBuffer = yPlane.buffer
    val uBuffer = uPlane.buffer
    val vBuffer = vPlane.buffer
    yBuffer.rewind()
    uBuffer.rewind()
    vBuffer.rewind()

    val ySize = yBuffer.remaining()

    var position = 0
    // TODO(b/115743986): Pull these bytes from a pool instead of allocating for every image.
    val nv21 = ByteArray(ySize + (image.width * image.height / 2))

    // Add the full y buffer to the array. If rowStride > 1, some padding may be skipped.
    for (row in 0 until image.height) {
        yBuffer[nv21, position, image.width]
        position += image.width
        yBuffer.position(
            min(ySize.toDouble(), (yBuffer.position() - image.width + yPlane.rowStride).toDouble())
                .toInt()
        )
    }

    val chromaHeight = image.height / 2
    val chromaWidth = image.width / 2
    val vRowStride = vPlane.rowStride
    val uRowStride = uPlane.rowStride
    val vPixelStride = vPlane.pixelStride
    val uPixelStride = uPlane.pixelStride

    // Interleave the u and v frames, filling up the rest of the buffer. Use two line buffers to
    // perform faster bulk gets from the byte buffers.
    val vLineBuffer = ByteArray(vRowStride)
    val uLineBuffer = ByteArray(uRowStride)
    for (row in 0 until chromaHeight) {
        vBuffer[vLineBuffer, 0, min(vRowStride.toDouble(), vBuffer.remaining().toDouble()).toInt()]
        uBuffer[uLineBuffer, 0, min(uRowStride.toDouble(), uBuffer.remaining().toDouble()).toInt()]
        var vLineBufferPosition = 0
        var uLineBufferPosition = 0
        for (col in 0 until chromaWidth) {
            nv21[position++] = vLineBuffer[vLineBufferPosition]
            nv21[position++] = uLineBuffer[uLineBufferPosition]
            vLineBufferPosition += vPixelStride
            uLineBufferPosition += uPixelStride
        }
    }

    return nv21
}

@RequiresApi(Build.VERSION_CODES.R)
private suspend fun cameraImage(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    onNewImage: (ByteArray) -> Unit,
) {
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val imageAnalysis = ImageAnalysis.Builder()
        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888) //default YUV
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .apply {
            setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                onNewImage(
                    yuvImageToJpegByteArray(image = imageProxy)
                )
                imageProxy.close()
            }
        }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()
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


actual fun ByteArray.toImageBitmap(): ImageBitmap =
    BitmapFactory.decodeByteArray(this, 0, size).asImageBitmap()