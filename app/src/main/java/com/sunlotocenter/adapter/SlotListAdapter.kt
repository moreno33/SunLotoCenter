package com.sunlotocenter.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.sunlotocenter.MyApplication
import com.sunlotocenter.activity.ChangePasswordActivity
import com.sunlotocenter.R
import com.sunlotocenter.activity.GameActivity
import com.sunlotocenter.activity.admin.AdminPersonalInfoActivity
import com.sunlotocenter.activity.admin.BlameListActivity
import com.sunlotocenter.activity.admin.ResultActivity
import com.sunlotocenter.dao.*
import com.sunlotocenter.dto.Result
import com.sunlotocenter.listener.SaveUserListener
import com.sunlotocenter.utils.*
import kotlinx.android.synthetic.main.activity_admin_dashboard.*
import kotlinx.android.synthetic.main.employee_layout.view.*
import kotlinx.android.synthetic.main.report_layout.view.*
import kotlinx.android.synthetic.main.result_header_layout.view.*
import kotlinx.android.synthetic.main.result_layout.view.*
import kotlinx.android.synthetic.main.result_layout.view.txtDateMorning
import kotlinx.android.synthetic.main.result_layout.view.txtDateNight
import kotlinx.android.synthetic.main.slot_layout.view.*
import java.util.*
import kotlin.collections.ArrayList

class SlotListAdapter(var slots: ArrayList<Slot>) :
    RecyclerView.Adapter<SlotListAdapter.CustomViewHolder>() {

    private val HEADER= 0
    private val ITEM= 1

    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        context= parent.context

//        if(viewType== ITEM)
        return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.slot_layout, null))
//        else return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.result_header_layout, null))
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        var slot= slots[position]
        var gameAdapter= GameSlotAdapter(TreeSet(slot.games))

        holder.rclSlotRow.apply {
            setHasFixedSize(true)
            layoutManager= LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter= gameAdapter
        }
        holder.imgCopy.setOnClickListener {
            val filteredGameList= getFilteredGameList(slot.games)
            context.startActivity(Intent(context, GameActivity::class.java).putExtra(COPIED_GAME_LIST, filteredGameList))
        }
    }

    private fun getFilteredGameList(games: List<Game>): ArrayList<Game> {
        return ArrayList(games.filter { g->g.type== 1 })
    }


    override fun getItemViewType(position: Int): Int {
        if (position== 0) return HEADER
        else return ITEM
    }

    override fun getItemCount(): Int {
        return slots.size
    }

    class CustomViewHolder(var item: View) : RecyclerView.ViewHolder(item){
        val rclSlotRow by lazy { item.rclSlotRow }
        val imgCopy by lazy { item.imgCopy }
    }
}