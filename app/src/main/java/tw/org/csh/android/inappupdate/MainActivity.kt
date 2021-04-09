package tw.org.csh.android.inappupdate

import android.app.DownloadManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    var downloadManager: DownloadManager? = null
    var request: DownloadManager.Request? = null
    var downloadID: Long? = null
    var CONTENT_URI = Uri.parse("content://downloads/my_downloads")
    var app_URL = "https://mdevws.csh.org.tw/App_Archives/Android/Develop/TestingApp2/app-debug.apk"

    var tvVersion: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvVersion = findViewById(R.id.tvVersion)

        try {
            tvVersion.text = packageManager.pack
            var packageInfo = packageManager.getPackageInfo(packageName(), 0)
        }

    }
}