package com.sunlotocenter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sunlotocenter.R
import com.sunlotocenter.dao.*
import com.sunlotocenter.utils.*
import kotlinx.android.synthetic.main.blame_layout.view.*
import kotlinx.android.synthetic.main.blame_layout.view.txtAuthor
import kotlinx.android.synthetic.main.blame_layout.view.txtTime
import kotlinx.android.synthetic.main.version_layout.view.*

class VersionAdapter(var versions: ArrayList<Version>) : RecyclerView.Adapter<VersionAdapter.CustomViewHolder>() {

    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        context= parent.context
        return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.version_layout, null))
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        var version= versions[position]
        holder.txtAuthor.text= "${version.author?.firstName} ${version.author?.lastName}"
        holder.txtVersion.text= "${version.versionName}(${version.versionCode})"
        holder.txtWhen.text= getDateString(version.createdDateTime!!)
        holder.imgCheck.visibility= if (version.selected) View.VISIBLE else View.GONE
    }


    override fun getItemCount(): Int {
        return versions.size
    }

    class CustomViewHolder(var item: View) : RecyclerView.ViewHolder(item){
        val txtAuthor by lazy { item.txtAuthor }
        val txtVersion by lazy { item.txtVersion }
        val txtWhen by lazy { item.txtTime }
        val imgCheck by lazy { item.imgCheck }
    }
}