package com.sunlotocenter.activity.admin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import com.emarkall.worldwidephonenumberedittext.Country
import com.emarkall.worldwidephonenumberedittext.CountryListActivity
import com.emarkall.worldwidephonenumberedittext.WorldWidePhoneNumberEditText
import com.sunlotocenter.MyApplication
import com.sunlotocenter.activity.ProtectedActivity
import com.sunlotocenter.activity.R
import com.sunlotocenter.dao.*
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.model.UserViewModel
import com.sunlotocenter.utils.*
import com.sunlotocenter.validator.Form
import com.sunlotocenter.validator.MaxLengthValidator
import com.sunlotocenter.validator.MinLengthValidator
import com.yongchun.library.view.ImageSelectorActivity
import kotlinx.android.synthetic.main.activity_admin_personal_info.*
import org.modelmapper.ModelMapper
import java.util.*


class AdminPersonalInfoActivity : ProtectedActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_admin_personal_info
    }

    private lateinit var userViewModel: UserViewModel
    private var user: User= Seller()
    var userExtra: User?= null
    private var userType: UserType= UserType.SELLER

    private lateinit var categories: Array<SpinnerItem>

    private var form= Form()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)

        userViewModel= ViewModelProvider(this, SavedStateViewModelFactory(application, this))
            .get(UserViewModel::class.java)
        categories= arrayOf(
            SpinnerItem("seller", getString(R.string.seller)),
            SpinnerItem("admin", getString(R.string.administrator))
        )

        edxPhone.switchCountry(509)

        fillControl()
        prepareControl()
        observe()
    }

    private fun observe() {
        userViewModel.userResponseData.observe(this,
            {
                dialog.dismiss()
                if(it== null){
                    showDialog(this@AdminPersonalInfoActivity,
                        getString(R.string.internet_error_title),
                        getString(
                            R.string.internet_error_message
                        ) ,
                        getString(R.string.ok),
                        object :ClickListener{
                            override fun onClick(): Boolean {
                                return false
                            }

                        }, true)
                }else{
                    //Update the local data is connected user is changed
                    if(it.success){
                        if(userExtra!= null && userExtra!!.sequence.id== MyApplication.getInstance().connectedUser.sequence.id)
                            MyApplication.getInstance().connectedUser= it.data
                        showDialog(this@AdminPersonalInfoActivity,
                            getString(R.string.success_title),
                            getString(
                                if(userExtra== null) {
                                    R.string.create_account_success_message
                                } else {
                                    if(userExtra!!.sequence.id== MyApplication.getInstance().connectedUser.sequence.id)
                                        R.string.update_own_account_success_message
                                    else R.string.update_user_account_success_message
                                }
                            ) ,
                            getString(R.string.ok),
                            object :ClickListener{
                                override fun onClick(): Boolean {
                                    if(userExtra== null || userExtra!!.sequence.id!= MyApplication.getInstance().connectedUser.sequence.id){
                                        setResult(Activity.RESULT_OK, Intent())
                                    }
                                    finish()
                                    return false
                                }

                            }, false)
                    }else{
                        showDialog(this@AdminPersonalInfoActivity,
                            getString(R.string.internet_error_title),
                            it.message!! ,
                            getString(R.string.ok),
                            object :ClickListener{
                                override fun onClick(): Boolean {
                                    return false
                                }

                            }, false)
                    }

                }
            })
    }

    private fun fillControl() {
        userExtra= intent.extras?.getSerializable(USER_EXTRA) as User?
        if (userExtra!= null){
            user.profilePath= userExtra!!.profilePath
            glide(this, user.profilePath, imgProfile, R.drawable.background_gray, getProfileImage(userExtra))
            //An admin can't downgrade
            if(userExtra is Admin){
                clCategory.visibility= GONE
                userType= UserType.ADMIN
            }

            //Move the spinner to the right value
            UserType.values().forEachIndexed{index, element->
                if(element.id == userType.id){
                    spnCategory.setSelection(index)
                }
            }

            if(userExtra!!.sex== Sex.MALE){
                rdbMale.isChecked= true
                rdbFemale.isChecked= false
            }else{
                rdbMale.isChecked= false
                rdbFemale.isChecked= true
            }

            userExtra?.firstName?.let{edxFirstName.text= it }
            userExtra?.lastName?.let{edxLastName.text= it }
            userExtra?.phoneNumber?.let{
                edxPhone.switchCountry(it!!.countryCode)
                edxPhone.setPhoneNumber(it.number)}
            userExtra?.address?.let{edxAddress.text= it }
            userExtra?.city?.let{edxCity.text= it}

        }else{
            toolbar.title= getString(R.string.create_employee)
        }
    }

    private fun getProfileImage(user: User?): Int {
        if(user== null){
            return R.drawable.admin_male_icon
        }else{
            if (user.sex== Sex.MALE){
                return R.drawable.admin_male_icon
            }else{
                return R.drawable.woman_icon
            }
        }

    }

    private fun prepareControl() {

        //Add validator
        edxFirstName.addValidator(
            MinLengthValidator(1, getString(R.string.empty_validator_error)),
            MaxLengthValidator(50,getString(R.string.max_lenght_validator_error, 50)))
        edxLastName.addValidator(
            MinLengthValidator(1, getString(R.string.empty_validator_error)),
            MaxLengthValidator(50,getString(R.string.max_lenght_validator_error, 50)))
        edxAddress.addValidator(
            MinLengthValidator(1, getString(R.string.empty_validator_error)),
            MaxLengthValidator(50,getString(R.string.max_lenght_validator_error, 50)))
        edxCity.addValidator(
            MinLengthValidator(1, getString(R.string.empty_validator_error)),
            MaxLengthValidator(50,getString(R.string.max_lenght_validator_error, 50)))

        form.addInput(edxFirstName, edxLastName, edxPhone, edxAddress, edxCity)

        //Fill category spinner
        val dataAdapter= ArrayAdapter<SpinnerItem>(this, android.R.layout.simple_spinner_item, categories)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnCategory.adapter = dataAdapter
        spnCategory.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                UserType.values().forEach {
                    if(it.id == dataAdapter.getItem(position)!!.id){
                        userType= it
                    }
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }

        rdgSex.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.rdbMale->{
                    user.sex= Sex.MALE
                    glide(this, user.profilePath, imgProfile, R.drawable.background_gray, R.drawable.admin_male_icon)
                }
                R.id.rdbFemale->{
                    user.sex= Sex.FEMALE
                    glide(this, user.profilePath, imgProfile, R.drawable.background_gray, R.drawable.woman_icon)
                }
            }
        }

        //Request to get profile picture
        clPick.setOnClickListener {
            ImageSelectorActivity.start(
                AdminPersonalInfoActivity@this,
                1,
                ImageSelectorActivity.MODE_SINGLE,
                true,
                true,
                true
            )

        }

        btnSubmit.setOnClickListener {
            submit()
        }
    }

    private fun submit() {
        if(form.isValid()){

            when(userType){
                UserType.SELLER->{
                    user= Seller(sex = user.sex, profilePath = user.profilePath)
                    if(userExtra != null){
                        userExtra!!.sex= user.sex
                        userExtra!!.profilePath= user.profilePath
                        ModelMapper().map(userExtra, user)
                    }
                    user.apply {
                        firstName = edxFirstName.text.trim()
                        lastName = edxLastName.text.trim()
                        phoneNumber = PhoneNumber(edxPhone.region, edxPhone.countryCode, edxPhone.text)
                        address = edxAddress.text.trim()
                        city = edxCity.text.trim()
                    }
                }
                UserType.ADMIN->{
                    user= Admin(sex = user.sex, profilePath = user.profilePath)
                    if(userExtra != null){
                        userExtra!!.sex= user.sex
                        userExtra!!.profilePath= user.profilePath
                        ModelMapper().map(userExtra, user)
                    }
                    user.apply {
                        firstName = edxFirstName.text
                        lastName = edxLastName.text
                        phoneNumber = PhoneNumber(edxPhone.region, edxPhone.countryCode, edxPhone.text)
                        address = edxAddress.text
                        city = edxCity.text
                    }
                }
            }
            dialog.show()
            userViewModel.save(user)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if(requestCode == ImageSelectorActivity.REQUEST_IMAGE){
                val images = data?.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT) as ArrayList<String>
                user.profilePath= images[0]
                glide(this, user.profilePath, imgProfile, R.drawable.background_gray, R.drawable.woman_icon)

            }
            if (requestCode === WorldWidePhoneNumberEditText.REQUEST_CODE) {
                val country = data?.getSerializableExtra(CountryListActivity.COUNTRY_EXTRA) as Country
                edxPhone.switchCountry(country.getmIsoCode())
            }

        }
    }
}