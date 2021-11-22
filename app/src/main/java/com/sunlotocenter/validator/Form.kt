package com.sunlotocenter.validator

import com.emarkall.worldwidephonenumberedittext.Validable

class Form {
    val _inputs= ArrayList<Validable>()
    fun addInput(vararg inputs: Validable){
        inputs.forEach { e-> _inputs.add(e) }
    }

    fun isValid(): Boolean{
        var isvalid= true
        _inputs.forEach {
            if(!it.isValid){
                isvalid= false
            }
        }
        return isvalid
    }
}