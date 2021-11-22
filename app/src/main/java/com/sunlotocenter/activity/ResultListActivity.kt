package com.sunlotocenter.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sunlotocenter.extensions.enableHome
import kotlinx.android.synthetic.main.activity_result_list.*

class ResultListActivity : ProtectedActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_result_list
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)
    }
}