package com.sunlotocenter.activity.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sunlotocenter.activity.BasicActivity
import com.sunlotocenter.activity.ProtectedActivity
import com.sunlotocenter.activity.R
import com.sunlotocenter.extensions.enableHome
import kotlinx.android.synthetic.main.activity_prevent_trouble.*

class PreventTroubleActivity : ProtectedActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_prevent_trouble
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableHome(toolbar)
    }
}