package com.sunlotocenter.activity.admin

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
import com.sunlotocenter.model.GameViewModel
import com.sunlotocenter.utils.ClickListener
import com.sunlotocenter.utils.DialogType
import com.sunlotocenter.validator.Form
import com.sunlotocenter.validator.LengthValidator
import com.sunlotocenter.validator.MinLengthValidator
import kotlinx.android.synthetic.main.activity_game_sechedule.*
import kotlinx.android.synthetic.main.activity_game_sechedule.btnAdd
import kotlinx.android.synthetic.main.fragment_marriage.*
import kotlinx.android.synthetic.main.fragment_marriage.view.*
import org.modelmapper.ModelMapper

class GameScheduleActivity : ProtectedActivity() {

    companion object {
        val GAME_EXTRA= "com.sunlotocenter.activity.admin.GAME_EXTRA"
        val GAME_SCHEDULE_REQUEST_CODE= 1000
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_game_sechedule
    }

    private var gameType: GameType?= null
    private lateinit var gameViewModel: GameViewModel
    private var gameExtra: GameSchedule?= null
    private var gameSchedule: GameSchedule?= null

    private lateinit var gameTypes: Array<SpinnerItem>

    private var form= Form()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)

        gameViewModel= ViewModelProvider(this, SavedStateViewModelFactory(application, this))
            .get(GameViewModel::class.java)
        gameTypes= arrayOf(
            SpinnerItem("NY", getString(R.string.new_york)),
            SpinnerItem("FL", getString(R.string.florida)),
            SpinnerItem("GA", getString(R.string.georgia))
        )
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
                    com.sunlotocenter.utils.showDialog(this@GameScheduleActivity,
                        getString(R.string.success_title),
                        getString(
                            if (gameExtra == null) R.string.create_game_schedule_success_message
                            else R.string.update_game_schedule_success_message

                        ),
                        getString(R.string.ok),
                        object : ClickListener {
                            override fun onClick(): Boolean {
                                finish()
                                return false
                            }

                        }, false, DialogType.SUCCESS)
                }
            })
    }

    private fun fillControl() {
        gameExtra= intent.extras?.getSerializable(GAME_EXTRA) as GameSchedule?
        if (gameExtra!= null){

            //Move the spinner to the right value
            UserType.values().forEachIndexed{ index, element->
                if(element.id == gameExtra!!.type!!.id){
                    spnGame.setSelection(index)
                }
            }

            gameExtra?.morningTime.let{edxMorning.text= it!!.toString()}
            gameExtra?.nightTime.let{edxNight.text= it!!.toString()}
            gameExtra?.secInterval.let{edxInterval.text= it!!.toString()}

        }else{
            toolbar.title= getString(R.string.create_employee)
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
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        //Add validator
        edxMorning.addValidator(
            LengthValidator(5, getString(R.string.length_validator_error, 5)))
        edxNight.addValidator(
            LengthValidator(5, getString(R.string.length_validator_error, 5)))
        edxInterval.addValidator(
            MinLengthValidator(1, getString(R.string.empty_validator_error))
        )

        form.addInput(edxMorning, edxNight, edxInterval)

        //Fill game spinner
        val dataAdapter= ArrayAdapter<SpinnerItem>(this, android.R.layout.simple_spinner_item, gameTypes)
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
            gameSchedule?.morningTime= edxMorning.text
            gameSchedule?.nightTime= edxNight.text
            gameSchedule?.secInterval= edxInterval.text.toLong()

            dialog.show()
            gameViewModel.saveGameSchedule(gameSchedule!!)
        }
    }
}