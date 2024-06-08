package com.atlast.brickfight

import App
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import domain.ChatMessage
import domain.RoomMember
import io.ktor.util.moveToByteArray
import java.nio.ByteBuffer
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : ComponentActivity() {

    fun Context.checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun AppUI(modifier: Modifier = Modifier) {
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
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val lensFacing = remember {
                mutableIntStateOf(CameraSelector.LENS_FACING_BACK)
            }
            val cameraOutput = remember {
                mutableStateOf<ImageMetadata?>(null)
            }
            CameraPreview(
                modifier = Modifier
                    .background(color = Color.Red)
                    .fillMaxWidth()
                    .aspectRatio(1f),
                lensFacing = lensFacing.intValue,
                onNewBitmap = {
                    cameraOutput.value = it
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedButton(onClick = {
                lensFacing.intValue = if (lensFacing.intValue == CameraSelector.LENS_FACING_BACK) {
                    CameraSelector.LENS_FACING_FRONT
                } else {
                    CameraSelector.LENS_FACING_BACK
                }
            }) {
                Text(text = "Change")
            }
            cameraOutput.value?.let {
                Image(
                    modifier = Modifier.rotate(
                        if (lensFacing.intValue == CameraSelector.LENS_FACING_FRONT) {
                            -90f
                        } else {
                            90f
                        }
                    ),
                    bitmap = it.toBitmap().asImageBitmap(),
                    contentDescription = null
                )
            }

        }
    }


    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    @Composable
    fun CameraPreview(
        modifier: Modifier = Modifier,
        lensFacing: Int,
        onNewBitmap: (ImageMetadata) -> Unit = {},
    ) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val preview = remember {
            Preview.Builder().build()
        }
        val previewView = remember {
            PreviewView(context)
        }

        val imageAnalysis = remember {
            ImageAnalysis.Builder()
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888) //default YUV
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .apply {
                    setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                        imageProxy.toImageMetadata().let(onNewBitmap)
                        imageProxy.close()
                    }
                }
        }

        LaunchedEffect(lensFacing) {
            val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
            context.getCameraProvider().apply {
                unbindAll()
                bindToLifecycle(
                    lifecycleOwner = lifecycleOwner,
                    cameraSelector = cameraSelector,
                    imageAnalysis,
//                    preview.apply {
//                        setSurfaceProvider(previewView.surfaceProvider)
//                    },
                )
            }
        }
        AndroidView(
            modifier = modifier.padding(30.dp),
            factory = {
                previewView.apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
        )
    }

    private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
        suspendCoroutine { cont ->
            ProcessCameraProvider.getInstance(this).apply {
                addListener(
                    {
                        cont.resume(get())
                    },
                    ContextCompat.getMainExecutor(this@MainActivity)
                )
            }
        }

    class ImageMetadata(
        val width: Int,
        val height: Int,
        val bytes: ByteArray,
    ) {

        fun toBitmap() = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            copyPixelsFromBuffer(ByteBuffer.wrap(bytes))
        }
    }

    private fun ImageProxy.toImageMetadata() = ImageMetadata(
        width = width,
        height = height,
        bytes = planes[0].buffer.moveToByteArray(),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            AppUI()
            App()
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
)
@Composable
fun AppAndroidPreview() {
    val list = remember {
        mutableStateListOf(
            ChatMessage(
                sender = RoomMember(
                    id = 1,
                    name = "Fake member 1",
                ),
                content = "Hello",
                timestamp = System.currentTimeMillis(),
            ),
            ChatMessage(
                sender = null,
                content = "Hi there",
                timestamp = System.currentTimeMillis(),
            )
        )
    }
//    Column {
//        list.forEach {
//            if (it.isIncoming) {
//                IncomingMessageItem(sender = it.sender?.name ?: "Unknown", content = it.content)
//            } else {
//                OutgoingMessageItem(content = it.content)
//            }
//        }
//    }
}

