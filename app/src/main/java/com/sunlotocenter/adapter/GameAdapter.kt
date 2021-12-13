package com.sunlotocenter.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.sunlotocenter.activity.ChangePasswordActivity
import com.sunlotocenter.R
import com.sunlotocenter.activity.admin.AdminPersonalInfoActivity
import com.sunlotocenter.dao.Game
import com.sunlotocenter.dao.User
import com.sunlotocenter.utils.USER_EXTRA
import kotlinx.android.synthetic.main.game_row_layout.view.*
import java.util.*

class GameAdapter(var gameSet: TreeSet<Game>, var onGameRemoveListener: OnGameRemoveListener) : RecyclerView.Adapter<GameAdapter.CustomViewHolder>() {

    private val BORLET_HEADER: Int= 0
    private val MARRIAGE_HEADER: Int= 1
    private val LOTO3_HEADER: Int= 2
    private val LOTO4_HEADER: Int= 3
    private val LOTO5_HEADER: Int= 4
    private val ITEM: Int= 5

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        context= parent.context
        if(viewType== ITEM)
            return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.game_row_layout, null))
        else
            return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.game_header_layout, null))
    }

    override fun getItemViewType(position: Int): Int {

        var game= gameSet.elementAt(position)
        if(game.position== 0) {
            if(game.type== 0)
                return BORLET_HEADER
            else return ITEM
        }
        else if(game.position== 1) {
            if(game.type== 0)
                return MARRIAGE_HEADER
            else return ITEM
        }
        else if(game.position== 2) {
            if(game.type== 0)
                return LOTO3_HEADER
            else return ITEM
        }
        else if(game.position== 3) {
            if(game.type== 0)
                return LOTO4_HEADER
            else return ITEM
        }
        else {
            if(game.type== 0)
                return LOTO5_HEADER
            else return ITEM
        }

    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        var type= getItemViewType(position)
        when(type){
            ITEM->{
                var game= gameSet.elementAt(position)
                holder.txtGame.text= game.number
                holder.txtOption.text= game.opt
                holder.txtAmount.text= context.getString(R.string.price_currency, game.amount)
                //Click to open menu
                holder.imgAction.setOnClickListener {
                    deleteGame(game, position)
                }
            }
            BORLET_HEADER->{
                holder.txtGame.text= context.getString(R.string.borlet)
            }
            MARRIAGE_HEADER->{
                holder.txtGame.text= context.getString(R.string.marriage)
            }
            LOTO3_HEADER->{
                holder.txtGame.text= context.getString(R.string.loto3)
            }
            LOTO4_HEADER->{
                holder.txtGame.text= context.getString(R.string.loto4)
            }
            LOTO5_HEADER->{
                holder.txtGame.text= context.getString(R.string.loto5)
            }
        }

    }

    private fun deleteGame(game: Game, position: Int) {
        var count= 0
        gameSet.forEach { if(it.javaClass== game.javaClass) count++ }

        if(count== 2){
            var gameIterator= gameSet.iterator()
            while (gameIterator.hasNext()){
                var next= gameIterator.next()
                if(next.javaClass== game.javaClass){
                    gameIterator.remove()
                    notifyDataSetChanged()
                }
            }

        }else if(count> 2){
            var gameIterator= gameSet.iterator()
            while (gameIterator.hasNext()){
                var next= gameIterator.next()
                if(next== game){
                    gameIterator.remove()
                    notifyDataSetChanged()
                }
            }
        }
        onGameRemoveListener.onRemove(gameSet, game)

    }

    override fun getItemCount(): Int {
        return gameSet.size
    }

    class CustomViewHolder(var item: View) : RecyclerView.ViewHolder(item){
        val txtGame by lazy { item.txtGame }
        val txtOption by lazy { item.txtOption }
        val txtAmount by lazy { item.txtAmount }
        val imgAction by lazy { item.imgAction }
    }

    interface OnGameRemoveListener{
        fun onRemove(games:Set<Game>, game: Game)
    }
}