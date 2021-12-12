package com.sunlotocenter.activity.seller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.sunlotocenter.R
import com.sunlotocenter.activity.*
import com.sunlotocenter.activity.SettingActivity
import com.sunlotocenter.activity.admin.NotificationActivity
import kotlinx.android.synthetic.main.activity_seller_dashboard.*

class SellerDashboardActivity : ProtectedActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_seller_dashboard
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        manageClick(mapOf(
//            Pair(clReports, AdminReportActivity::class.java),
            Pair(clResults, ResultListActivity::class.java),
            Pair(btnPlay, GameActivity::class.java)
        ))
    }

    private fun manageClick(actions: Map<View, Class<out AppCompatActivity>>) {
        actions.forEach { action->
            action.key.setOnClickListener {
                startActivity(Intent(this, action.value))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        val menuInflater= menuInflater
        menuInflater.inflate(R.menu.mn_admin_dashboard, menu)

//        val view= menu?.findItem(R.id.mnNotification)?.actionView
//
//        actionViewnotificationBadge= view
//
//        initializeBottomViewNavigation()
//
//        view?.setOnClickListener {
//            startActivity(Intent(this, AdminNotificationListActivity::class.java))
//        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when(item.itemId){
            R.id.mnSettings-> {
                val intent= Intent(this, SettingActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.mnNotification->{
                startActivity(Intent(this, NotificationActivity::class.java))
                return  true
            }
        }
        return false
    }
}