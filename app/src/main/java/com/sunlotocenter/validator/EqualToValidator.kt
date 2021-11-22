package com.sunlotocenter.validator


class EqualToValidator(var other: ValidatableEditText, errMsg: String) : MyValidator(errMsg) {
    override fun isValid(): Boolean {
        return other.text == editText.text
    }
}