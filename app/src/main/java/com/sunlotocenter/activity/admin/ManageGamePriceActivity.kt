package com.sunlotocenter.activity.admin

import android.os.Bundle
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import com.sunlotocenter.MyApplication
import com.sunlotocenter.activity.ProtectedActivity
import com.sunlotocenter.activity.R
import com.sunlotocenter.dao.GamePrice
import com.sunlotocenter.dao.GameSchedule
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.model.GameViewModel
import com.sunlotocenter.utils.ClickListener
import com.sunlotocenter.utils.DialogType
import com.sunlotocenter.utils.GAME_PRICE_EXTRA
import com.sunlotocenter.validator.Form
import com.sunlotocenter.validator.GreaterThanZeroValidator
import kotlinx.android.synthetic.main.activity_manage_game_price.*
import kotlinx.android.synthetic.main.activity_manage_game_price.toolbar
import org.modelmapper.ModelMapper

class ManageGamePriceActivity : ProtectedActivity() {
    var gamePriceFromServer: GamePrice?= null
    var gamePrice: GamePrice?= null
    private var ACTION= "GET"

    var form= Form()
    private lateinit var gameViewModel: GameViewModel

    override fun getLayoutId(): Int {
        return R.layout.activity_manage_game_price
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)

        gameViewModel= ViewModelProvider(this, SavedStateViewModelFactory(application, this))
            .get(GameViewModel::class.java)

        btnSubmit.setOnClickListener {
            ACTION= "SAVE"
            submit()
        }

        dialog.show()
        gameViewModel.getGamePrice()

        addValidator()
        observe()
    }

    private fun prefill(gamePrice: GamePrice?) {
        if(gamePrice!= null){
            edxLo1Price.text= gamePrice.lo1Price.toString()
            edxLo1Price.setSelection(edxLo1Price.text.length)
            edxLo2Price.text= gamePrice.lo2Price.toString()
            edxLo3Price.text= gamePrice.lo3Price.toString()
            edxMarriagePrice.text= gamePrice.marriagePrice.toString()
            edxLoto3Price.text= gamePrice.loto3Price.toString()
            edxLoto4Price.text= gamePrice.loto4Price.toString()
            edxLoto5Price.text= gamePrice.loto5Price.toString()
        }
    }

    private fun submit() {
        if(form.isValid()){
            gamePrice= GamePrice(sequence = com.sunlotocenter.dao.Sequence(), author = MyApplication.getInstance().connectedUser)
            if(gamePriceFromServer!= null){
                ModelMapper().map(gamePriceFromServer, gamePrice)
            }
            gamePrice?.apply {
                lo1Price = edxLo1Price.text.toInt()
                lo2Price = edxLo2Price.text.toInt()
                lo3Price = edxLo3Price.text.toInt()
                marriagePrice = edxMarriagePrice.text.toInt()
                loto3Price= edxLoto3Price.text.toInt()
                loto4Price= edxLoto4Price.text.toInt()
                loto5Price= edxLoto5Price.text.toInt()
                author = MyApplication.getInstance().connectedUser
            }

            dialog.show()
            gameViewModel.saveGamePrice(gamePrice!!)
        }
    }

    private fun addValidator() {
        edxLo1Price.addValidator(GreaterThanZeroValidator(getString(R.string.greater_than_zero_validator_error)))
        edxLo2Price.addValidator(GreaterThanZeroValidator(getString(R.string.greater_than_zero_validator_error)))
        edxLo3Price.addValidator(GreaterThanZeroValidator(getString(R.string.greater_than_zero_validator_error)))
        edxMarriagePrice.addValidator(GreaterThanZeroValidator(getString(R.string.greater_than_zero_validator_error)))
        edxLoto3Price.addValidator(GreaterThanZeroValidator(getString(R.string.greater_than_zero_validator_error)))
        edxLoto4Price.addValidator(GreaterThanZeroValidator(getString(R.string.greater_than_zero_validator_error)))
        edxLoto5Price.addValidator(GreaterThanZeroValidator(getString(R.string.greater_than_zero_validator_error)))
        form.addInput(edxLo1Price, edxLo2Price, edxLo3Price, edxMarriagePrice, edxLoto3Price, edxLoto4Price, edxLoto5Price)
    }

    private fun observe() {
        gameViewModel.gamePriceData.observe(this,
            {
                dialog.dismiss()
                if(it== null){
                    if(ACTION== "SAVE")
                        com.sunlotocenter.utils.showDialog(this@ManageGamePriceActivity,
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
                    if(ACTION== "SAVE")
                        com.sunlotocenter.utils.showDialog(this@ManageGamePriceActivity,
                            getString(R.string.success_title),
                            getString(
                                if (gamePriceFromServer == null) R.string.create_game_price_success_message
                                else R.string.update_game_price_success_message

                            ),
                            getString(R.string.ok),
                            object : ClickListener {
                                override fun onClick(): Boolean {
                                    finish()
                                    return false
                                }

                            }, false, DialogType.SUCCESS)
                    else {
                        gamePriceFromServer= it.data
                        prefill(gamePriceFromServer)
                    }
                }
            })
    }
}