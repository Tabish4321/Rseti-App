package com.rsetiapp.core.util

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.widget.ProgressBar

class DownloadHelper(private val context: Context) {

    private lateinit var downloadManager: DownloadManager
    private var downloadId: Long = 0
    private var progressBar: ProgressBar? = null

    // Define a listener interface for notifying the activity about the download progress or completion
    interface DownloadListener {
        fun onProgress(progress: Int)
        fun onDownloadComplete()
        fun onDownloadFailed()
    }


    @SuppressLint("InlinedApi")
    fun startDownload(url: String, progressBar: ProgressBar, listener: DownloadListener) {
        this.progressBar = progressBar
        downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)

        // Setting title and description for the notification
        request.setTitle("Downloading App")
        request.setDescription("Downloading the app in the background")

        // Setting the destination location of the downloaded file
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "yourfile.apk")

        // Enqueue the download request
        downloadId = downloadManager.enqueue(request)

        // Register for a broadcast receiver to listen for download completion
        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

                // Check if the download ID matches
                if (id == downloadId) {
                    val query = DownloadManager.Query()
                    query.setFilterById(id)
                    val cursor: Cursor = downloadManager.query(query)

                    if (cursor.moveToFirst()) {
                        val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)?:0)

                        when (status) {
                            DownloadManager.STATUS_RUNNING -> {
                                val bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)?:0)
                                val totalBytes = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)?:0)
                                val progress = (bytesDownloaded * 100L) / totalBytes
                                progressBar.progress = progress.toInt()
                                listener.onProgress(progress.toInt())
                            }
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                listener.onDownloadComplete()
                            }
                            DownloadManager.STATUS_FAILED -> {
                                listener.onDownloadFailed()
                            }
                        }
                    }
                    cursor.close()
                }
            }
        }, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED)
    }


    fun stopDownload() {
        downloadManager.remove(downloadId)
    }
}