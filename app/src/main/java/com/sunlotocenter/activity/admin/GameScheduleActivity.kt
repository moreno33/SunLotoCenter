package com.sunlotocenter.activity.admin

import android.app.Activity
import android.content.Intent
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
import com.sunlotocenter.activity.R
import com.sunlotocenter.dao.*
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.extensions.gameTypes
import com.sunlotocenter.model.GameViewModel
import com.sunlotocenter.utils.ClickListener
import com.sunlotocenter.utils.DialogType
import com.sunlotocenter.utils.GAME_SCHEDULE_EXTRA
import com.sunlotocenter.utils.REFRESH_REQUEST_CODE
import com.sunlotocenter.validator.*
import kotlinx.android.synthetic.main.activity_game_sechedule.*
import kotlinx.android.synthetic.main.activity_game_sechedule.btnAdd
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import org.modelmapper.ModelMapper

class GameScheduleActivity : ProtectedActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_game_sechedule
    }

    private var gameType: GameType?= null
    private lateinit var gameViewModel: GameViewModel
    private var gameExtra: GameSchedule?= null
    private var gameSchedule: GameSchedule?= null

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
        gameViewModel.gameScheduleData.observe(this,
            {
                dialog.dismiss()
                if(it== null){
                    com.sunlotocenter.utils.showDialog(this@GameScheduleActivity,
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
                        com.sunlotocenter.utils.showDialog(this@GameScheduleActivity,
                            getString(R.string.success_title),
                            getString(
                                if (gameExtra == null) R.string.create_game_schedule_success_message
                                else R.string.update_game_schedule_success_message

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
                        com.sunlotocenter.utils.showDialog(this@GameScheduleActivity,
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
        gameExtra= intent.extras?.getSerializable(GAME_SCHEDULE_EXTRA) as GameSchedule?
        if (gameExtra!= null){

            //Move the spinner to the right value
            gameTypes().forEachIndexed{ index, element->
                if(element.id == gameExtra!!.type!!.id){
                    spnGame.setSelection(index)
                }
            }

            gameExtra?.morningTime.let{edxMorning.text= it!!.toString()}
            gameExtra?.nightTime.let{edxNight.text= it!!.toString()}
            gameExtra?.secInterval.let{edxInterval.text= it!!.toString()}

        }
    }

    private fun prepareControl() {

        edxMorning.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s== null) return
                if(s.length>= 2 && before== 0 && !s.contains(":")){
                    edxMorning.text= "${s.substring(0,2)}:${s.substring(2)}"
                    edxMorning.setSelection(edxMorning.text.length)
                }
                if(s.length>= 5)
                    edxNight.focus()
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        edxNight.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s== null) return
                if(s.length>= 2 && before== 0 && !s.contains(":")){
                    edxNight.text= "${s.substring(0,2)}:${s.substring(2)}"
                    edxNight.setSelection(edxNight.text.length)
                }
                if(s.length>= 5)
                    edxInterval.focus()
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        //Add validator
        edxMorning.addValidator(TimeValidator(getString(R.string.bad_time_validator_error)))
        edxNight.addValidator(TimeValidator(getString(R.string.bad_time_validator_error)))
        edxInterval.addValidator(MaxMinuteValidation(60, getString(R.string.max_min_validator_error, 60)))

        form.addInput(edxMorning, edxNight, edxInterval)

        //Fill game spinner
        val dataAdapter= ArrayAdapter(this, android.R.layout.simple_spinner_item, gameTypes())
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnGame.adapter = dataAdapter
        spnGame.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                GameType.values().forEach {
                    if(it.id == dataAdapter.getItem(position)!!.id){
                        gameType= it
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
            gameSchedule= GameSchedule(sequence = Sequence(), author = MyApplication.getInstance().connectedUser!!)
            if(gameExtra!= null){
                ModelMapper().map(gameExtra, gameSchedule)
            }
            gameSchedule?.type= gameType
            gameSchedule?.morningTime= LocalTime.parse(edxMorning.text, DateTimeFormat.forPattern("HH:mm"))
            gameSchedule?.nightTime= LocalTime.parse(edxNight.text, DateTimeFormat.forPattern("HH:mm"))
            gameSchedule?.secInterval= edxInterval.text.toLong()
            gameSchedule?.author = MyApplication.getInstance().connectedUser!!

            dialog.show()
            gameViewModel.saveGameSchedule(gameSchedule!!)
        }
    }
}