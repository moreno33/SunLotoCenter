package com.sunlotocenter.activity

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import androidx.core.net.toFile
import com.github.javiersantos.appupdater.AppUpdaterUtils
import com.github.javiersantos.appupdater.AppUpdaterUtils.UpdateListener
import com.github.javiersantos.appupdater.enums.AppUpdaterError
import com.github.javiersantos.appupdater.enums.UpdateFrom
import com.github.javiersantos.appupdater.objects.Update
import com.sunlotocenter.MyApplication
import com.sunlotocenter.R
import com.sunlotocenter.utils.ClickListener
import com.sunlotocenter.utils.DialogType
import java.lang.Exception

open abstract class BasicActivity: CheckInternetActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //For having white icon on the status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decor = window.decorView
            decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }


        val appUpdaterUtils = AppUpdaterUtils(this)
            .setUpdateFrom(UpdateFrom.JSON)
            .setGitHubUserAndRepo("moreno33", "SunLotoCenter")
            .withListener(object : UpdateListener {
                override fun onSuccess(update: Update, isUpdateAvailable: Boolean) {

                    var releaseNote= ""
                    update.releaseNotes?.forEach { releaseNote += "-${it}\n" }

                    com.sunlotocenter.utils.showDialog(
                        this@BasicActivity,
                        getString(R.string.new_version_available_title),
                        getString(
                            R.string.new_version_available,
                            "${update.latestVersion} (${update.latestVersionCode})",
                        releaseNote),
                        getString(R.string.download),
                        object : ClickListener {
                            override fun onClick(): Boolean {
                                download(
                                    MyApplication.BASE_URL + "/apk/${update.latestVersionCode}",
                                    "slc_${MyApplication.getInstance().version.versionCode}.apk"
                                )
                                return false
                            }

                        },
                        true, DialogType.NEUTRAL
                    )
                }
                override fun onFailed(error: AppUpdaterError) {
                    Log.d("AppUpdater Error", "Something went wrong")
                }
            })
        appUpdaterUtils.start()
    }

    fun download(url: String, filename: String){
        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        request.setTitle(filename)
        request.setDescription(getString(R.string.downloading))
        request.addRequestHeader("cookie", CookieManager.getInstance().getCookie(url))
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
        val downloadId= manager?.enqueue(request)
        dialog.show()

        val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctxt: Context, intent: Intent) {
                dialog.dismiss()
                val file = manager?.getUriForDownloadedFile(downloadId!!)?.toFile()
                if (file!!.exists()) {
                    try {
                        val install = Intent(Intent.ACTION_PACKAGE_REPLACED)
                        install.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        install.setDataAndType(manager?.getUriForDownloadedFile(downloadId!!),
                            "application/vnd.android.package-archive"
                        )
                        startActivity(install)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
//                val intent = Intent(Intent.ACTION_DELETE)
//                intent.data = Uri.parse("package:com.sunlotocenter")
//                intent.putExtra(Intent.EXTRA_RETURN_RESULT, true)
//                activityResult.launch(intent)

            }
        }
        //register receiver for when .apk download is compete
        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}