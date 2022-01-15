package com.sunlotocenter.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sunlotocenter.R
import com.sunlotocenter.extensions.enableHome
import kotlinx.android.synthetic.main.activity_admin_setting.*

class VersionActivity : ProtectedActivity() {

    override fun getLayoutId(): Int= R.layout.activity_version

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)
    }
}