package com.ulsee.shiba.utils

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

class ImageCompressor private constructor(var file: File, var ctx: Context) {
    internal class Size(var width: Int, var height: Int)

    @Throws(IOException::class)
    fun compress(): File {
        var fileName: String
        val fileExtension = "jpg"
        val idx = file.name.lastIndexOf(".")
        fileName = if (idx >= 0) {
            file.name.substring(0, idx)
        } else {
            file.name
        }
        if (fileName.length < 3) {
            fileName = "___$fileName"
        }
        val tmp = File.createTempFile(fileName, ".$fileExtension")
        val fos = FileOutputStream(tmp)

        // 1. get bitmap size
        val size = readSize(file)
        // 2. read bitmap
        val scaledBitmap = read(file, size)
        // 3. check exif
        val bitmap = rotate(scaledBitmap, file)
        // 4. compress
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)
        fos.flush()
        fos.close()
        return tmp
    }

    private fun readSize(file: File): Size {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.path, options)
        return Size(options.outWidth, options.outHeight)
    }

    private fun read(file: File, imgSize: Size): Bitmap {
        // 1. get options
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = false

        // 2. get file
        var inSampleSize = 1
        while (!(imgSize.height / inSampleSize <= 1920 && imgSize.width / inSampleSize <= 1080) &&
            !(imgSize.height / inSampleSize <= 1080 && imgSize.width / inSampleSize <= 1920)
        ) {
            inSampleSize *= 2
        }
        options.inSampleSize = inSampleSize
        //        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(file.path, options)
    }

    @Throws(IOException::class)
    private fun rotate(bitmap: Bitmap, file: File): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val rotatedBitmap: Bitmap
        val orientation = getExifOrientationFromFile(file)
        val matrix = Matrix()
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180 || orientation == ExifInterface.ORIENTATION_FLIP_VERTICAL) {
            matrix.postRotate(180f)
        }
        if (orientation == ExifInterface.ORIENTATION_TRANSPOSE || orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            matrix.postRotate(90f)
        }
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270 || orientation == ExifInterface.ORIENTATION_TRANSVERSE) {
            matrix.postRotate(270f)
        }
        if (orientation == ExifInterface.ORIENTATION_FLIP_VERTICAL || orientation == ExifInterface.ORIENTATION_FLIP_HORIZONTAL) {
            matrix.postScale(-1f, 1f, width / 2.toFloat(), height / 2.toFloat())
        }
        if (orientation == ExifInterface.ORIENTATION_TRANSPOSE || orientation == ExifInterface.ORIENTATION_TRANSVERSE) {
            matrix.postScale(1f, -1f, width / 2.toFloat(), height / 2.toFloat())
        }
        rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        return rotatedBitmap
    }

    companion object {
        fun with(file: File, ctx: Context): ImageCompressor {
            return ImageCompressor(file, ctx)
        }

        @Throws(IOException::class)
        fun getExifOrientationFromFile(file: File): Int {
            val exif = ExifInterface(file.path)
            return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
        }
    }

}
