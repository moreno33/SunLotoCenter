package com.sunlotocenter.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sunlotocenter.R
import com.sunlotocenter.dao.Game
import kotlinx.android.synthetic.main.compose_row_layout.view.edxAmount
import kotlinx.android.synthetic.main.compose_row_layout.view.edxNumber

class AutComposeAdapter (var dataSet: Set<Game>) : RecyclerView.Adapter<AutComposeAdapter.CustomVH>() {

    private var ctx: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomVH {
        this.ctx = parent.context
        var view= LayoutInflater.from(ctx).inflate(R.layout.compose_row_layout, null)
        return CustomVH(view!!)
    }

    override fun onBindViewHolder(holder: CustomVH, position: Int) {
        holder.setIsRecyclable(false)
        val marry = dataSet.elementAt(holder.adapterPosition)
        holder.edxNumber.text= marry.number
        holder.edxAmount.text= marry.amount.toString()

        holder.edxAmount.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s== null || s.isEmpty()) marry.amount= 0.0
                else marry.amount= s.toString().toDouble()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }


    /**
     * This is the viewholder for every single row that I will
     * have on my recyclerview.
     */
    inner class CustomVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val edxNumber by lazy { itemView.edxNumber }
        val edxAmount by lazy { itemView.edxAmount }
    }
}
