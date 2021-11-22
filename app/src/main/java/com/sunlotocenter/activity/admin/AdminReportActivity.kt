package com.sunlotocenter.activity.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sunlotocenter.activity.BasicActivity
import com.sunlotocenter.activity.ProtectedActivity
import com.sunlotocenter.activity.R
import com.sunlotocenter.extensions.enableHome
import kotlinx.android.synthetic.main.activity_admin_report.*

class AdminReportActivity : ProtectedActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_admin_report
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)
    }
}