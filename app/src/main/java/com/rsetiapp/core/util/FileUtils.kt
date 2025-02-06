package com.rsetiapp.core.util

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class FileUtils {

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var context: Context? = null

        @SuppressLint("Range")
        fun getFileFromUri(context: Context, uri: Uri, ext: String? = null): File? {
            var path: String? = null
            var originalPath: String? = null
            Companion.context = context

            // DocumentProvider
            /*       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {*/
            if (DocumentsContract.isDocumentUri(context, uri)) {

                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        path = Environment.getExternalStorageDirectory()
                            .toString() + "/" + split[1]
                    }
                } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                    val id = DocumentsContract.getDocumentId(uri)
                    //val contentUri = ContentUris.withAppendedId(
                    //Uri.parse("content://downloads/public_downloads"),
                    //java.lang.Long.valueOf(id)
                    //)
                    path = getFilePath(uri)
                    /*path = getDataColumn(
                        context,
                        contentUri,
                        null,
                        null
                    )*/
                } else if (isMediaDocument(uri)) { // MediaProvider
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(
                        split[1]
                    )
                    /* path = getDataColumn(
                         context,
                         contentUri,
                         selection,
                         selectionArgs
                     )*/
                    path = getFilePath(uri /*if(ext != null) type else type*/ /*"documentFile"*/)
                } else if (isGoogleDrive(uri)) { // Google Drive
                    val TAG = "isGoogleDrive"
                    path = TAG
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(";").toTypedArray()
                    val acc = split[0]
                    val doc = split[1]

                    /*
                     * @details google drive document data. - acc , docId.
                     * */return saveFileIntoExternalStorageByUri(
                        context,
                        uri
                    )
                } // MediaStore (and general)
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {
                path = getFilePath(uri)
                /*getDataColumn(
                    context,
                    uri,
                    null,
                    null
                )*/
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                path = uri.path
            }
            return File(path)

            /*  else {
                  val cursor = context.contentResolver.query(uri, null, null, null, null)
                  File(cursor!!.getString(cursor.getColumnIndex("_data")))
              }*/
        }

        private fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        private fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }

        private fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }


        private fun isGoogleDrive(uri: Uri): Boolean {
            return uri.authority.equals("com.google.android.apps.docs.storage", ignoreCase = true)
        }

        private fun getDataColumn(
            context: Context, uri: Uri?, selection: String?,
            selectionArgs: Array<String>?
        ): String? {

            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)

            try {
                cursor =
                    context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val index = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(index)
                }
            } finally {
                cursor?.close()
            }
            return null
        }

        private fun getSuffix(fileUri: Uri): String {
            var ext = ""
            var type = context?.contentResolver?.getType(fileUri)
            when (type) {
                "image/jpeg" -> {
                    ext = ".jpg"
                }

                "image/png" -> {
                    ext = ".jpg"
                }

                "text/csv" -> {
                    ext = ".csv"
                }

                "application/pdf" -> {
                    ext = ".pdf"
                }

                "application/vnd.ms-excel" -> {
                    ext = ".xls"
                }

                "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> {
                    ext = ".docx"
                }
            }
            return ext
        }


        private fun getFilePath(fileUri: Uri/*, name: String*/): String? {
            var filePath: String? = null
            val mediaFileName = "UD_${System.currentTimeMillis()}"
            val suffix =
                getSuffix(fileUri)//if(name == "document" || name == "pdf") ".pdf" else if(name == "docx") ".docx" else ".jpg"
            val file = File("${context?.externalCacheDir}/$mediaFileName$suffix")
            try {
                val fileOutputStream = FileOutputStream(file)
                val inputStream = context?.contentResolver?.openInputStream(fileUri)
                val buffers = ByteArray(1024)
                var read: Int
                while (inputStream!!.read(buffers).also { read = it } != -1) {
                    fileOutputStream.write(buffers, 0, read)
                }
                inputStream.close()
                fileOutputStream.close()
                filePath = file.path
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return filePath
        }

        @Throws(Exception::class)
        fun saveFileIntoExternalStorageByUri(context: Context, uri: Uri?): File? {
            val inputStream = context.contentResolver.openInputStream(uri!!)
            val originalSize = inputStream!!.available()
            var bis: BufferedInputStream? = null
            var bos: BufferedOutputStream? = null
            val fileName: String = getFileName(context, uri)
            val file: File =
                fileName.let {
                    makeEmptyFileIntoExternalStorageWithTitle(
                        it
                    )
                }
            bis = BufferedInputStream(inputStream)
            bos = BufferedOutputStream(
                FileOutputStream(
                    file, false
                )
            )
            val buf = ByteArray(originalSize)
            bis.read(buf)
            do {
                bos.write(buf)
            } while (bis.read(buf) != -1)
            bos.flush()
            bos.close()
            bis.close()
            return file
        }

        @SuppressLint("Range")
        fun getFileName(context: Context, uri: Uri): String {
            var result: String? = null
            if (uri.scheme == "content") {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                } finally {
                    cursor!!.close()
                }
            }
            if (result == null) {
                result = uri.path
                val cut = result!!.lastIndexOf('/')
                if (cut != -1) {
                    result = result.substring(cut + 1)
                }
            }
            return result
        }


        private fun makeEmptyFileIntoExternalStorageWithTitle(title: String): File {
            val root = Environment.getExternalStorageDirectory().absolutePath
            return File(root, title)
        }

    }


}