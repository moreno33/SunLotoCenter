package com.sunlotocenter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sunlotocenter.activity.R
import kotlinx.android.synthetic.main.valid_error_layout.view.*

class ErrorAdapter (var errors: ArrayList<String>) : RecyclerView.Adapter<ErrorAdapter.CustomVH>() {

    private var ctx: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomVH {
        this.ctx = parent.context
        var view= LayoutInflater.from(ctx).inflate(R.layout.valid_error_layout, null)

        return CustomVH(view!!)
    }

    override fun onBindViewHolder(holder: CustomVH, position: Int) {
        val error = errors[holder.adapterPosition]
        if (error.isNotEmpty()) {
            holder.txtError.text= error
        }
    }

    override fun getItemCount(): Int {
        return errors.size
    }


    /**
     * This is the viewholder for every single row that I will
     * have on my recyclerview.
     */
    inner class CustomVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtError: TextView by lazy { itemView.txtError }
    }
}
