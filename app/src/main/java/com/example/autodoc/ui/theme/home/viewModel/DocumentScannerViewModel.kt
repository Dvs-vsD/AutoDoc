package com.example.autodoc.ui.theme.home.viewModel

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.Modifier
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.io.path.fileVisitor

class DocumentScannerViewModel : ViewModel() {

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
            val externalUri =
                FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

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
        result.value?.pages?.let { pages ->
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
            packageInfo?.versionName ?: ""
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown"
        }
    }

    fun clearCache(context: Context) {
        val cache = context.cacheDir
        if (cache?.exists() == true) {
            cache.deleteRecursively()
        }
    }

    @Throws(IOException::class)
    fun copyAndRenameFile(context: Context, newFileName: String) {
        try {
            pdf.value?.let { file ->
                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(file.toUri())
                val cacheDir = context.cacheDir
                _pdf.value = File(cacheDir, newFileName)

                FileOutputStream(pdf.value).use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to Rename the file: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun savePdfToDevice(context: Context) {
        val fileName = pdf.value?.name ?: "Raven_${System.currentTimeMillis()}.pdf"

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Scoped Storage (Android 10+)
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_DOCUMENTS + "/RavenScanDocs"
                    )
                }

                val uri = context.contentResolver.insert(
                    MediaStore.Files.getContentUri("external"),
                    contentValues
                )
                    ?: throw IOException("Failed to create MediaStore entry")

                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    pdf.value?.inputStream()?.use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                } ?: throw IOException("Failed to open output stream")

                Toast.makeText(context, "PDF saved successfully!", Toast.LENGTH_SHORT).show()
            } else {
                // For Android 9 and below (Legacy Storage)
                val downloadsDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val savedFile = File(downloadsDir, fileName)

                pdf.value?.inputStream()?.use { input ->
                    FileOutputStream(savedFile).use { output ->
                        input.copyTo(output)
                    }
                }

                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(savedFile.absolutePath),
                    null,
                    null
                )

                Toast.makeText(context, "PDF saved in Downloads!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error saving PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveImagesToGallery(context: Context) {
        val contentResolver = context.contentResolver
        val name = pdf.value?.name?.removeSuffix(".pdf")?.plus(".jpg")
            ?: "Raven_${System.currentTimeMillis()}.pdf"

        for ((idx, uri) in getImages().withIndex()) {
            try {
                // Convert URI to Bitmap
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

                val fileName = "${name}_$idx.jpg"

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Scoped Storage (Android 10+)
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        put(
                            MediaStore.Images.Media.RELATIVE_PATH,
                            Environment.DIRECTORY_PICTURES + "/RavenScanDocs"
                        )
                        put(MediaStore.Images.Media.IS_PENDING, 1)
                    }

                    val imageUri = contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                    )
                        ?: throw IOException("Failed to create MediaStore entry")

                    contentResolver.openOutputStream(imageUri)?.use { outputStream ->
                        if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)) {
                            throw IOException("Failed to save bitmap")
                        }
                    } ?: throw IOException("Failed to open output stream")

                    // Mark as not pending
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    contentResolver.update(imageUri, contentValues, null, null)

                } else {
                    // Legacy Storage (Android 9 and below)
                    val picturesDir =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    val savedFile = File(picturesDir, fileName)

                    FileOutputStream(savedFile).use { outputStream ->
                        if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)) {
                            throw IOException("Failed to save bitmap")
                        }
                    }

                    MediaScannerConnection.scanFile(
                        context,
                        arrayOf(savedFile.absolutePath),
                        null,
                        null
                    )
                }

                Toast.makeText(context, "Image saved successfully!", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(context, "Error saving image: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


}