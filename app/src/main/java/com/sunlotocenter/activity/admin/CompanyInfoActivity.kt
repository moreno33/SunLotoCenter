package com.sunlotocenter.activity.admin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import com.emarkall.worldwidephonenumberedittext.Country
import com.emarkall.worldwidephonenumberedittext.CountryListActivity
import com.emarkall.worldwidephonenumberedittext.WorldWidePhoneNumberEditText
import com.sunlotocenter.MyApplication
import com.sunlotocenter.R
import com.sunlotocenter.activity.ProtectedActivity
import com.sunlotocenter.dao.*
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.model.UserViewModel
import com.sunlotocenter.utils.*
import com.sunlotocenter.validator.Form
import com.sunlotocenter.validator.MaxLengthValidator
import com.sunlotocenter.validator.MinLengthValidator
import com.yongchun.library.view.ImageSelectorActivity
import kotlinx.android.synthetic.main.activity_company_info.*
import kotlinx.android.synthetic.main.activity_company_info.btnSubmit
import kotlinx.android.synthetic.main.activity_company_info.edxAddress
import kotlinx.android.synthetic.main.activity_company_info.edxCity
import kotlinx.android.synthetic.main.activity_company_info.toolbar
import org.modelmapper.ModelMapper
import java.util.*


class CompanyInfoActivity : ProtectedActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_company_info
    }
    private lateinit var userViewModel: UserViewModel
    private var company= Company(sequence = Sequence())
    var companyExtra: Company?= null


    private var form= Form()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)

        userViewModel= ViewModelProvider(this, SavedStateViewModelFactory(application, this))
            .get(UserViewModel::class.java)

        edxPhone.switchCountry(509)

        fillControl()
        prepareControl()
        observe()
    }

    private fun observe() {
        userViewModel.companyResponseData.observe(this,
            {
                dialog.dismiss()
                if(it== null){
                    showDialog(this@CompanyInfoActivity,
                        getString(R.string.internet_error_title),
                        getString(
                            R.string.internet_error_message
                        ) ,
                        getString(R.string.ok),
                        object :ClickListener{
                            override fun onClick(): Boolean {
                                return false
                            }

                        }, true, DialogType.ERROR)
                }else{
                    //Update the local data is connected user is changed
                    if(it.success){
                        if(companyExtra!= null)
                        showDialog(this@CompanyInfoActivity,
                            getString(R.string.success_title),
                            getString(R.string.update_company_success_message) ,
                            getString(R.string.ok),
                            object :ClickListener{
                                override fun onClick(): Boolean {
                                    finish()
                                    return false
                                }
                            }, false, DialogType.SUCCESS)
                    }else{
                        showDialog(this@CompanyInfoActivity,
                            getString(R.string.internet_error_title),
                            it.message!! ,
                            getString(R.string.ok),
                            object :ClickListener{
                                override fun onClick(): Boolean {
                                    return false
                                }

                            }, false, DialogType.ERROR)
                    }

                }
            })
    }

    private fun fillControl() {
        companyExtra= intent.extras?.getSerializable(COMPANY_EXTRA) as Company?
        if (companyExtra!= null){
            company.profilePath= companyExtra!!.profilePath
            glide(this, company.profilePath, imgLogo, R.drawable.background_gray, R.drawable.sun)

            companyExtra?.name?.let{edxCompanyName.text= it }
            companyExtra?.phoneNumber?.let{
                edxPhone.switchCountry(it.countryCode)
                edxPhone.setPhoneNumber(it.number)}
            companyExtra?.address?.let{edxAddress.text= it }
            companyExtra?.city?.let{edxCity.text= it}

        }else{
            toolbar.title= getString(R.string.create_company)
        }
    }

    private fun prepareControl() {

        //Add validator
        edxCompanyName.addValidator(
            MinLengthValidator(1, getString(R.string.empty_validator_error)),
            MaxLengthValidator(50,getString(R.string.max_lenght_validator_error, 50)))
        edxAddress.addValidator(
            MinLengthValidator(1, getString(R.string.empty_validator_error)),
            MaxLengthValidator(50,getString(R.string.max_lenght_validator_error, 50)))
        edxCity.addValidator(
            MinLengthValidator(1, getString(R.string.empty_validator_error)),
            MaxLengthValidator(50,getString(R.string.max_lenght_validator_error, 50)))

        form.addInput(edxCompanyName, edxPhone, edxAddress, edxCity)


        //Request to get profile picture
        clEdit.setOnClickListener {
            ImageSelectorActivity.start(
                this@CompanyInfoActivity,
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
        if(company.profilePath.isNullOrEmpty()){
            showDialog(this@CompanyInfoActivity,
                getString(R.string.internet_error_title),
                getString(R.string.profile_picture_required) ,
                getString(R.string.ok),
                object :ClickListener{
                    override fun onClick(): Boolean {
                        return false
                    }

                }, true, DialogType.ERROR)
            return
        }
        if(form.isValid()){
            if(companyExtra != null){
                companyExtra!!.profilePath= company.profilePath
                ModelMapper().map(companyExtra, company)
            }
            company.apply {
                name = edxCompanyName.text.trim()
                phoneNumber = PhoneNumber(edxPhone.region, edxPhone.countryCode, edxPhone.text)
                address = edxAddress.text.trim()
                city = edxCity.text.trim()
                author= MyApplication.getInstance().connectedUser
            }
            dialog.show()
            userViewModel.saveCompany(company)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if(requestCode == ImageSelectorActivity.REQUEST_IMAGE){
                val images = data?.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT) as ArrayList<String>
                company.profilePath= images[0]
                glide(this, company.profilePath, imgLogo, R.drawable.background_gray, R.drawable.sun)

            }
            if (requestCode === WorldWidePhoneNumberEditText.REQUEST_CODE) {
                val country = data?.getSerializableExtra(CountryListActivity.COUNTRY_EXTRA) as Country
                edxPhone.switchCountry(country.getmIsoCode())
            }

        }
    }
}