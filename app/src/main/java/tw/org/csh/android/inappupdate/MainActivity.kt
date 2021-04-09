//https://solinariwu.blogspot.com/2017/03/android-downloadmanagerapkandroid-60-70.html

package tw.org.csh.android.inappupdate

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService

class MainActivity : AppCompatActivity() {

    var downloadManager: DownloadManager? = null
    var request: DownloadManager.Request? = null

    var CONTENT_URI = Uri.parse("content://downloads/my_downloads")
    var app_URL = "https://mdevws.csh.org.tw/App_Archives/Android/Develop/TestingApp2/app-debug.apk"

    var tvVersion: TextView? = null
    var btnUpdate: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvVersion = findViewById(R.id.tvVersion)
        btnUpdate = findViewById(R.id.btnUpload)

        try {
            var packageInfo = packageManager.getPackageInfo(packageName, 0)
            tvVersion!!.text = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        btnUpdate!!.setOnClickListener {
            downloadNewVersion()
        }
    }

    fun downloadNewVersion() {
        newFragment = DialogFragmentHelper()
        newFragment!!.show(supportFragmentManager, "download apk")

        downloadManager = this.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        var Uri = Uri.parse(app_URL)
        request = DownloadManager.Request(Uri)
        request!!.setMimeType("application/vnd.android.package-archive")
        request!!.setTitle("CSH-APP.apk")
        request!!.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkStoragePermission()
        } else {
            downloadManagerEqueue()
        }
    }

    fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                20
            )
        } else {
            downloadManagerEqueue()
        }
    }

    fun downloadManagerEqueue() {
        //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir()

        request!!.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "CSH_APP.apk")
        var downloadObserver = DownloadObserver(null)
        contentResolver.registerContentObserver(CONTENT_URI, true, downloadObserver)

        downloadID = downloadManager!!.enqueue(request)
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
        var downloadID: Long? = null
        @SuppressLint("StaticFieldLeak")
        var newFragment: DialogFragmentHelper? = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permission: Array<String>,
        grantReaults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if (grantReaults.isNotEmpty() && grantReaults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadManagerEqueue()
                } else {
                    checkStoragePermission()
                }
                return
            } else -> {

            }
        }
    }

    class DownloadObserver(handler: Handler?) :
        ContentObserver(handler) {
        lateinit var mContext: Context

        override fun onChange(selfChange: Boolean) {
            val query = DownloadManager.Query()
            downloadID?.let { query.setFilterById(it) }
            val dm = mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val cursor: Cursor? = dm.query(query)
            if (cursor != null && cursor.moveToFirst()) {
                val totalColumn: Int =
                    cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                val currentColumn: Int =
                    cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                val totalSize: Int = cursor.getInt(totalColumn)
                val currentSize: Int = cursor.getInt(currentColumn)
                val percent = currentSize.toFloat() / totalSize.toFloat()
                val progress = Math.round(percent * 100)
                (mContext as Activity).runOnUiThread(
                    Runnable() { //確保在UI Thread執行
                    fun run() {
                        newFragment!!.setProgress(progress) }})
            }
        }
    }
}

