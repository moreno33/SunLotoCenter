package com.sunlotocenter.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.sunlotocenter.MyApplication
import com.sunlotocenter.R
import com.sunlotocenter.activity.ResultListActivity
import com.sunlotocenter.activity.admin.ResultActivity
import com.sunlotocenter.dao.Admin
import com.sunlotocenter.dao.SuperAdmin
import com.sunlotocenter.dto.Result
import com.sunlotocenter.utils.*
import kotlinx.android.synthetic.main.result_header_layout.view.*
import kotlinx.android.synthetic.main.result_layout.view.*
import kotlinx.android.synthetic.main.result_layout.view.txtDateMorning
import kotlinx.android.synthetic.main.result_layout.view.txtDateNight
import org.joda.time.DateTimeZone

class ResultListAdapter(var results: ArrayList<Result>) :
    RecyclerView.Adapter<ResultListAdapter.CustomViewHolder>() {

    private val HEADER= 0
    private val ITEM= 1

    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        context= parent.context

        if(viewType== ITEM) return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.result_layout, null))
        else return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.result_header_layout, null))
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        var result= results[position]
        if(getItemViewType(position)== HEADER){
            var resultHeader= if(result.night== null) result.morning else result.night
            holder.txtFirst.text= resultHeader?.lo1
            holder.txtSecond.text= resultHeader?.lo2
            holder.txtThird.text= resultHeader?.lo3
            if(MyApplication.getInstance().connectedUser is Admin){
                holder.imgEdit.visibility= View.VISIBLE
            }else{
                holder.imgEdit.visibility= View.GONE
            }
            holder.imgEdit.setOnClickListener {
                (context as ResultListActivity).activityResult.launch(
                    Intent(context, ResultActivity::class.java).putExtra(RESULT_EXTRA, resultHeader))
            }
        }
        else{

            if (MyApplication.getInstance().connectedUser is SuperAdmin){
                holder.imgOpenMorning.visibility= View.VISIBLE
                holder.imgOpenNight.visibility= View.VISIBLE
            }else{
                holder.imgOpenMorning.visibility= View.GONE
                holder.imgOpenNight.visibility= View.GONE
            }
            holder.txtLo1Morning.text= result.morning?.lo1?:"-"
            holder.txtLo2Morning.text= result.morning?.lo2?:"-"
            holder.txtLo3Morning.text= result.morning?.  lo3?:"-"
            holder.txtDateMorning.text= result.morning?.let{ getDateString(result.morning!!.resultDate!!, DateTimeZone.forID("America/New_York")) }?:"-"

            holder.imgOpenMorning.setOnClickListener {
                if (result.morning!= null){
                    (context as ResultListActivity).activityResult.launch(
                        Intent(context, ResultActivity::class.java).putExtra(RESULT_EXTRA, result.morning))
                }
            }

            holder.imgOpenNight.setOnClickListener {
                if (result.night!= null){
                    (context as ResultListActivity).activityResult.launch(
                        Intent(context, ResultActivity::class.java).putExtra(RESULT_EXTRA, result.night))
                }
            }

            holder.txtLo1Night.text= result.night?.lo1?:"-"
            holder.txtLo2Night.text= result.night?.lo2?:"-"
            holder.txtLo3Night.text= result.night?.lo3?:"-"
            holder.txtDateNight.text= result.night?.let{ getDateString(result.night!!.resultDate!!, DateTimeZone.forID("America/New_York")) }?:"-"
        }
    }


    override fun getItemViewType(position: Int): Int {
        if (position== 0) return HEADER
        else return ITEM
    }

    override fun getItemCount(): Int {
        return results.size
    }

    class CustomViewHolder(var item: View) : RecyclerView.ViewHolder(item){
        val txtLo1Morning by lazy { item.txtLo1Morning }
        val txtLo2Morning by lazy { item.txtLo2Morning }
        val txtLo3Morning by lazy { item.txtLo3Morning }
        val txtDateMorning by lazy { item.txtDateMorning }
        val txtLo1Night by lazy { item.txtLo1Night }
        val txtLo2Night by lazy { item.txtLo2Night }
        val txtLo3Night by lazy { item.txtLo3Night }
        val txtDateNight by lazy { item.txtDateNight }
        val txtFirst by lazy { item.txtFirst }
        val txtSecond by lazy { item.txtSecond }
        val txtThird by lazy { item.txtThird }
        val imgEdit by lazy { item.imgEdit }
        val imgOpenMorning by lazy { item.imgOpenMorning }
        val imgOpenNight by lazy { item.imgOpenNight }

    }
}