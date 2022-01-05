package com.sunlotocenter.activity.admin

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import com.sunlotocenter.MyApplication
import com.sunlotocenter.activity.ProtectedActivity
import com.sunlotocenter.R
import com.sunlotocenter.dao.*
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.extensions.gameSessions
import com.sunlotocenter.extensions.gameTypes
import com.sunlotocenter.model.GameViewModel
import com.sunlotocenter.utils.*
import com.sunlotocenter.validator.*
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.activity_result.spnType
import kotlinx.android.synthetic.main.activity_result.toolbar
import kotlinx.android.synthetic.main.validatable_edit_text_layout.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.modelmapper.ModelMapper

class ResultActivity : ProtectedActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_result
    }

    private var gameType: GameType?= null
    private var gameSession: GameSession?= null
    private lateinit var gameViewModel: GameViewModel
    private var resultExtra: GameResult?= null
    private var result: GameResult?= null

    private var form= Form()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)

        gameViewModel= ViewModelProvider(this, SavedStateViewModelFactory(application, this))
            .get(GameViewModel::class.java)
        prepareControl()
        observe()
        fillControl()
    }

    private fun observe() {
        gameViewModel.gameResultData.observe(this,
            {
                dialog.dismiss()
                if(it== null){
                    showDialog(this@ResultActivity,
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
                    if(it.success){
                        showDialog(this@ResultActivity,
                            getString(R.string.success_title),
                            getString(
                                if (resultExtra == null) R.string.create_result_success_message
                                else R.string.updaate_result_success_message

                            ),
                            getString(R.string.ok),
                            object : ClickListener {
                                override fun onClick(): Boolean {
                                    setResult(Activity.RESULT_OK)
                                    finish()
                                    return false
                                }

                            }, false, DialogType.SUCCESS)
                    }else{
                        showDialog(this@ResultActivity,
                            getString(R.string.internet_error_title), it.message!!,
                            getString(R.string.ok),
                            object : ClickListener {
                                override fun onClick(): Boolean {
                                    return false
                                }

                            }, false, DialogType.ERROR)
                    }
                }
            })
    }

    private fun fillControl() {
        resultExtra= intent.extras?.getSerializable(RESULT_EXTRA) as GameResult?
        if (resultExtra!= null){

            edxDate.text= getDateString(resultExtra?.resultDate, DateTimeFormat.forPattern("dd-MM-yyyy"), DateTimeZone.forID("America/New_York"))!!
            //Move the spinners to the right value
            gameTypes().forEachIndexed{ index, element->
                if(element.id == resultExtra!!.type!!.id){
                    spnType.setSelection(index)
                }
            }
            gameSessions().forEachIndexed{ index, element->
                if(element.id == resultExtra!!.session!!.id){
                    spnSession.setSelection(index)
                }
            }
            resultExtra?.lo1?.let{edxLo1.text= it }
            resultExtra?.lo2?.let{edxLo2.text= it }
            resultExtra?.lo3?.let{edxLo3.text= it }
        }else{
            edxDate.text= getDateString(DateTime.now(), DateTimeFormat.forPattern("dd-MM-yyyy"), DateTimeZone.forID("America/New_York"))!!
        }

    }

    private fun prepareControl() {

        edxDate.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s== null) return
                if(s.length>= 2 && before== 0 && !s.contains("-")){
                    edxDate.text= "${s.substring(0,2)}-${s.substring(2)}"
                    edxDate.setSelection(edxDate.text.length)
                }else if(s.length>= 5 && before== 0 && !s.substring(3).contains("-")){
                    edxDate.text= "${s.substring(0,5)}-${s.substring(5)}"
                    edxDate.setSelection(edxDate.text.length)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        edxLo1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s?.length== 3) edxLo2.focus()
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        edxLo2.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s?.length== 2) edxLo3.focus()
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        //Add validator
        edxDate.addValidator(DateValidator(getString(R.string.date_validator_error)))
        edxLo1.addValidator(LengthValidator(3, getString(R.string.length_validator_error, 3)))
        edxLo2.addValidator(LengthValidator(2, getString(R.string.length_validator_error, 2)))
        edxLo3.addValidator(LengthValidator(2, getString(R.string.length_validator_error, 2)))


        form.addInput(edxDate, edxLo1, edxLo2, edxLo3)

        //Fill spinners
        val typeAdapter= ArrayAdapter(this, android.R.layout.simple_spinner_item, gameTypes())
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnType.adapter = typeAdapter
        spnType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                GameType.values().forEach {
                    if(it.id == typeAdapter.getItem(position)!!.id){
                        gameType= it
                    }
                }
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }

        val sessionAdapter= ArrayAdapter(this, android.R.layout.simple_spinner_item, gameSessions())
        sessionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnSession.adapter = sessionAdapter
        spnSession.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                GameSession.values().forEach {
                    if(it.id == sessionAdapter.getItem(position)!!.id){
                        gameSession= it
                    }
                }
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }


        btnAdd.setOnClickListener {
            submit()
        }
    }

    private fun submit() {
        if(form.isValid()){
            result= GameResult(sequence = Sequence(), author = MyApplication.getInstance().connectedUser!!, company = MyApplication.getInstance().company)
            if(resultExtra!= null){
                ModelMapper().map(resultExtra, result)
            }
            result?.type= gameType
            result?.session= gameSession
            result?.lo1= edxLo1.text
            result?.lo2= edxLo2.text
            result?.lo3= edxLo3.text
            result?.author= MyApplication.getInstance().connectedUser
            result?.resultDate= DateTime.parse(edxText.text.toString(), DateTimeFormat.forPattern("dd-MM-yyyy"))
                .withZone(DateTimeZone.forID("America/New_York"))
                .withTime(0, 0, 0, 0)

            dialog.show()
            gameViewModel.saveGameResult(result!!)
        }
    }
}