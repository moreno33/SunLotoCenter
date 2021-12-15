package com.sunlotocenter.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.emarkall.worldwidephonenumberedittext.Country
import com.emarkall.worldwidephonenumberedittext.CountryListActivity
import com.emarkall.worldwidephonenumberedittext.WorldWidePhoneNumberEditText
import com.sunlotocenter.MyApplication
import com.sunlotocenter.R
import com.sunlotocenter.dao.Response
import com.sunlotocenter.dao.User
import com.sunlotocenter.dao.UserStatus
import com.sunlotocenter.extensions.isConnected
import com.sunlotocenter.extensions.neutralDialog
import com.sunlotocenter.extensions.redirectToDashboard
import com.sunlotocenter.utils.ClickListener
import com.sunlotocenter.utils.DialogType
import com.sunlotocenter.utils.userApi
import com.sunlotocenter.utils.showDialog
import com.sunlotocenter.validator.Form
import kotlinx.android.synthetic.main.activity_admin_personal_info.*
import kotlinx.android.synthetic.main.activity_admin_personal_info.edxPhone
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import java.util.*

class LoginActivity : BasicActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        edxPhone.switchCountry(509)
        btnLogin.setOnClickListener { submit() }
    }

    override fun onResume() {
        super.onResume()
        if(MyApplication.getInstance().isLoggedIn)redirectToDashboard(MyApplication.getInstance().connectedUser)
    }


    /**
     * This method allows us to perform a click on the login button.
     */
    fun submit() {

        //Let's create a form to validate
        val form = Form()

//        edxPassword.addValidator(
//            MinLengthValidator(8, getString(R.string.min_length_validator_error)),
//            MaxLengthValidator(20, getString(R.string.max_lenght_validator_error))
//        )
        form.addInput(edxPhone, edxPassword)

        if (form.isValid()) {

            //Let's check the internet connectiviy
            if (isConnected()) {

                dialog.show()

                login(edxPhone.text, edxPassword.text)

            } else {
                //We will show a dialog to tell the user to check his internet connection
                neutralDialog()
            }


        }


    }

    private fun login(phone: String, password: String) {

        val call: Call<Response<User>> = userApi.login(phone, password)

        call.enqueue(object : Callback<Response<User>> {
            override fun onResponse(call: Call<Response<User>>,
                                    response: retrofit2.Response<Response<User>>) {

                dialog.dismiss()
                var user= response.body()?.data
                if(user!= null) {
                    if (user.status == UserStatus.BLOCKED) {
                        showDialog(this@LoginActivity,
                            getString(R.string.internet_error_title),
                            getString(R.string.blocked_account),
                            getString(R.string.ok),
                            object : ClickListener {
                                override fun onClick(): Boolean {
                                    return false
                                }

                            },
                            true, DialogType.ERROR)
                    } else{
                        MyApplication.getInstance().login(user)
                        redirectToDashboard(user)
                    }

                }else{
                    showDialog(this@LoginActivity,
                      getString(R.string.internet_error_title),
                      getString(R.string.incorrect_credetential_message),
                      getString(R.string.ok),
                      object : ClickListener {
                          override fun onClick(): Boolean {
                              return false
                          }

                      },
                      true, DialogType.ERROR)
                }
            }

            override fun onFailure(call: Call<Response<User>>, t: Throwable) {
                Log.i("Response", t.toString())
                dialog.dismiss()
            }
        })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == WorldWidePhoneNumberEditText.REQUEST_CODE) {
                val country = data?.getSerializableExtra(CountryListActivity.COUNTRY_EXTRA) as Country
                edxPhone.switchCountry(country.getmIsoCode())
            }

        }
    }
}