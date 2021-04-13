package tw.org.csh.android.inappupdate

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesHelper {
    val SP_Name: String = "AutomaticallyOpenApk_SP"
    val DownloadID: String = "LoginID"
    lateinit var settings:SharedPreferences
    lateinit var PE:SharedPreferences.Editor

    constructor()

    constructor(context: Context): this() {
        settings = context.getSharedPreferences(SP_Name, 0)
        PE = settings.edit()
    }

    fun setDownloadID(id: Long){
        PE.putLong(DownloadID, id)
        PE.commit()
    }

    fun getDownloadID(): Long {
        return settings.getLong(DownloadID, -1)
    }


}