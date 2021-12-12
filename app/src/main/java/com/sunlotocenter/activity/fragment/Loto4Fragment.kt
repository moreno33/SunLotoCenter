package com.sunlotocenter.activity.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sunlotocenter.R
import com.sunlotocenter.dao.Loto4
import com.sunlotocenter.listener.AddGameListener
import com.sunlotocenter.utils.ClickListener
import com.sunlotocenter.utils.showDialog
import com.sunlotocenter.validator.Form
import com.sunlotocenter.validator.LengthValidator
import kotlinx.android.synthetic.main.fragment_loto4.*
import kotlinx.android.synthetic.main.fragment_loto4.view.*
import kotlinx.android.synthetic.main.option_price_layout.view.*

class Loto4Fragment(private var addGameListener: AddGameListener) : Fragment() {
    private lateinit var form: Form

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_loto4, container, false)
        val content= inflater.inflate(R.layout.option_price_layout, null)
        view.btnAdd.setOnClickListener {
            validateAndAdd(view, content)
        }
        return view
    }

    private fun validateAndAdd(view: View, content:View) {

        form=  Form()

        view.edxNumber.addValidator(
            LengthValidator(4, getString(R.string.length_validator_error, 4))
        )
        form.addInput(view.edxNumber)
        if(form.isValid()){
            showDialog(this.context!!, getString(R.string.option), content, getString(R.string.add), getString(R.string.cancel), object :ClickListener{
                override fun onClick(): Boolean {
                    val amount1= content.edxAmount1.text
                    if(amount1.isNotEmpty()){
                        val loto4= Loto4(view.edxNumber.text, amount1.toDouble(), "1", 1)
                        addGameListener.addGame(loto4)
                    }
                    val amount2= content.edxAmount2.text
                    if(amount2.isNotEmpty()){
                        val loto4= Loto4(view.edxNumber.text, amount2.toDouble(), "2", 1)
                        addGameListener.addGame(loto4)
                    }
                    val amount3= content.edxAmount3.text
                    if(amount3.isNotEmpty()){
                        val loto4= Loto4(view.edxNumber.text, amount1.toDouble(), "3", 1)
                        addGameListener.addGame(loto4)
                    }
                    edxNumber.text= ""
                    (content.parent as ViewGroup).removeView(content)
                    return false
                }

            }, object:ClickListener{
                override fun onClick(): Boolean {
                    (content.parent as ViewGroup).removeView(content)
                    return false
                }

            })


        }
    }

}