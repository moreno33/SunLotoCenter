package com.sunlotocenter.validator

class TimeValidator(errMsg: String): MyValidator(errMsg) {
    override fun isValid(): Boolean {
        if(editText.text.length!= 5) return false
        if(!editText.text.contains(":")) return false
        if(editText.text.split(":").size!= 2) return false
        if(editText.text.split(":")[0].toInt()> 23) return false
        if(editText.text.split(":")[1].toInt()>59) return false
        return true
    }
}