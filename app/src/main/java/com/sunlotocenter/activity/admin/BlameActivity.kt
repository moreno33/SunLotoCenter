package com.sunlotocenter.activity.admin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import com.sunlotocenter.MyApplication
import com.sunlotocenter.activity.ProtectedActivity
import com.sunlotocenter.activity.R
import com.sunlotocenter.dao.Blame
import com.sunlotocenter.dao.Response
import com.sunlotocenter.dao.User
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.model.UserViewModel
import com.sunlotocenter.utils.ClickListener
import com.sunlotocenter.utils.USER_EXTRA
import com.sunlotocenter.validator.*
import kotlinx.android.synthetic.main.activity_blame.*

class BlameActivity : ProtectedActivity() {

    private lateinit var userViewModel: UserViewModel

    override fun getLayoutId() = R.layout.activity_blame
    private var userExtra: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)

        userExtra= intent.extras?.getSerializable(USER_EXTRA) as User?

        userViewModel= ViewModelProvider(this, SavedStateViewModelFactory(application, this))
            .get(UserViewModel::class.java)

        btnBlame.setOnClickListener {
            submit()
        }
        observe()
    }

    private fun submit() {
        if(edxMessage.text.isNotEmpty()){
            //Now we need to check if the old password is valid
            dialog.show()
            userViewModel.addBlame(Blame(null, userExtra!!, MyApplication.getInstance().connectedUser, edxMessage.text.trim().toString()))
        }else{
            com.sunlotocenter.utils.showDialog(this,
                getString(R.string.internet_error_title),
                getString(R.string.add_reprimand_error_message),
                getString(R.string.ok),
                object :ClickListener{
                    override fun onClick(): Boolean {
                        return false
                    }

                },
                true)
        }
    }

    private fun observe() {
        userViewModel.blameData.observe(this,
            {
                blameListener(it)
            })
    }

    private fun blameListener(it: Response<Blame>?) {
        dialog.dismiss()
        if(it== null){
            com.sunlotocenter.utils.showDialog(this,
                getString(R.string.internet_error_title),
                getString(
                    R.string.internet_error_message
                ),
                getString(R.string.ok),
                object : ClickListener {
                    override fun onClick(): Boolean {
                        return false
                    }

                }, true
            )
        }else{
            com.sunlotocenter.utils.showDialog(this,
                getString(R.string.success_title),
                getString(R.string.blame_susccess_message),
                getString(R.string.ok),
                object : ClickListener {
                    override fun onClick(): Boolean {
                        setResult(Activity.RESULT_OK, Intent())
                        finish()
                        return false
                    }

                }, false
            )
        }
    }


}