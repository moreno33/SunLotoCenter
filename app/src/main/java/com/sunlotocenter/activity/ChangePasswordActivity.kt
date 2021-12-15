package com.sunlotocenter.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import com.sunlotocenter.MyApplication
import com.sunlotocenter.R
import com.sunlotocenter.activity.admin.AdminPersonalInfoActivity
import com.sunlotocenter.dao.Response
import com.sunlotocenter.dao.User
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.model.UserViewModel
import com.sunlotocenter.utils.*
import com.sunlotocenter.validator.*
import kotlinx.android.synthetic.main.activity_change_password.*

class ChangePasswordActivity : ProtectedActivity() {

    private var userExtra: User? = null
    private lateinit var userViewModel: UserViewModel
    lateinit var form:Form

    override fun getLayoutId() = R.layout.activity_change_password

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)
        userExtra= intent.extras?.getSerializable(USER_EXTRA) as User?

        userViewModel= ViewModelProvider(this, SavedStateViewModelFactory(application, this))
            .get(UserViewModel::class.java)

        controlViewToShow()
        btnSubmit.setOnClickListener {
            submit()
        }
        observe()
    }

    private fun controlViewToShow() {
        form=  Form()
        edxNewPassword.addValidator(
            MinLengthValidator(8, getString(R.string.min_length_validator_error, 8)),
            MaxLengthValidator(20,getString(R.string.max_lenght_validator_error, 20))
        )
        edxRepeatPasssword.addValidator(
            MinLengthValidator(8, getString(R.string.min_length_validator_error, 8)),
            MaxLengthValidator(20,getString(R.string.max_lenght_validator_error, 20)),
            EqualToValidator(edxNewPassword, getString(R.string.equal_validator_error))
        )
        form.addInput(edxNewPassword, edxRepeatPasssword)

        if(userExtra!!.sequence!!.id== MyApplication.getInstance().connectedUser.sequence!!.id){
            edxOldPassword.addValidator(
                MinLengthValidator(8, getString(R.string.min_length_validator_error, 8)),
                MaxLengthValidator(20,getString(R.string.max_lenght_validator_error, 20))
            )
            form.addInput(edxOldPassword)
        }else{
            edxOldPassword.visibility= View.GONE
        }
    }

    private fun submit() {
        if(form.isValid()){
            //Now we need to check if the old password is valid
            dialog.show()
            if(userExtra!!.sequence!!.id== MyApplication.getInstance().connectedUser.sequence!!.id)
                userViewModel.login(userExtra!!.phoneNumber!!.number, edxOldPassword.text)
            else{
                val user= userExtra
                user!!.actor= MyApplication.getInstance().connectedUser
                user.password= edxNewPassword.text.trim()
                userViewModel.save(user)
            }
        }
    }

    private fun observe() {
        userViewModel.userResponseData.observe(this,
            {
                when(userViewModel.ACTION){
                    ACTION_SAVE->
                        saveListener(it)
                    ACTION_LOGIN->
                        loginListener(it)
                }
            })
    }

    private fun loginListener(it: Response<User>?) {
        if(it== null){
            dialog.dismiss()
            com.sunlotocenter.utils.showDialog(this,
                getString(R.string.internet_error_title),
                getString( R.string.internet_error_message),
                getString(R.string.ok),
                object : ClickListener {
                    override fun onClick(): Boolean {
                        return false
                    }

                }, true, DialogType.ERROR)
        }else{
            if(userExtra!!.sequence!!.id== MyApplication.getInstance().connectedUser.sequence!!.id){
                if(it.success){
                    val user= userExtra
                    user!!.password= edxNewPassword.text.trim()
                    userViewModel.save(user)
                }else{
                    dialog.dismiss()
                    com.sunlotocenter.utils.showDialog(this,
                        getString(R.string.internet_error_title),
                        getString(R.string.wrong_old_password_error_message),
                        getString(R.string.ok),
                        object : ClickListener {
                            override fun onClick(): Boolean {
                                return false
                            }

                        }, false, DialogType.ERROR)
                }
            }
        }
    }

    private fun saveListener(it: Response<User>?) {
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

                }, true, DialogType.ERROR)
        }else{
            //Update the local data is connected user is changed
            if(userExtra!!.sequence!!.id== MyApplication.getInstance().connectedUser.sequence!!.id)
                MyApplication.getInstance().connectedUser= it.data

            com.sunlotocenter.utils.showDialog(this,
                getString(R.string.success_title),
                getString(
                    if (userExtra!!.sequence!!.id== MyApplication.getInstance().connectedUser.sequence!!.id)
                        R.string.password_change_success
                    else
                        R.string.update_user_password_success_message
                ),
                getString(R.string.ok),
                object : ClickListener {
                    override fun onClick(): Boolean {
                        setResult(RESULT_OK)
                        finish()
                        return false
                    }

                }, false, DialogType.SUCCESS)
        }
    }


}