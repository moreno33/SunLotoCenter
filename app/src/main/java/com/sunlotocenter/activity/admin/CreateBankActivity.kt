package com.sunlotocenter.activity.admin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import com.sunlotocenter.MyApplication
import com.sunlotocenter.activity.ProtectedActivity
import com.sunlotocenter.R
import com.sunlotocenter.dao.*
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.model.BankViewModel
import com.sunlotocenter.model.UserViewModel
import com.sunlotocenter.utils.*
import com.sunlotocenter.validator.Form
import com.sunlotocenter.validator.MaxLengthValidator
import com.sunlotocenter.validator.MinLengthValidator
import com.yongchun.library.view.ImageSelectorActivity
import kotlinx.android.synthetic.main.activity_create_bank.*
import kotlinx.android.synthetic.main.activity_create_bank.toolbar
import kotlinx.android.synthetic.main.fragment_marriage.*
import kotlinx.android.synthetic.main.fragment_marriage.view.*
import org.modelmapper.ModelMapper
import java.util.*
import kotlin.collections.ArrayList


class CreateBankActivity: ProtectedActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_create_bank
    }

    private lateinit var bankViewModel: BankViewModel
    private lateinit var userViewModel: UserViewModel
    private var bank: Bank= Bank(company = MyApplication.getInstance().company)
    var bankExtra: Bank?= null
    var selectedWorker: User?= null
    private var selectedProfilePath:String?= null

    private var form= Form()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)

        supportActionBar?.title= getString(R.string.create_bank)

        bankViewModel= ViewModelProvider(this, SavedStateViewModelFactory(application, this))
            .get(BankViewModel::class.java)

        userViewModel= ViewModelProvider(this, SavedStateViewModelFactory(application, this))
            .get(UserViewModel::class.java)
        userViewModel.loadSellers(MyApplication.getInstance().company.sequence!!.id!!)

        prepareControl()
        observe()
    }

    override fun onResume() {
        super.onResume()
        fillControl()
    }

    private fun observe() {
        bankViewModel.bankResponseData.observe(this,
            {
                dialog.dismiss()
                if(it== null){
                    showDialog(this@CreateBankActivity,
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
                        showDialog(this@CreateBankActivity,
                            getString(R.string.success_title),
                            getString(
                                if(bankExtra== null) {
                                    R.string.create_bank_success_message
                                } else { R.string.update_bank_success_message }
                            ) ,
                            getString(R.string.ok),
                            object :ClickListener{
                                override fun onClick(): Boolean {
                                    setResult(Activity.RESULT_OK, Intent())
                                    finish()
                                    return false
                                }

                            }, false, DialogType.SUCCESS)
                    }else{
                        showDialog(this@CreateBankActivity,
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

        userViewModel.lastAddedEmployeesData.observe(this,
            { employees ->
                val sellerArray= arrayOfNulls<Seller>(employees.size)
                val sellers= employees as ArrayList<Seller>

                sellers.add(0, Seller(firstName = getString(R.string.choose_a_seller), company = MyApplication.getInstance().company))

                val sellerAdapter= ArrayAdapter<Seller>(this, android.R.layout.simple_spinner_dropdown_item,
                    sellers.toArray(sellerArray))
                sellerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnWorker.adapter= sellerAdapter
                spnWorker.setTitle(getString(R.string.seller))
                spnWorker.setPositiveButton(getString(R.string.ok))
                if(bankExtra!= null && bankExtra!!.worker!= null){
                    sellers.forEachIndexed { index, seller ->
                        if(bankExtra!!.worker!!.sequence!!.id== seller.sequence!!.id){
                            spnWorker.setSelection(index)
                        }
                    }
                }

                spnWorker.onItemSelectedListener= object : OnItemSelectedListener{
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        if(position== 0){
                            selectedWorker= null
                        }else{
                            selectedWorker= sellerAdapter.getItem(position)
                        }
                    }

                }

            })
    }

    private fun fillControl() {
        bankExtra= intent.extras?.getSerializable(BANK_EXTRA) as Bank?
        if (bankExtra!= null){
            //Move the spinner to the right value
            //Here we are filling the category of the cars
                if(isNotEmpty(bankExtra!!.profilePath)){
                    glide(this, bankExtra!!.profilePath, imgProfile, R.drawable.bank_image, R.drawable.background_gray)
                }
            bankExtra?.name?.let{edxName.text= it}
            bankExtra?.code?.let{edxCode.text= it}
            bankExtra?.address?.let{edxAddress.text= it}
            bankExtra?.city?.let{edxCity.text= it}

        }
    }

    private fun prepareControl() {

        //Add validator
        edxName.addValidator(
            MinLengthValidator(1, getString(R.string.empty_validator_error)),
            MaxLengthValidator(50,getString(R.string.max_lenght_validator_error, 50)))
        edxCode.addValidator(
            MinLengthValidator(1, getString(R.string.empty_validator_error)),
            MaxLengthValidator(50,getString(R.string.max_lenght_validator_error, 50)))
        edxAddress.addValidator(
            MinLengthValidator(1, getString(R.string.empty_validator_error)),
            MaxLengthValidator(50,getString(R.string.max_lenght_validator_error, 50)))
        edxCity.addValidator(
            MinLengthValidator(1, getString(R.string.empty_validator_error)),
            MaxLengthValidator(50,getString(R.string.max_lenght_validator_error, 50)))

        form.addInput(edxName, edxCode, edxAddress, edxCity)

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

        edxCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s== null) return
                if(s.length<= 3 && before== 1){
                    edxCode.text= "SLC"
                    edxCode.setSelection(edxCode.text.length)
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        btnSubmit.setOnClickListener {
            submit()
        }
    }

    private fun submit() {
        if(form.isValid()){
//            if(selectedWorker== null){
//                showDialog(this, getString(R.string.internet_error_title),
//                    getString(R.string.choose_a_seller),
//                    getString(R.string.ok),
//                object :ClickListener{
//                    override fun onClick(): Boolean {
//                        return false
//                    }
//
//                }, false, DialogType.ERROR)
//            }else{
//
//            }

            if(bankExtra != null)
                ModelMapper().map(bankExtra, bank)
            bank.apply {
                name = edxName.text.trim()
                code = edxCode.text.trim()
                address = edxAddress.text.trim()
                city = edxCity.text.trim()
                author= MyApplication.getInstance().connectedUser
                worker= selectedWorker
            }
            if(selectedProfilePath!= null)
                bank.profilePath= selectedProfilePath

            dialog.show()
            bankViewModel.save(bank)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if(requestCode == ImageSelectorActivity.REQUEST_IMAGE){
                val images = data?.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT) as ArrayList<String>
                selectedProfilePath= images[0]
                glide(this, selectedProfilePath, imgProfile, R.drawable.bank_image, R.drawable.background_gray)

            }

        }
    }
}