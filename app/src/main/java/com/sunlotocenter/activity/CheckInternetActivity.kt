package com.sunlotocenter.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kaopiz.kprogresshud.KProgressHUD
import com.sunlotocenter.R
import com.sunlotocenter.extensions.isConnected
import com.sunlotocenter.extensions.toast
import com.sunlotocenter.utils.getDialog
import com.valdesekamdem.library.mdtoast.MDToast
import kotlin.collections.ArrayList


abstract class CheckInternetActivity: AppCompatActivity(){


    companion object{
        const val REFRESHER_CODE: Int= 400

    }

    /**
     * This receiver is going to help us to verify
     * at real time when the internet state changes.
     */
    private lateinit var internetReceiver: BroadcastReceiver

    lateinit var dialog: KProgressHUD

    /**
     * As we need to show connection notice only
     * after the user lost connection, we need to keep
     * track of this lost of connection
     */
    private var isBackToConnection: Boolean = false

    var onNetworkStateChanges= ArrayList<OnNetworkStateChange>()

//    var onRefreshListeners= ArrayList<OnRefreshListener>()
//
//    fun addOnRefreshListener(onRefreshListener: OnRefreshListener){
//        var exist= false
//        for (o in onRefreshListeners){
//            if(onRefreshListener::class.java.name.equals(o::class.java.name)){
//                exist= true
//                break
//            }
//        }
//        if(!exist){
//            onRefreshListeners.add(onRefreshListener)
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        dialog= getDialog(this)
        initialization()
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(internetReceiver, filter)

    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(internetReceiver)
    }

    private fun initialization() {
        internetReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (isBackToConnection) {
                    if (isConnected()) {
                        //We need to set the is back to connection to false
                        isBackToConnection = false
                        toast(getString(R.string.connected), MDToast.TYPE_SUCCESS)
                    } else {
                        isBackToConnection = true
                        toast(getString(R.string.no_connexion), MDToast.TYPE_ERROR)
                    }
                    onNetworkStateChanges.forEach { it?.networkStateChange(isConnected()) }
                } else {
                    //No matter it's not back to connection we need to show notice for connection lost
                    if (!isConnected()) {
                        isBackToConnection = true
                        toast(getString(R.string.no_connexion), MDToast.TYPE_ERROR)
                    }
                }
            }
        }
    }

    abstract fun getLayoutId():Int

    interface OnNetworkStateChange{
        fun networkStateChange(isConnected:Boolean)
    }

//    fun sendToConfirmationScreen(title: String, subTitle: String, type: ConfirmationActivity.Type) {
//        val intent= Intent(this, ConfirmationActivity::class.java)
//        val message= ConfirmationActivity.Message(title, subTitle, type)
//
//        intent.putExtra(ConfirmationActivity.MESSAGE_EXTRA, message)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//
//        startActivity(intent)
//        finish()
//    }
//
//
//    fun returnOnMainScreen(){
//        //The wooliber dashboard knows where to redirect us.
//        val intent= Intent(this, WooliberDashboardActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//
//        startActivity(intent)
//        finish()
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(resultCode== Activity.RESULT_OK){
//            when(requestCode){
//                REFRESHER_CODE ->{
//                    onRefreshListeners.forEach { it.onRefresh() }
//                }
//            }
//        }
//    }
}
