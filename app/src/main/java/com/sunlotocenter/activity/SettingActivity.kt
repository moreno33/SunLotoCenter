package com.sunlotocenter.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.sunlotocenter.BuildConfig
import com.sunlotocenter.MyApplication
import com.sunlotocenter.R
import com.sunlotocenter.activity.admin.*
import com.sunlotocenter.dao.Seller
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.utils.COMPANY_EXTRA
import com.sunlotocenter.utils.USER_EXTRA
import kotlinx.android.synthetic.main.activity_admin_setting.*

class SettingActivity : ProtectedActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_admin_setting
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)
        setVersion()
        //logout
        txtLogout.setOnClickListener {
            MyApplication.getInstance().logout()
        }

        //Control what to display
        controlUserDisplay()
    }

    private fun controlUserDisplay() {
        val connectedUser= MyApplication.getInstance().connectedUser
        if(connectedUser is Seller){
            clCompanyInfo.visibility= View.GONE
            clManageGame.visibility= View.GONE
            clChangeGamePrice.visibility= View.GONE
            clLimit.visibility= View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        manageClick(mapOf(Pair(clPersonalInfo, AdminPersonalInfoActivity::class.java),
            Pair(clCompanyInfo, CompanyInfoActivity::class.java),
            Pair(clManageGame, ManageGameActivity::class.java),
            Pair(clChangePassword, ChangePasswordActivity::class.java),
            Pair(clChangeGamePrice, ManageGamePriceActivity::class.java),
            Pair(clLimit, PreventTroubleActivity::class.java)))
    }

    private fun manageClick(actions: Map<ConstraintLayout, Class<out AppCompatActivity>>) {
        actions.forEach { action->
            action.key.setOnClickListener {
                var intent= Intent(this, action.value)
                intent.putExtra(USER_EXTRA, MyApplication.getInstance().connectedUser)
                intent.putExtra(COMPANY_EXTRA, MyApplication.getInstance().company)
                startActivity(intent)
            }
        }
    }

    private fun setVersion() {
        txtVersion.text= getString(R.string.version_code, BuildConfig.VERSION_CODE)
    }
}