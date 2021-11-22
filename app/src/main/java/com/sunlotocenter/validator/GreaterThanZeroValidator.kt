package com.sunlotocenter.validator

class GreaterThanZeroValidator(errMsg:String):MyValidator(errMsg) {
    override fun isValid(): Boolean {
        return editText.text.isNotEmpty() && editText.text.toDouble()>0
    }
}