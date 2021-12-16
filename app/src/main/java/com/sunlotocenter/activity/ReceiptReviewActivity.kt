package com.sunlotocenter.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.PersistableBundle
import android.telephony.PhoneNumberUtils
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.text.HtmlCompat
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
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.ImagePrintable
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import com.sunlotocenter.MyApplication
import com.sunlotocenter.R
import com.sunlotocenter.adapter.GameReceiptAdapter
import com.sunlotocenter.adapter.SlotListAdapter
import com.sunlotocenter.dao.*
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.extensions.toast
import com.sunlotocenter.listener.LoadMoreListener
import com.sunlotocenter.model.GameViewModel
import com.sunlotocenter.utils.*
import com.sunlotocenter.validator.Form
import kotlinx.android.synthetic.main.activity_receipt_review.*
import java.util.*
import kotlin.collections.ArrayList

class ReceiptReviewActivity : ProtectedActivity(), PrintingCallback {
    private lateinit var gameReceiptAdapter: GameReceiptAdapter
    private lateinit var gameViewModel: GameViewModel
    private var isSaveState= false
    private lateinit var slotExtra:Slot
    private var printing: Printing?= null


    override fun getLayoutId(): Int {
        return R.layout.activity_receipt_review
    }

    private lateinit var activityResult:
            ActivityResultLauncher<Intent>

    override fun onStart() {
        super.onStart()
        activityResult= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode== Activity.RESULT_OK){
                printing= Printooth.printer()
            }
        }
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
        val formattedNumber = phoneNumberUtil.format(phone, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)

        txtPhone.text= getString(R.string.tel_value, formattedNumber)
        txtUniq.text= getString(R.string.receipt_number, "-")
        txtDate.text= getString(R.string.date_value, "-")
        txtSession.text= getString(R.string.session_value, slotExtra.session.id)
        txtGame.text= getString(R.string.game_value, slotExtra.type.id)
        txtConditions.text=  HtmlCompat.fromHtml(getString(R.string.conditions, company.name), HtmlCompat.FROM_HTML_MODE_COMPACT)
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
                if(Printooth.hasPairedPrinter()) {
                    submit(slotExtra)
                }
                else {
                    showDialog(this@ReceiptReviewActivity,
                        getString(R.string.internet_error_title),
                        getString(R.string.no_printer_no_submit),
                        getString(R.string.connect),
                        object : ClickListener {
                            override fun onClick(): Boolean {
                                activityResult.launch(Intent(this@ReceiptReviewActivity, ScanningActivity::class.java))
                                return false
                            }

                        },
                        false, DialogType.ERROR)

                }

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
                    showDialog(this@ReceiptReviewActivity,
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
                    showDialog(this@ReceiptReviewActivity,
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
                                if(it.success){
                                    txtUniq.text= getString(R.string.receipt_number, it.data?.uniq)
                                    txtDate.text= getString(R.string.date_value, getDateTimeString(this@ReceiptReviewActivity, it.data!!.createdDateTime!!))
                                    if(Printooth.hasPairedPrinter()) {
                                        print(it.data)
                                    }
                                    else {
                                        showDialog(this@ReceiptReviewActivity,
                                            getString(R.string.internet_error_title),
                                            getString(R.string.no_printer_no_submit),
                                            getString(R.string.connect),
                                            object : ClickListener {
                                                override fun onClick(): Boolean {
                                                    activityResult.launch(Intent(this@ReceiptReviewActivity, ScanningActivity::class.java))
                                                    return false
                                                }

                                            },
                                            false, DialogType.ERROR)

                                    }
                                }
                                return false
                            }

                        },
                        false,
                        if(it.success) DialogType.SUCCESS else DialogType.ERROR)
                }
            })

    }

    private fun print(slot: Slot) {

        val company= MyApplication.getInstance().company;

        // ThermalPrinter is ready
        Glide.with(this)
            .asBitmap()
            .load(company.profilePath)
            .into(object : CustomTarget<Bitmap?>(70, 70) {

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?) {
                    printReceipt(resource, company, slot)
                }
                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    printReceipt(BitmapFactory.decodeResource(resources, R.drawable.sun), company, slot)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}

            })
    }

    private fun printReceipt(resource: Bitmap, company:Company, slot: Slot) {
        val printables= ArrayList<Printable>()

        printables.add(ImagePrintable.Builder(resource).build())

        printables.add(TextPrintable.Builder()
            .setText(company.name!!)
            .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
            .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
            .build())
        printables.add(TextPrintable.Builder()
            .setText(company.address!!)
            .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
            .build())
        printables.add(TextPrintable.Builder()
            .setText(company.phoneNumber!!.number)
            .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
            .setNewLinesAfter(4)
            .build())

        printables.add(TextPrintable.Builder()
            .setText(getString(R.string.receipt_number, slot.uniq))
            .build())
        printables.add(TextPrintable.Builder()
            .setText(getString(R.string.date_value, getDateTimeString(this, slot.createdDateTime!!)))
            .build())
        printables.add(TextPrintable.Builder()
            .setText(getString(R.string.session_value, slot.session.id))
            .build())
        printables.add(TextPrintable.Builder()
            .setText(getString(R.string.game_value, slot.type.id))
            .build())

        var counter= 0
        gameReceiptAdapter.games.forEachIndexed {id, it->
            if(it.type==1 && it.amount > 0){
                counter++
            }
            if(it.position== 4){
                printables.add(TextPrintable.Builder()
                    .setText(getString(R.string.total_preview, counter, it.amount))
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .build())
            }else{
                val builder= TextPrintable.Builder()
                    .setText(it.number + "              " + it.opt + "          " + it.amount)
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                if(id== gameReceiptAdapter.games.size-2)
                    builder.setNewLinesAfter(1)
                        .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)

                printables.add(builder.build())
            }
        }

        printing?.print(printables)
    }

    override fun connectingWithPrinter() {

    }

    override fun connectionFailed(error: String) {
        TODO("Not yet implemented")
    }

    override fun onError(error: String) {
        TODO("Not yet implemented")
    }

    override fun onMessage(message: String) {
        TODO("Not yet implemented")
    }

    override fun printingOrderSentSuccessfully() {
        TODO("Not yet implemented")
    }
}