package com.example.phonepe.ui.screens

import android.Manifest
import android.net.Uri
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.phonepe.ui.navigation.Screen
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.net.URLEncoder
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember { mutableStateOf(false) }
    var torchEnabled by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("Point your camera at a QR code.") }
    var navigating by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (!granted) statusText = "Camera permission is required to scan."
    }

    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        // Process still image with ML Kit
        val image = InputImage.fromFilePath(context, uri)
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        val scanner = BarcodeScanning.getClient(options)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                val upi = barcodes.firstOrNull { it.rawValue?.startsWith("upi://pay", true) == true }
                val raw = upi?.rawValue
                if (!raw.isNullOrBlank()) {
                    handleResult(raw, navController) { navigating = it }
                } else {
                    statusText = if (barcodes.isNotEmpty()) "Scanned: ${barcodes.first().rawValue ?: "Unsupported"}" else "No QR detected in image."
                }
            }
            .addOnFailureListener { e ->
                statusText = e.localizedMessage ?: "Failed to process image"
            }
    }

    LaunchedEffect(Unit) { permissionLauncher.launch(Manifest.permission.CAMERA) }

    // CameraX state
    var camera by remember { mutableStateOf<Camera?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (hasCameraPermission) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    val previewView = PreviewView(it).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(it)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also { p ->
                            p.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        @Suppress("DEPRECATION")
                        val analysis = ImageAnalysis.Builder()
                            .setTargetResolution(Size(1280, 720))
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()

                        val options = BarcodeScannerOptions.Builder()
                            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                            .build()
                        val scanner = BarcodeScanning.getClient(options)

                        analysis.setAnalyzer(ContextCompat.getMainExecutor(it)) { imageProxy ->
                            processFrame(
                                scanner = scanner,
                                imageProxy = imageProxy,
                                onUpi = { raw ->
                                    if (!navigating) {
                                        handleResult(raw, navController) { navigating = it }
                                    }
                                },
                                onError = { errCode ->
                                    if (errCode != CommonStatusCodes.CANCELED) {
                                        statusText = errMsg(errCode)
                                    }
                                }
                            )
                        }

                        try {
                            val selector = CameraSelector.DEFAULT_BACK_CAMERA
                            cameraProvider.unbindAll()
                            camera = cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview, analysis)
                            camera?.cameraControl?.enableTorch(torchEnabled)
                        } catch (e: Exception) {
                            statusText = e.localizedMessage ?: "Camera init failed"
                        }
                    }, ContextCompat.getMainExecutor(it))
                    previewView
                }
            )

            // Viewfinder overlay
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.fillMaxWidth(0.75f).aspectRatio(1f)) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val stroke = 6.dp.toPx()
                        val len = 24.dp.toPx()
                        val c = Color(0xFFBB00FF)
                        // TL
                        drawLine(c, Offset(0f, stroke / 2), Offset(len, stroke / 2), stroke, StrokeCap.Round)
                        drawLine(c, Offset(stroke / 2, 0f), Offset(stroke / 2, len), stroke, StrokeCap.Round)
                        // TR
                        drawLine(c, Offset(size.width - len, stroke / 2), Offset(size.width, stroke / 2), stroke, StrokeCap.Round)
                        drawLine(c, Offset(size.width - stroke / 2, 0f), Offset(size.width - stroke / 2, len), stroke, StrokeCap.Round)
                        // BL
                        drawLine(c, Offset(0f, size.height - stroke / 2), Offset(len, size.height - stroke / 2), stroke, StrokeCap.Round)
                        drawLine(c, Offset(stroke / 2, size.height - len), Offset(stroke / 2, size.height), stroke, StrokeCap.Round)
                        // BR
                        drawLine(c, Offset(size.width - len, size.height - stroke / 2), Offset(size.width, size.height - stroke / 2), stroke, StrokeCap.Round)
                        drawLine(c, Offset(size.width - stroke / 2, size.height - len), Offset(size.width - stroke / 2, size.height), stroke, StrokeCap.Round)
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.align(Alignment.Center).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Requesting camera permission…", color = Color.White)
                CircularProgressIndicator()
            }
        }

        // Top overlay controls: small back button and torch/upload
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Row {
                IconButton(onClick = { pickImage.launch("image/*") }) {
                    Icon(Icons.Filled.FileUpload, contentDescription = "Upload", tint = Color.White)
                }
                IconButton(onClick = {
                    torchEnabled = !torchEnabled
                    camera?.cameraControl?.enableTorch(torchEnabled)
                }) {
                    Icon(if (torchEnabled) Icons.Filled.FlashOn else Icons.Filled.FlashOff, contentDescription = "Torch", tint = Color.White)
                }
            }
        }

        // Bottom status overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.3f))
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = statusText,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )
        }
    }
}

private fun processFrame(
    scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    imageProxy: ImageProxy,
    onUpi: (String) -> Unit,
    onError: (Int) -> Unit
) {
    val image = imageProxy.toInputImage()
    scanner.process(image)
        .addOnSuccessListener { barcodes ->
            val upi = barcodes.firstOrNull { it.rawValue?.startsWith("upi://pay", true) == true }
            val raw = upi?.rawValue
            if (!raw.isNullOrBlank()) onUpi(raw)
        }
        .addOnFailureListener { e ->
            val code = (e as? ApiException)?.statusCode ?: -1
            onError(code)
        }
        .addOnCompleteListener { imageProxy.close() }
}

private fun ImageProxy.toInputImage(): InputImage {
    val yBuffer = planes[0].buffer
    val uBuffer = planes[1].buffer
    val vBuffer = planes[2].buffer
    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)
    val jpegBytes = out.toByteArray()
    val bitmap = BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.size)
    return InputImage.fromBitmap(bitmap, imageInfo.rotationDegrees)
}

private fun handleResult(raw: String, navController: NavController, setNavigating: (Boolean) -> Unit) {
    val uri = Uri.parse(raw)
    val upiId = uri.getQueryParameter("pa").orEmpty()
    val name = uri.getQueryParameter("pn").orEmpty()
    if (upiId.isNotBlank()) {
        val encName = URLEncoder.encode(name, "UTF-8")
        val encUpi = URLEncoder.encode(upiId, "UTF-8")
        val encQrData = URLEncoder.encode(raw, "UTF-8")
        setNavigating(true)
        navController.navigate(Screen.Payment.createRoute(encName, encUpi, encQrData))
    }
}

private fun errMsg(code: Int): String = when (code) {
    CommonStatusCodes.NETWORK_ERROR -> "Network error"
    else -> "Scan error"
}
