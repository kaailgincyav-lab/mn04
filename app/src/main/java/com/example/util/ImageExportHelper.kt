package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object ImageExportHelper {

    /**
     * Saves the kaleidoscope bitmap to the system gallery under Pictures/Kaleidoskop.
     */
    fun saveBitmapToGallery(context: Context, bitmap: Bitmap, titlePrefix: String = "Kaleidoskop"): Result<Uri> {
        return runCatching {
            val filename = "${titlePrefix}_${System.currentTimeMillis()}.png"
            val contentResolver = context.contentResolver

            val imageUri: Uri?
            val outputStream: OutputStream?

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/Kaleidoskop")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }

                val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    ?: throw IllegalStateException("MediaStore kaydı oluşturulamadı")
                imageUri = uri
                outputStream = contentResolver.openOutputStream(uri)
                    ?: throw IllegalStateException("Çıktı akışı açılamadı")

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
                outputStream.close()

                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                contentResolver.update(uri, contentValues, null, null)
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val kaleidoscopeDir = File(imagesDir, "Kaleidoskop").apply {
                    if (!exists()) mkdirs()
                }
                val imageFile = File(kaleidoscopeDir, filename)
                outputStream = FileOutputStream(imageFile)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
                outputStream.close()

                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                }
                imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    ?: Uri.fromFile(imageFile)
            }

            imageUri
        }
    }

    /**
     * Prepares bitmap for sharing via Android system share sheet.
     */
    fun shareBitmap(context: Context, bitmap: Bitmap, title: String = "Kaleidoskop Eserim") {
        runCatching {
            val cacheFolder = File(context.cacheDir, "kaleidoscope_export").apply {
                if (!exists()) mkdirs()
            }
            val tempFile = File(cacheFolder, "kaleidoscope_share_${System.currentTimeMillis()}.png")
            FileOutputStream(tempFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
            }

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                tempFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, "Kaleidoskop ile oluşturduğum simetrik sanat eseri! ✨")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Kaleidoskop Desenini Paylaş"))
        }
    }
}
