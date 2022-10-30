package com.zekierciyas.fancyfilter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.ParcelFileDescriptor
import java.io.FileDescriptor
import java.io.IOException

fun Uri.uriToBitmap(context: Context): Bitmap? {
    return try {
        val parcelFileDescriptor: ParcelFileDescriptor? =
            context.contentResolver.openFileDescriptor(this, "r")
        val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        image
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

fun Bitmap.rotateHorizontally(): Bitmap {
    return if (this.height < this.width) {
        this.rotate(90F)
    } else this
}

fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}