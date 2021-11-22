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
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.sunlotocenter.activity.ChangePasswordActivity
import com.sunlotocenter.activity.R
import com.sunlotocenter.activity.admin.AdminPersonalInfoActivity
import com.sunlotocenter.activity.admin.BlameListActivity
import com.sunlotocenter.activity.admin.CreateBankActivity
import com.sunlotocenter.dao.Bank
import com.sunlotocenter.dao.Sex
import com.sunlotocenter.dao.User
import com.sunlotocenter.dao.UserStatus
import com.sunlotocenter.listener.SaveBankListener
import com.sunlotocenter.utils.BANK_EXTRA
import com.sunlotocenter.utils.REFRESH_REQUEST_CODE
import com.sunlotocenter.utils.getDateString
import kotlinx.android.synthetic.main.bank_layout.view.*

class BankListAdapter(var banks: ArrayList<Bank>, var saveBankListener: SaveBankListener) : RecyclerView.Adapter<BankListAdapter.CustomViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        context= parent.context
        return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.bank_layout, null))
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        var bank= banks[position]
        holder.txtName.text= "${bank.name} (${bank.code})"
        holder.txtStartDate.text= context.getString(R.string.since, getDateString(bank.createdDateTime!!))

//        if(employee.status== UserStatus.ACTIVE && employee.blames.isEmpty())
//            holder.vwStatus.background= ContextCompat.getDrawable(context, R.drawable.green_circle_background)
//        else if(employee.status== UserStatus.INACTIVE || employee.blames.isNotEmpty())
//            holder.vwStatus.background= ContextCompat.getDrawable(context, R.drawable.yellow_circle_background)
//        else
//            holder.vwStatus.background= ContextCompat.getDrawable(context, R.drawable.red_circle_background)

        if(bank.profilePath.isNullOrEmpty()){
            Glide
                .with(context)
                .load(R.drawable.bank_image)
                .into(holder.imgProfile)

        }else{
            Glide
                .with(context)
                .load(bank.profilePath)
                .into(holder.imgProfile)
        }

//        //Click to open menu
        holder.imgMenu.setOnClickListener {
            showMenu(it, bank)
        }

    }

    private fun showMenu(view: View?, bank: Bank) {
        val popupMenu = popupMenu {
            section {
                item {
                    labelRes = R.string.change
                    icon = R.drawable.pencil_outline_black_18
                    callback = {
                        (context as AppCompatActivity).startActivityForResult(Intent(context, CreateBankActivity::class.java).putExtra(
                            BANK_EXTRA, bank), REFRESH_REQUEST_CODE)
                    }
                }
//                item {
//                    var res= 0
//                    if(employee.status== UserStatus.ACTIVE || employee.status== UserStatus.INACTIVE){
//                        res= R.string.block_user
//                    } else{
//                        res= R.string.un_block_user
//                    }
//
//                    labelRes = res
//                    icon = R.drawable.account_cancel_outline_black_18
//                    callback = {
//                        if(employee.status== UserStatus.ACTIVE || employee.status== UserStatus.INACTIVE){
//                            employee.status= UserStatus.BLOCKED
//                        } else{
//                            employee.status= UserStatus.ACTIVE
//                        }
////                        saveUserListener.save(employee)
//                    }
//                }
//                item {
//                    labelRes = R.string.reprimand
//                    icon = R.drawable.account_alert_outline_black_18
//                    callback = {
////                        (context as AppCompatActivity).startActivityForResult(Intent(context, BlameListActivity::class.java).putExtra(
////                            USER_EXTRA, employee), REFRESH_REQUEST_CODE)
//                    }
//                }
//                item {
//                    labelRes = R.string.balance
//                    icon = R.drawable.wallet_outline_black_18
//                    callback = {
//
//                    }
//                }
            }
        }

        popupMenu.show(context, view!!)
    }

    override fun getItemCount(): Int {
        return banks.size
    }

    class CustomViewHolder(var item: View) : RecyclerView.ViewHolder(item){
        var txtName= item.txtName
        var txtStartDate= item.txtDate
        var imgProfile= item.imgProfile
        var imgMenu= item.imgMenu
    }
}