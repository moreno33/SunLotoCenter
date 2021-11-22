package com.sunlotocenter.validator

class MaxLengthValidator (var length: Int, errMsg: String): MyValidator(errMsg) {
    override fun isValid(): Boolean {
        return editText.text.length <= length
    }
}