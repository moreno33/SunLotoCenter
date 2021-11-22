package com.sunlotocenter.activity.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sunlotocenter.activity.R
import com.sunlotocenter.dao.Borlet
import com.sunlotocenter.listener.AddGameListener
import com.sunlotocenter.validator.*
import kotlinx.android.synthetic.main.fragment_borlet.*
import kotlinx.android.synthetic.main.fragment_borlet.view.*

class BorletFragment(private var addGameListener: AddGameListener) : Fragment() {
    private lateinit var form: Form

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_borlet, container, false)
        view.btnAdd.setOnClickListener {
            validateAndAdd(view)

        }

        view.edxNumber.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if(s?.length== 2){
                    view.edxAmount.focus()
                }
            }

        })
        return view
    }

    private fun validateAndAdd(view: View) {

        form=  Form()

        edxNumber.addValidator(
            LengthValidator(2, getString(R.string.length_validator_error, 2))
        )
        edxAmount.addValidator(
            MinLengthValidator(1, getString(R.string.min_length_validator_error, 1)),
            GreaterThanZeroValidator(getString(R.string.greater_than_zero_validator_error))
        )
        form.addInput(edxNumber, edxAmount)
        if(form.isValid()){
            val borlet= Borlet(edxNumber.text, edxAmount.text.toDouble(), "", 1)
            addGameListener?.addGame(borlet)
            edxNumber.text= ""
            edxAmount.text= ""
        }
    }

}