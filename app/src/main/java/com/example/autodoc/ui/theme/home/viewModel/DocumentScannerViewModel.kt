package com.example.autodoc.ui.theme.home.viewModel

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.compose.ui.Modifier
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DocumentScannerViewModel: ViewModel() {

    private val _result: MutableLiveData<GmsDocumentScanningResult?> = MutableLiveData(null)
    val result: LiveData<GmsDocumentScanningResult?> = _result
    private val _pdf: MutableLiveData<File?> = MutableLiveData(null)
    val pdf: LiveData<File?> = _pdf

    fun setResult(result: GmsDocumentScanningResult) {
        result.pdf?.uri?.path?.let { path ->
            val file = File(path)
            _pdf.value = file
        }
        _result.value = result
    }

    fun clearResult() {
        _result.value = null
    }

    fun sharePdf(context: Context) {
        pdf.value?.let { file ->
            val externalUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_STREAM, externalUri)
                type = "application/pdf"
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share PDF"))
        }
    }

    fun getImages(): ArrayList<Uri> {
        val images: ArrayList<Uri> = ArrayList()
        result.value?.pages?.let { pages->
            for (page in pages) {
                page?.imageUri?.path?.let {
                    images.add(page.imageUri)
                }
            }
        }

        return images
    }

    fun shareImages(context: Context) {
        val files = getImages()
        val fileUris = files.map { uri ->
            val file = File(uri.path!!)
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            action = Intent.ACTION_SEND_MULTIPLE
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(fileUris))
            type = "image/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share Images"))
    }

    fun getAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown"
        }
    }

    fun clearCache(context: Context) {
        val cache = context.cacheDir
        if (cache.exists()) {
            cache.deleteRecursively()
        }
    }

    @Throws(IOException::class)
    fun copyAndRenameFile(context: Context, newFileName: String) {
        pdf.value?.let { file ->
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(file.toUri())
            val cacheDir = context.cacheDir
            _pdf.value = File(cacheDir, newFileName)

            FileOutputStream(pdf.value).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
        }
    }
}