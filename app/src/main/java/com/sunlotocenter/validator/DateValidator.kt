package com.sunlotocenter.validator

import android.util.Log
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormatterBuilder
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat

class DateValidator (errMsg: String): MyValidator(errMsg) {
    override fun isValid(): Boolean {
        return try {
            //If there is no exeception, it's fine
            DateTime.parse(editText.text, DateTimeFormat.forPattern("dd-MM-yyyy"))
            true
        } catch (e: Exception) {
            false
        }
    }
}