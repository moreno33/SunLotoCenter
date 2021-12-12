package com.sunlotocenter.activity.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sunlotocenter.R
import com.sunlotocenter.dao.Marriage
import com.sunlotocenter.listener.AddGameListener
import com.sunlotocenter.validator.Form
import com.sunlotocenter.validator.GreaterThanZeroValidator
import com.sunlotocenter.validator.LengthValidator
import com.sunlotocenter.validator.MinLengthValidator
import kotlinx.android.synthetic.main.fragment_marriage.*
import kotlinx.android.synthetic.main.fragment_marriage.view.*

class MarriageFragment(private var addGameListener: AddGameListener) : Fragment() {
    private lateinit var form: Form

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_marriage, container, false)
        view.btnAdd.setOnClickListener {
            validateAndAdd(view)

        }
        formatEdxNumber(view)
        return view
    }

    private fun formatEdxNumber(view: View) {
        view.edxNumber.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s== null) return
                if(s.length>= 2 && before== 0 && !s.contains("X")){
                    view.edxNumber.text= "${s.substring(0,2)}X${s.substring(2)}"
                    view.edxNumber.setSelection(edxNumber.text.length)
                }
            }
            override fun afterTextChanged(s: Editable?) {
                if(s?.length== 5){
                    view.edxAmount.focus()
                }
            }
        })
    }

    private fun validateAndAdd(view: View) {

        form=  Form()

        edxNumber.addValidator(
            LengthValidator(5, getString(R.string.length_validator_error, 5))
        )
        edxAmount.addValidator(
            MinLengthValidator(1, getString(R.string.min_length_validator_error, 1)),
            GreaterThanZeroValidator(getString(R.string.greater_than_zero_validator_error))
        )
        form.addInput(edxNumber, edxAmount)
        if(form.isValid()){
            val marriage= Marriage(edxNumber.text, edxAmount.text.toDouble(), "", 1)
            addGameListener?.addGame(marriage)
            edxNumber.text= ""
            edxAmount.text= ""
        }
    }

}