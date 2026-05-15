package com.example.smartsales.util

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class BarcodeAnalyzer(
    // Esta función lambda enviará el código escaneado de vuelta a nuestra UI
    private val onBarcodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {

    // Configuramos ML Kit para que lea todos los formatos estándar de códigos de barras y QR
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image

        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { valor ->
                            onBarcodeScanned(valor)
                        }
                    }
                }
                .addOnFailureListener {
                    // Aquí podríamos registrar un error silencioso si falla el análisis
                }
                .addOnCompleteListener {
                    // IMPORTANTE: Liberar la imagen procesada para que CameraX nos envíe el siguiente cuadro.
                    // Si omites esto, la cámara se congelará en el primer segundo.
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}