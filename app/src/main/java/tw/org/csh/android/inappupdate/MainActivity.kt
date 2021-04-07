package tw.org.csh.android.inappupdate

import android.Manifest
import android.app.DownloadManager
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File


class MainActivity : AppCompatActivity() {

    //var newFragment: DialogFragmentHelper? = null
    var DM: DownloadManager? = null
    var request: DownloadManager.Request? = null
    var latestDownloadID: Long? = null


    var URL: String? = null
    //var downloadObserver: DownloadObserver? = null
    var CONTENT_URI = Uri.parse("content://downloads/my_downloads")

    var tvVersion: TextView? = null
    var btnUpload: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvVersion = findViewById(R.id.tvVersion)
        btnUpload = findViewById(R.id.btnUpload)

        try { //取得APP目前的versionName
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            tvVersion!!.text = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        btnUpload!!.setOnClickListener{
            downloadNewVersion()
        }
    }

    private fun downloadNewVersion() {
        //newFragment = DialogFragmentHelper()
        //newFragment.show(supportFragmentManager, "download apk")
        DM = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        URL = "https://mdevws.csh.org.tw/App_Archives/Android/Develop/TestingApp2/app-debug.apk"
        val uri = Uri.parse(URL)
        request = DownloadManager.Request(uri)
        request!!.setMimeType("application/vnd.android.package-archive") // ("application/vnd.android.package-archive")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //Android 6.0以上需要判斷使用者是否願意開啟儲存(WRITE_EXTERNAL_STORAGE)的權限
            checkStoragePermission()
        } else {
            downloadManagerEnqueue()
        }
    }

    @Suppress("DEPRECATED_IDENTITY_EQUALS")
    private fun checkStoragePermission() { //Android 6.0檢查是否開啟儲存(WRITE_EXTERNAL_STORAGE)的權限，若否，出現詢問視窗
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                !== PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                !== PackageManager.PERMISSION_GRANTED) { //Can add more as per requirement
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    20)
        } else {
            downloadManagerEnqueue()
        }
    }

    private fun downloadManagerEnqueue() {
        //創建目錄
        //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir()
        //val directory = File(Environment.DIRECTORY_DOWNLOADS)
        if(File(Environment.DIRECTORY_DOWNLOADS).exists()) {
            File(Environment.DIRECTORY_DOWNLOADS).mkdirs()
        }


        //設定APK儲存位置
        request!!.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "APP.jpg")
        latestDownloadID = DM!!.enqueue(request)


//        val receiver = DownloadCompleteReceiver(applicationContext)
//        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) //註冊DOWNLOAD_COMPLETE-BroadcastReceiver
//        downloadObserver = null
//        contentResolver.registerContentObserver(CONTENT_URI, true, downloadObserver) //註冊ContentObserver
//        LatestDownloadID = DM!!.enqueue(request)
//        val sp = SharedPreferencesHelper(applicationContext)
//        sp.setDownloadID(LatestDownloadID) //儲存DownloadID



        //創建目錄
//        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir() ;
//        //設定APK儲存位置
//        request.setDestinationInExternalPublicDir(  Environment.DIRECTORY_DOWNLOADS  , "DG_App.apk" ) ;
//        LatestDownloadID= DM.enqueue(request);

    }
    companion object {
        private const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay!
                    // Download the Image
                    downloadManagerEnqueue()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    checkStoragePermission()
                }
                return
            }
            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }


//    class DownloadObserver(handler: Handler?) : ContentObserver(handler) {
//        override fun onChange(selfChange: Boolean) {
//            val query = DownloadManager.Query()
//            query.setFilterById(LatestDownloadID)
//            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
//            val cursor = dm.query(query)
//            if (cursor != null && cursor.moveToFirst()) {
//                val totalColumn = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
//                val currentColumn = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
//                val totalSize = cursor.getInt(totalColumn)
//                val currentSize = cursor.getInt(currentColumn)
//                val percent = currentSize.toFloat() / totalSize.toFloat()
//                val progress = Math.round(percent * 100)
//                runOnUiThread(Runnable
//                //確保在UI Thread執行
//                { newFragment.setProgress(progress) })
//            }
//        }
//    }
}