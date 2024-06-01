package com.atlast.brickfight

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}

//class AppActivity : ComponentActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            AppTheme {
//                AppUI()
//            }
//        }
//    }

//    @OptIn(ExperimentalPermissionsApi::class)
//    @Composable
//    fun AppUI(modifier: Modifier = Modifier) {
//        val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
//        if (cameraPermissionState.status.isGranted.not()) {
//            if (cameraPermissionState.status.shouldShowRationale) {
//                Box(
//                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
//                ) {
//                    Text(text = "No permission No camera")
//                }
//            }
//            LaunchedEffect(cameraPermissionState) {
//                cameraPermissionState.launchPermissionRequest()
//            }
//            return
//        }
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            val lensFacing = remember {
//                mutableIntStateOf(CameraSelector.LENS_FACING_BACK)
//            }
//            val cameraOutput = remember {
//                mutableStateOf<ImageMetadata?>(null)
//            }
//            CameraPreview(
//                modifier = Modifier
//                    .background(color = Color.Red)
//                    .fillMaxWidth()
//                    .aspectRatio(1f),
//                lensFacing = lensFacing.intValue,
//                onNewBitmap = {
//                    cameraOutput.value = it
//                }
//            )
//            Spacer(modifier = Modifier.height(20.dp))
//            OutlinedButton(onClick = {
//                lensFacing.intValue = if (lensFacing.intValue == CameraSelector.LENS_FACING_BACK) {
//                    CameraSelector.LENS_FACING_FRONT
//                } else {
//                    CameraSelector.LENS_FACING_BACK
//                }
//            }) {
//                Text(text = "Change")
//            }
//            cameraOutput.value?.let {
//                Image(
//                    modifier = Modifier.rotate(
//                        if (lensFacing.intValue == CameraSelector.LENS_FACING_FRONT) {
//                            -90f
//                        } else {
//                            90f
//                        }
//                    ),
//                    bitmap = it.toBitmap().asImageBitmap(),
//                    contentDescription = null
//                )
//            }
//        }
//    }
//
//
//    @androidx.annotation.OptIn(ExperimentalGetImage::class)
//    @Composable
//    fun CameraPreview(
//        modifier: Modifier = Modifier,
//        lensFacing: Int,
//        onNewBitmap: (ImageMetadata) -> Unit = {},
//    ) {
//        val context = LocalContext.current
//        val lifecycleOwner = LocalLifecycleOwner.current
//        val preview = remember {
//            Preview.Builder().build()
//        }
//        val previewView = remember {
//            PreviewView(context)
//        }
//
//        val imageAnalysis = remember {
//            ImageAnalysis.Builder()
//                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888) //default YUV
//                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                .build()
//                .apply {
//                    setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
//                        imageProxy.toImageMetadata().let(onNewBitmap)
//                        imageProxy.close()
//                    }
//                }
//        }
//
//        LaunchedEffect(lensFacing) {
//            val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
//            context.getCameraProvider().apply {
//                unbindAll()
//                bindToLifecycle(
//                    lifecycleOwner = lifecycleOwner,
//                    cameraSelector = cameraSelector,
//                    imageAnalysis,
//                    //                    preview.apply {
//                    //                        setSurfaceProvider(previewView.surfaceProvider)
//                    //                    },
//                )
//            }
//        }
//        AndroidView(
//            modifier = modifier.padding(30.dp),
//            factory = {
//                previewView.apply {
//                    layoutParams = ViewGroup.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT
//                    )
//                }
//            },
//        )
//    }
//
//    private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
//        suspendCoroutine { cont ->
//            ProcessCameraProvider.getInstance(this).apply {
//                addListener(
//                    {
//                        cont.resume(get())
//                    },
//                    ContextCompat.getMainExecutor(this@AppActivity)
//                )
//            }
//        }
//
//    class ImageMetadata(
//        val width: Int,
//        val height: Int,
//        val bytes: ByteArray,
//    ) {
//
//        fun toBitmap() = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
//            copyPixelsFromBuffer(ByteBuffer.wrap(bytes))
//        }
//    }
//    private fun ImageProxy.toImageMetadata() = ImageMetadata(
//        width = width,
//        height = height,
//        bytes = planes[0].buffer.moveToByteArray(),
//    ).also {
//        Log.d("HOANTAG", "toImageMetadata: width: ${it.width}, height: ${it.height}")
//    }
//
//    fun Int.toByteArray() : String {
//        Integer.parseInt("0001010", 2)
//        return this.toString(radix = 2)
//    }
//}