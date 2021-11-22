package com.sunlotocenter.activity.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sunlotocenter.activity.ProtectedActivity
import com.sunlotocenter.activity.R
import com.sunlotocenter.extensions.enableHome
import kotlinx.android.synthetic.main.activity_change_password.*

class AdminBroadcastActivity : ProtectedActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_broadcast
    }
}