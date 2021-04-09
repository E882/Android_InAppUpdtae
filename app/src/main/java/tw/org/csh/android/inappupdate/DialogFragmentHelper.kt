package tw.org.csh.android.inappupdate

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class DialogFragmentHelper: DialogFragment(){

    var progressBar: ProgressBar? = null
    var txtProgress: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE);//取消Dialog title列
        dialog!!.setCanceledOnTouchOutside(false);//不能點擊Dialog以外區域

        var v = inflater.inflate(R.layout.download_apk_dialog, container, false)
        progressBar = v.findViewById(R.id.download_progressBar)
        txtProgress = v.findViewById(R.id.txtProgress)
        txtProgress!!.text = "0%"
        return v//super.onCreateView(inflater, container, savedInstanceState)
    }

    fun setProgress(progress: Int) {
        progressBar!!.progress = progress
        txtProgress!!.text = Integer.toString(progress) + "%"
    }
}