package com.sunlotocenter.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.sunlotocenter.MyApplication

abstract class ProtectedActivity : CheckInternetActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())

        checkLogin()

        //For having white icon on the status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decor = window.decorView
            decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

    }

    override fun onResume() {
        super.onResume()
        checkLogin()
    }


    private fun checkLogin() {
        if(!MyApplication.getInstance().isLoggedIn){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId== android.R.id.home)finish()
        return super.onOptionsItemSelected(item)
    }

}
