package com.sunlotocenter.activity

import android.os.Build
import android.os.Bundle
import android.view.View

open abstract class BasicActivity: CheckInternetActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //For having white icon on the status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decor = window.decorView
            decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}