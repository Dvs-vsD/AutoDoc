package com.example.autodoc

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.FileProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import com.example.autodoc.ui.theme.AutoDocTheme
import com.example.autodoc.ui.theme.home.HomeScreen
import com.example.autodoc.ui.theme.home.Title
import com.example.autodoc.ui.theme.home.viewModel.DocumentScannerViewModel
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import java.io.File

class MainActivity : ComponentActivity() {
    private lateinit var resultLauncher: ActivityResultLauncher<IntentSenderRequest>
    private val docScanViewModel: DocumentScannerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            handleScannerResult(result)
        }

        setContent {
            AutoDocTheme {
                HomeScreen(onScanButtonClicked = { onScanButtonClicked() }, docScanViewModel, this)
            }
        }
    }

    private fun onScanButtonClicked() {
        val options =
            GmsDocumentScannerOptions.Builder()
                .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
                .setResultFormats(
                    RESULT_FORMAT_JPEG,
                    RESULT_FORMAT_PDF
                )
                .setGalleryImportAllowed(true)

        GmsDocumentScanning.getClient(options.build())
            .getStartScanIntent(this)
            .addOnSuccessListener { intentSender: IntentSender ->
                resultLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to Scan: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun handleScannerResult(activityResult: ActivityResult) {
        val resultCode = activityResult.resultCode
        val result = GmsDocumentScanningResult.fromActivityResultIntent(activityResult.data)
        if (resultCode == RESULT_OK && result != null) {
            docScanViewModel.setResult(result)
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Scanning cancelled", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Something went wrong !!! Please Try Again", Toast.LENGTH_SHORT).show()
        }
    }
}

