package com.sunlotocenter.activity.admin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.sunlotocenter.MyApplication
import com.sunlotocenter.activity.*
import com.sunlotocenter.dao.Sex
import com.sunlotocenter.dao.User
import com.sunlotocenter.dao.UserType
import com.sunlotocenter.utils.glide
import kotlinx.android.synthetic.main.activity_admin_dashboard.*
import kotlinx.android.synthetic.main.activity_admin_dashboard.toolbar

class AdminDashboardActivity : ProtectedActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_admin_dashboard
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        loadScreen()

        manageClick(mapOf(Pair(clEmployees, EmployeeListActivity::class.java),
            Pair(clReports, AdminReportActivity::class.java),
            Pair(clResults, ResultListActivity::class.java),
            Pair(clBanks, BankListActivity::class.java)
//            Pair(clLimit, PreventTroubleActivity::class.java)
            ))

        btnPlay.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }
    }

    private fun loadScreen() {
        glide(this, MyApplication.getInstance().connectedUser.profilePath, imgAdmin, R.drawable.background_gray, getProfileImage(MyApplication.getInstance().connectedUser))

        txtName.text= "${MyApplication.getInstance().connectedUser.firstName} ${MyApplication.getInstance().connectedUser.lastName}"
        txtPosition.text= UserType.ADMIN.id
    }

    private fun getProfileImage(user: User?): Int {
        if(user== null){
            return R.drawable.admin_male_icon
        }else{
            if (user.sex== Sex.MALE){
                return R.drawable.admin_male_icon
            }else{
                return R.drawable.woman_icon
            }
        }

    }

    private fun manageClick(actions: Map<ConstraintLayout, Class<out AppCompatActivity>>) {
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
            R.id.mnBroadcast->{
                startActivity(Intent(this, AdminBroadcastActivity::class.java))
                return  true
            }
        }
        return false
    }
}