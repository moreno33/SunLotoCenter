package com.sunlotocenter.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.sunlotocenter.MyApplication
import com.sunlotocenter.activity.ChangePasswordActivity
import com.sunlotocenter.R
import com.sunlotocenter.activity.admin.AdminPersonalInfoActivity
import com.sunlotocenter.activity.admin.BlameListActivity
import com.sunlotocenter.dao.Sex
import com.sunlotocenter.dao.User
import com.sunlotocenter.dao.UserStatus
import com.sunlotocenter.listener.SaveUserListener
import com.sunlotocenter.utils.REFRESH_REQUEST_CODE
import com.sunlotocenter.utils.USER_EXTRA
import com.sunlotocenter.utils.getDateString
import com.sunlotocenter.utils.glide
import kotlinx.android.synthetic.main.activity_admin_dashboard.*
import kotlinx.android.synthetic.main.employee_layout.view.*

class EmployeeListAdapter(var employees: ArrayList<User>, var saveUserListener: SaveUserListener) : RecyclerView.Adapter<EmployeeListAdapter.CustomViewHolder>() {

    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        context= parent.context
        return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.employee_layout, null))
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        var employee= employees[position]
        holder.txtName.text= "${employee.firstName} ${employee.lastName}"
        holder.txtStartDate.text= context.getString(R.string.since, getDateString(employee.createdDateTime!!))
        holder.txtAlert.text= context.getString(R.string.reprimand_count, employee.blames?.size)
        if(employee.status== UserStatus.ACTIVE && employee.blames.isNullOrEmpty())
            holder.vwStatus.background= ContextCompat.getDrawable(context, R.drawable.green_circle_background)
        else if(employee.status== UserStatus.INACTIVE || (!employee.blames.isNullOrEmpty() && employee.status!= UserStatus.BLOCKED))
            holder.vwStatus.background= ContextCompat.getDrawable(context, R.drawable.yellow_circle_background)
        else
            holder.vwStatus.background= ContextCompat.getDrawable(context, R.drawable.red_circle_background)

//        glide(context, employee.profilePath, holder.imgProfile, R.drawable.background_gray, getProfileImage(employee))
        glide(context,
            employee.profilePath,
            holder.imgProfile,
            R.drawable.background_gray,
            getProfileImage(employee))



        //Click to open menu
        holder.imgMenu.setOnClickListener {
            showMenu(it, employee)
        }

    }

    private fun getProfileImage(user: User?): Int {
        return if(user== null){
            R.drawable.admin_male_icon
        }else{
            if (user.sex== Sex.MALE){
                R.drawable.admin_male_icon
            }else{
                R.drawable.woman_icon
            }
        }

    }

    private fun showMenu(view: View?, employee: User) {
        val popupMenu = popupMenu {
            section {
                item {
                    labelRes = R.string.update_user
                    icon = R.drawable.account_outline_18
                    callback = {
                        (context as AppCompatActivity).startActivityForResult(Intent(context, AdminPersonalInfoActivity::class.java).putExtra(
                            USER_EXTRA, employee), REFRESH_REQUEST_CODE)
                    }
                }
                item {
                    labelRes = R.string.addOrChangePassword
                    icon = R.drawable.lock_outline_18
                    callback = {
                        (context as AppCompatActivity).startActivityForResult(Intent(context, ChangePasswordActivity::class.java).putExtra(
                            USER_EXTRA, employee), REFRESH_REQUEST_CODE)
                    }
                }
                item {
                    var res= 0
                    if(employee.status== UserStatus.ACTIVE || employee.status== UserStatus.INACTIVE){
                        res= R.string.block_user
                    } else{
                        res= R.string.un_block_user
                    }

                    labelRes = res
                    icon = R.drawable.account_cancel_outline_black_18
                    callback = {
                        if(employee.status== UserStatus.ACTIVE || employee.status== UserStatus.INACTIVE){
                            employee.status= UserStatus.BLOCKED
                        } else{
                            employee.status= UserStatus.ACTIVE
                        }
                        saveUserListener.save(employee)
                    }
                }
                item {
                    labelRes = R.string.reprimand
                    icon = R.drawable.account_alert_outline_black_18
                    callback = {
                        (context as AppCompatActivity).startActivityForResult(Intent(context, BlameListActivity::class.java).putExtra(
                            USER_EXTRA, employee), REFRESH_REQUEST_CODE)
                    }
                }
                item {
                    labelRes = R.string.balance
                    icon = R.drawable.wallet_outline_black_18
                    callback = {

                    }
                }
            }
        }

        popupMenu.show(context, view!!)
    }

    override fun getItemCount(): Int {
        return employees.size
    }

    class CustomViewHolder(var item: View) : RecyclerView.ViewHolder(item){
        val txtName by lazy { item.txtName }
        val txtAlert by lazy { item.txtAlert }
        val txtStartDate by lazy { item.txtTime }
        val vwStatus by lazy { item.vwStatus }
        val imgProfile by lazy { item.imgProfile }
        val imgMenu by lazy { item.imgMenu }
    }
}