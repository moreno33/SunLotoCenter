package com.sunlotocenter.activity.admin

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import com.sunlotocenter.MyApplication
import com.sunlotocenter.activity.ProtectedActivity
import com.sunlotocenter.R
import com.sunlotocenter.activity.SettingActivity
import com.sunlotocenter.dao.Notification
import com.sunlotocenter.dao.NotificationState
import com.sunlotocenter.dao.User
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.model.NotificationViewModel
import com.sunlotocenter.utils.*
import kotlinx.android.synthetic.main.activity_broadcast.*

class AdminBroadcastActivity :
    ProtectedActivity() {

    private lateinit var notificationViewModel: NotificationViewModel
    private var isSaveState= false
    private var userExtra: User? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_broadcast
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)
        notificationViewModel= ViewModelProvider(this, SavedStateViewModelFactory(application, this))
            .get(NotificationViewModel::class.java)

        userExtra= intent.extras?.getSerializable(USER_EXTRA) as User?
        observe()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        val menuInflater= menuInflater
        menuInflater.inflate(R.menu.mn_broadcast, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when(item.itemId){
            R.id.mnClose-> {
                if(edxMessage.text.toString().isNotEmpty())
                    showDialog(this,
                        getString(R.string.attention),
                        getString(R.string.message_not_empty),
                        getString(R.string.yes),
                        getString(R.string.no),
                        object :ClickListener{
                            override fun onClick(): Boolean {
                                finish()
                                return true
                            }

                        },
                        object :ClickListener{
                            override fun onClick(): Boolean {
                                return false
                            }

                        }, false)
                else finish()
                return true
            }
            R.id.mnSend->{
                if(edxMessage.text.trim().isNotEmpty()){
                    submit(edxMessage.text.toString())
                }
                return  true
            }
        }
        return false
    }

    private fun submit(text: String) {
        dialog.show()
        edxMessage.text.clear()
        notificationViewModel.sendBroadCast(Notification(com.sunlotocenter.dao.Sequence(),
            text,
            MyApplication.getInstance().connectedUser,
            NotificationState.NEW,
            company = MyApplication.getInstance().company))
    }


    private fun observe() {
        notificationViewModel.notificationData.observe(this,
            { notification ->
                dialog.dismiss()
                if(notification== null){
                    showDialog(this,
                        getString(R.string.internet_error_title),
                        getString(
                            R.string.internet_error_message
                        ),
                        getString(R.string.ok),
                        object : ClickListener {
                            override fun onClick(): Boolean {
                                return false
                            }

                        }, true, DialogType.ERROR)
                }else{
                    showDialog(this,
                        getString(R.string.success_title),
                        getString(R.string.broadcast_susccess_message),
                        getString(R.string.ok),
                        object : ClickListener {
                            override fun onClick(): Boolean {
                                finish()
                                return false
                            }

                        }, false, DialogType.SUCCESS)
                }
            })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() === 0) {
            if(edxMessage.text.toString().isNotEmpty()) {
                showDialog(this,
                    getString(R.string.attention),
                    getString(R.string.message_not_empty),
                    getString(R.string.yes),
                    getString(R.string.no),
                    object :ClickListener{
                        override fun onClick(): Boolean {
                            finish()
                            return true
                        }

                    },
                    object :ClickListener{
                        override fun onClick(): Boolean {
                            return false
                        }

                    }, false)
                return true
            }
            return super.onKeyDown(keyCode, event)
        } else super.onKeyDown(keyCode, event)
    }

}