package tw.org.csh.android.inappupdate

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import androidx.core.content.FileProvider
import java.io.File


class DownloadCompleteReceiver: BroadcastReceiver {

    companion object {
        var sp: SharedPreferencesHelper? = null

    }

    var con: Context? = null
    constructor()
    constructor(context: Context?) {
        con = context
        sp = context?.let { SharedPreferencesHelper(it) }
    }

    @SuppressLint("NewApi")
    override fun onReceive(context: Context?, intent: Intent?) {
        var downloadID = intent!!.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        var cacheDownloadID = sp!!.getDownloadID()
        if (cacheDownloadID == downloadID) {
            var install = Intent(Intent.ACTION_VIEW)
            var apkFile = queryDownloadedApk(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //Android 7.0 需要透過FileProvider來取得APK檔的Uri
                install.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                val contentUri: Uri = FileProvider.getUriForFile(
                    context!!,
                    BuildConfig.APPLICATION_ID + ".fileProvider",
                    apkFile
                )
                install.setDataAndType(contentUri, "application/vnd.android.package-archive")
            } else {
                install.setDataAndType(
                    Uri.fromFile(apkFile),
                    "application/vnd.android.package-archive"
                )
                install.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context!!.startActivity(install)
        }
    }

    fun queryDownloadedApk(context: Context?): File {
        lateinit var targetApkFile: File
        var downloader: DownloadManager = context!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        var downloadID: Long = sp!!.getDownloadID()
        if(downloadID.toInt() != -1) {
            var query = DownloadManager.Query()
            query.setFilterById(downloadID)
            query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL)
            var cur: Cursor = downloader.query(query)
            if (cur != null) {
                if (cur.moveToFirst()) {
                    var uriString: String = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                    if(!TextUtils.isEmpty(uriString)) {
                        targetApkFile = File(Uri.parse(uriString).path)
                    }
                }
                cur.close()
            }
        }

        return targetApkFile
    }
}