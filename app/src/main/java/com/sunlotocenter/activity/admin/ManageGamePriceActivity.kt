package com.sunlotocenter.activity.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sunlotocenter.activity.BasicActivity
import com.sunlotocenter.activity.R
import com.sunlotocenter.extensions.enableHome
import kotlinx.android.synthetic.main.activity_manage_game.*

class ManageGamePriceActivity : BasicActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_manage_game_price
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)

        btnAdd.setOnClickListener {
            //startActivityForResult()
        }
    }
}