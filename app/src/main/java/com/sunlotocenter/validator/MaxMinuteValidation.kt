package com.sunlotocenter.validator

import java.lang.Exception

class MaxMinuteValidation (var max: Int, errMsg: String): MyValidator(errMsg) {
    override fun isValid(): Boolean {
        try {
            var min= editText.text.toInt()
            return min<= max
        }catch (e: Exception){return false}

    }
}