package com.sunlotocenter.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.PersistableBundle
import android.telephony.PhoneNumberUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.isradeleon.thermalprinter.ConnectBluetoothActivity
import com.application.isradeleon.thermalprinter.models.PrintAlignment
import com.application.isradeleon.thermalprinter.models.PrintFont
import com.application.isradeleon.thermalprinter.models.ThermalPrinter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.sunlotocenter.MyApplication
import com.sunlotocenter.R
import com.sunlotocenter.adapter.GameReceiptAdapter
import com.sunlotocenter.adapter.SlotListAdapter
import com.sunlotocenter.dao.*
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.listener.LoadMoreListener
import com.sunlotocenter.model.GameViewModel
import com.sunlotocenter.utils.*
import com.sunlotocenter.validator.Form
import kotlinx.android.synthetic.main.activity_broadcast.*
import kotlinx.android.synthetic.main.activity_receipt_review.*
import kotlinx.android.synthetic.main.activity_slot_list.*
import kotlinx.android.synthetic.main.activity_slot_list.progressBar
import kotlinx.android.synthetic.main.activity_slot_list.rclSlot
//import kotlinx.android.synthetic.main.activity_slot_list.swpLayout
import kotlinx.android.synthetic.main.activity_slot_list.toolbar
import kotlinx.android.synthetic.main.activity_slot_list.txtInfo

class ReceiptReviewActivity : ProtectedActivity() {
    private lateinit var gameReceiptAdapter: GameReceiptAdapter
    private lateinit var gameViewModel: GameViewModel
    private var isSaveState= false
    private lateinit var slotExtra:Slot


    override fun getLayoutId(): Int {
        return R.layout.activity_receipt_review
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)

        gameViewModel= ViewModelProvider(this, SavedStateViewModelFactory(application, this))
            .get(GameViewModel::class.java)

        if(savedInstanceState!=  null){
            isSaveState= true
            gameViewModel.loadSaveState()
        }

        slotExtra= intent.getSerializableExtra(SLOT_EXTRA) as Slot

        supportActionBar?.title= getString(R.string.receipt, "${slotExtra.type} (${slotExtra.session.id})")

        setUpReceipHeader()

        setUpAdapter()
    }

    private fun setUpReceipHeader() {
        val company= MyApplication.getInstance().company;
        if(company== null) return;
        txtName.text= company.name
        txtAddress.text= company.address

        glide(this, company.profilePath, imgLogo, R.drawable.sun, R.drawable.sun)
        val phoneNumberUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance()
        val phone = phoneNumberUtil.parseAndKeepRawInput(company.phoneNumber!!.number, company.phoneNumber!!.countryCode)
        txtPhone.text= phone.rawInput
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        val menuInflater= menuInflater
        menuInflater.inflate(R.menu.mn_receipt, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when(item.itemId){
            R.id.mnClose-> {
                finish()
                return true
            }
            R.id.mnSend->{
                submit(slotExtra)
                return  true
            }
        }
        return false
    }

    private fun setUpAdapter() {
        gameReceiptAdapter= GameReceiptAdapter(slotExtra.games.toSet())
        gameViewModel.slots.clear()

        rclSlot.apply {
            setHasFixedSize(true)
            layoutManager= LinearLayoutManager(this@ReceiptReviewActivity, LinearLayoutManager.VERTICAL, false)
            adapter= gameReceiptAdapter
        }
        observe()
    }

    fun submit(slot: Slot){
        dialog.show()
        gameViewModel.play(slot)
    }

    private fun observe() {
        gameViewModel.slotResponseData.observe(this,
            {
                dialog.dismiss()
                if(it== null){
                    com.sunlotocenter.utils.showDialog(this@ReceiptReviewActivity,
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
                    com.sunlotocenter.utils.showDialog(this@ReceiptReviewActivity,
                        getString(
                            if(it.success)R.string.success_title else R.string.internet_error_title
                        ),
                        if (it.success) getString(R.string.game_success_message) else it.message!!
                        ,
                        getString(
                            if(it.success)R.string.print_receipt else R.string.ok
                        ),
                        object : ClickListener {
                            override fun onClick(): Boolean {
                                startActivityForResult(
                                    Intent(this@ReceiptReviewActivity, ConnectBluetoothActivity::class.java),
                                    ConnectBluetoothActivity.CONNECT_BLUETOOTH
                                )
                                return false
                            }

                        },
                        false,
                        if(it.success) DialogType.SUCCESS else DialogType.ERROR)
                }
            })

    }

    private fun print() {

        val company= MyApplication.getInstance().company;

        // ThermalPrinter is ready
        Glide.with(this)
            .asBitmap()
            .load(company.profilePath)
            .into(object : CustomTarget<Bitmap?>(70, 70) {

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?) {
                    printReceipt(resource, company)
                }
                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    printReceipt(BitmapFactory.decodeResource(resources, R.drawable.sun), company)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}

            })






    }

    private fun printReceipt(resource: Bitmap, company:Company) {
        val print= ThermalPrinter.instance
            .writeImage(resource)
            .write(company.name!!, PrintAlignment.CENTER, PrintFont.LARGE)
            .write(company.address!!, PrintAlignment.CENTER)
            .write(company.phoneNumber!!.number!!, PrintAlignment.CENTER)

        gameReceiptAdapter.games.forEach {
            if(it.position== 4){
                print.fillLineWith('-');
                print.write(it.number, PrintAlignment.CENTER, PrintFont.LARGE)
            }else{
                print.write(it.number + "       " + it.opt + "       " + it.amount)
            }
        }

        print.print()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && requestCode == ConnectBluetoothActivity.CONNECT_BLUETOOTH){
           print()
        }
    }
}