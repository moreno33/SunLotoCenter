package com.sunlotocenter.validator

abstract class MyValidator(var errMsg: String) {
    lateinit var editText: ValidatableEditText
    abstract fun isValid():Boolean
}