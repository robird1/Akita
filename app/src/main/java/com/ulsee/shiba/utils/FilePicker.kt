package com.ulsee.shiba.utils

import android.provider.OpenableColumns

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

class FilePickerHelper {
    var idx = 1000
    var pickedFiles: MutableList<File> = ArrayList()

    @Throws(IOException::class, Exception::class)
    fun putPickedFiles(
        ctx: Context,
        uris: List<Uri>
    ): ArrayList<File> {
        val tempFiles: ArrayList<File> = ArrayList()
        for (uri in uris) {
            val temp: File = tempSaveFileToInternal(ctx, uri)
            val compressedFile: File = ImageCompressor.with(temp, ctx).compress()
            tempFiles.add(compressedFile)
        }
        pickedFiles.addAll(tempFiles)
        return tempFiles
    }

    @Throws(IOException::class, Exception::class)
    fun putPickedFile(ctx: Context, uri: Uri): File {
        val temp: File = tempSaveFileToInternal(ctx, uri)
        val compressedFile: File = ImageCompressor.with(temp, ctx).compress()
        pickedFiles.add(compressedFile)
        temp.delete()
        return compressedFile
    }

    @Throws(IOException::class, Exception::class)
    private fun tempSaveFileToInternal(ctx: Context, uri: Uri): File {
        var copied: Long = 0
        val inputStream: InputStream? = ctx.getContentResolver().openInputStream(uri)
        val fileName = getFileName(ctx, uri)
        val splitName =
            splitFileName(fileName)
        val tempFile: File = File.createTempFile(splitName[0], splitName[1])
        //tempFile = rename(tempFile, fileName);
        //tempFile.deleteOnExit();
        val out = FileOutputStream(tempFile)
        if (inputStream != null) {
            copied = copy(inputStream, out)
            inputStream.close()
        }
        if (out != null) {
            out.close()
        }
        if (copied <= 0) throw Exception("無法讀取檔案")
        return tempFile
    }

    fun reset() {
        idx = 1000
        // remove all
        for (f in pickedFiles) {
            f.delete()
        }
        pickedFiles = ArrayList()
    }

    companion object {
        private var mInstance: FilePickerHelper? = null
        fun shared(): FilePickerHelper {
            if (mInstance == null) mInstance =
                FilePickerHelper()
            return mInstance!!
        }

        private fun splitFileName(fileName: String): Array<String> {
            var name = fileName
            var extension = ""
            val i = fileName.lastIndexOf(".")
            if (i != -1) {
                name = fileName.substring(0, i)
                extension = fileName.substring(i)
            }
            return arrayOf(name, extension)
        }

        @Throws(IOException::class)
        private fun copy(input: InputStream, output: OutputStream): Long {
            var count: Long = 0
            var n: Int
            val buffer = ByteArray(4096)
            while (-1 !== input.read(buffer).also { n = it } && n > 0) {
                output.write(buffer, 0, n)
                count += n.toLong()
            }
            output.flush()
            return count
        }

        private fun getFileName(context: Context, uri: Uri): String {
            var result: String? = null
            if (uri.getScheme().equals("content")) {
                val cursor: Cursor? = context.getContentResolver().query(uri, null, null, null, null)
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    if (cursor != null) {
                        cursor.close()
                    }
                }
            }
            if (result == null) {
                result = uri.getPath()
                val cut: Int = result!!.lastIndexOf(File.separator)
                if (cut != -1) {
                    result = result?.substring(cut + 1)
                }
            }
            return result!!
        }
    }
}
