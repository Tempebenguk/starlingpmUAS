package com.example.proyekstarling

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.ImageView
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MediaHelper (private val context:Context) {
    fun getOutputMediaFileUri(): Uri {
        val mediaFile = getOutputMediaFile()
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", mediaFile)
    }

    fun getRcCamera() = 1
    fun getRcGallery() = 2

    private fun getOutputMediaFile(): File {
        val mediaStorageDir = File(context.getExternalFilesDir(null), "Camera")

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                throw IOException("Failed to create directory")
            }
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File(mediaStorageDir.path + File.separator + "IMG_" + timeStamp + ".jpg")
    }

    fun getBitmapToString(uri: Uri, imageView: ImageView): String {
        val imageStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(imageStream)
        imageView.setImageBitmap(bitmap)
        return encodeImage(bitmap)
    }

    private fun encodeImage(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}