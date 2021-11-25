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
import com.sunlotocenter.extensions.gameCategories
import com.sunlotocenter.extensions.gameTypes
import com.sunlotocenter.model.GameViewModel
import com.sunlotocenter.utils.BLOCKED_GAME_EXTRA
import com.sunlotocenter.utils.ClickListener
import com.sunlotocenter.utils.DialogType
import com.sunlotocenter.validator.Form
import com.sunlotocenter.validator.LengthValidator
import com.sunlotocenter.validator.MinLengthValidator
import kotlinx.android.synthetic.main.activity_blocked_game.*
import kotlinx.android.synthetic.main.activity_blocked_game.toolbar
import org.modelmapper.ModelMapper

class BlockedGameActivity : ProtectedActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_blocked_game
    }

    private var gameType: GameType?= null
    private lateinit var gameViewModel: GameViewModel
    private var blockedGameExtra: BlockedGame?= null
    private var blockedGame: BlockedGame?= null
    private var selectedGameType: GameType?= null

//    private lateinit var gameTypes: Array<SpinnerItem>

    private var form= Form()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)

        gameViewModel= ViewModelProvider(this, SavedStateViewModelFactory(application, this))
            .get(GameViewModel::class.java)
//        gameTypes= arrayOf(
//            SpinnerItem("NY", getString(R.string.new_york)),
//            SpinnerItem("FL", getString(R.string.florida)),
//            SpinnerItem("GA", getString(R.string.georgia))
//        )
        prepareControl()
        observe()
        fillControl()
        btnBlock.setOnClickListener {
            submit()
        }
    }

    private fun observe() {
        gameViewModel.blockedGameData.observe(this,
            {
                dialog.dismiss()
                if(it== null){
                    com.sunlotocenter.utils.showDialog(this@BlockedGameActivity,
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
                        com.sunlotocenter.utils.showDialog(this@BlockedGameActivity,
                            getString(R.string.success_title),
                            getString(
                                if (blockedGameExtra == null) R.string.create_blocked_game_success_message
                                else R.string.update_blocked_game_success_message

                            ),
                            getString(R.string.ok),
                            object : ClickListener {
                                override fun onClick(): Boolean {
                                    setResult(Activity.RESULT_OK, Intent())
                                    finish()
                                    return false
                                }

                            }, false, DialogType.SUCCESS)
                    }else{
                        com.sunlotocenter.utils.showDialog(this@BlockedGameActivity,
                            getString(R.string.internet_error_title),
                            it.message!!,
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
        blockedGameExtra= intent.extras?.getSerializable(BLOCKED_GAME_EXTRA) as BlockedGame?
        if (blockedGameExtra!= null){

            val gameCategoryArray= gameCategories()
            val gameCategoryAdapter= ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
                gameCategoryArray)
            gameCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnGame.adapter= gameCategoryAdapter
            spnGame.setTitle(getString(R.string.game))
            spnGame.setPositiveButton(getString(R.string.ok))
            if(blockedGameExtra!= null){
                gameCategoryArray.forEachIndexed { i, s ->
                    if(s.id== blockedGameExtra!!.classType.name){
                        spnGame.setSelection(i)
                    }
                }
            }

            spnGame.onItemSelectedListener= object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                    selectedWorker= sellerAdapter.getItem(position)
                }

            }


            UserType.values().forEachIndexed{ index, element->
                if(element.id == blockedGameExtra!!.type?.id){
                    spnType.setSelection(index)
                }
            }

            val gameTypeArray= gameTypes()
            val gameTypeAdapter= ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
                gameTypeArray)
            gameTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnType.adapter= gameTypeAdapter
            spnType.setTitle(getString(R.string.type))
            spnType.setPositiveButton(getString(R.string.ok))
            if(blockedGameExtra!= null){
                gameTypeArray.forEachIndexed { i, s ->
                    if(s.id== blockedGameExtra!!.type?.id){
                        spnType.setSelection(i)
                    }
                }
            }

            spnType.onItemSelectedListener= object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                    selectedWorker= sellerAdapter.getItem(position)
                }

            }

            blockedGameExtra?.number.let{edxGame.text= it.toString()}

        }
    }

    private fun prepareControl() {

        val gameCategoryArray= gameCategories()
        val gameCategoryAdapter= ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
            gameCategoryArray)
        gameCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnGame.adapter= gameCategoryAdapter
        spnGame.setTitle(getString(R.string.game))
        spnGame.setPositiveButton(getString(R.string.ok))

        var textWatcher: TextWatcher?= null
        spnGame.onItemSelectedListener= object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(gameCategoryAdapter.getItem(position)?.id){
                    "Borlet"->{
                        edxGame.apply {
                            setHint("00")
                            setMaxLength(2)
                            if(textWatcher!= null)
                                removeTextChangedListener(textWatcher!!)
                            text=""
                            validators.clear()
                            addValidator(LengthValidator(2, getString(R.string.length_validator_error, 2)))
                        }

                    }
                    "Marriage"->{
                        textWatcher= object :TextWatcher{
                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                            }
                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                                if(s== null) return
                                if(s.length>= 2 && before== 0 && !s.contains("X")){
                                    edxGame.text= "${s.substring(0,2)}X${s.substring(2)}"
                                    edxGame.setSelection(edxGame.text.length)
                                }
                            }
                            override fun afterTextChanged(s: Editable?) {
                                if(s?.length== 5){
                                    edxGame.focus()
                                }
                            }
                        }
                        edxGame.apply {
                            setHint("00X00")
                            setMaxLength(5)
                            if(textWatcher!= null)
                                removeTextChangedListener(textWatcher!!)
                            text=""
                            addTextChangedListener(textWatcher!!)
                            validators.clear()
                            addValidator(LengthValidator(5, getString(R.string.length_validator_error, 5)))
                        }
                    }
                    "Loto3"->{
                        edxGame.apply {
                            setHint("000")
                            setMaxLength(3)
                            if(textWatcher!= null)
                                removeTextChangedListener(textWatcher!!)
                            text=""
                            validators.clear()
                            addValidator(LengthValidator(3, getString(R.string.length_validator_error, 3)))
                        }
                    }
                    "Loto4"->{
                        edxGame.apply {
                            setHint("0000")
                            setMaxLength(4)
                            if(textWatcher!= null)
                                removeTextChangedListener(textWatcher!!)
                            text=""
                            validators.clear()
                            addValidator(LengthValidator(4, getString(R.string.length_validator_error, 4)))
                        }
                    }
                    "Loto5"->{
                        edxGame.apply {
                            setHint("00000")
                            setMaxLength(5)
                            if(textWatcher!= null)
                                removeTextChangedListener(textWatcher!!)
                            text=""
                            validators.clear()
                            addValidator(LengthValidator(5, getString(R.string.length_validator_error, 5)))
                        }
                    }
                }
            }

        }

        form.addInput(edxGame)


        val gameTypeArray= gameTypes()
        val gameTypeAdapter= ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
            gameTypeArray)
        gameTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnType.adapter= gameTypeAdapter
        spnType.setTitle(getString(R.string.type))
        spnType.setPositiveButton(getString(R.string.ok))
        spnType.onItemSelectedListener= object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                GameType.values().forEach {
                    if(it.id== gameTypeAdapter.getItem(position)!!.id)
                        selectedGameType= it
                }
            }

        }
    }

    private fun submit() {
        if(form.isValid()){
            blockedGame= BlockedGame(sequence = Sequence(), number = edxGame.text, author = MyApplication.getInstance().connectedUser!!)
            if(blockedGameExtra!= null){
                ModelMapper().map(blockedGameExtra, blockedGame)
            }
            blockedGame?.apply {
                number= edxGame.text.toString()
                author = MyApplication.getInstance().connectedUser!!
                type= selectedGameType
                current= true
            }
            dialog.show()
            gameViewModel.saveBlockedGame(blockedGame!!)
        }
    }
}