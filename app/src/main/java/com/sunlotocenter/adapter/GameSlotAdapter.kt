package com.sunlotocenter.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sunlotocenter.activity.R
import com.sunlotocenter.dao.Borlet
import com.sunlotocenter.dao.Game
import kotlinx.android.synthetic.main.game_row_layout.view.*
import kotlinx.android.synthetic.main.game_row_layout.view.txtAmount
import kotlinx.android.synthetic.main.game_row_layout.view.txtGame
import kotlinx.android.synthetic.main.game_row_layout.view.txtOption
import kotlinx.android.synthetic.main.game_slot_footer_layout.view.*
import kotlinx.android.synthetic.main.game_slot_row_layout.view.*
import java.util.*

class GameSlotAdapter(gameSet: TreeSet<Game>) : RecyclerView.Adapter<GameSlotAdapter.CustomViewHolder>() {

    private val BORLET_HEADER: Int= 0
    private val MARRIAGE_HEADER: Int= 1
    private val LOTO3_HEADER: Int= 2
    private val LOTO4_HEADER: Int= 3
    private val LOTO5_HEADER: Int= 4
    private val ITEM: Int= 5
    private val FOOTER= 6

    lateinit var context: Context

    var games= TreeSet<Game>()

    init{
        gameSet.forEach {
            addGame(it)
        }
        var totalAmount= 0.0
        var totalLost= 0.0
        games.forEach {
            if(it.type== 1){
                totalAmount+= it.amount
                totalLost+= it.amountWin
            }
        }
        games.add(Borlet(number = "", amount = totalAmount, amountWin = totalLost, option = "", position = 4, type = 4))
    }

    fun addGame(game: Game) {
        if(games.isEmpty()){
            addHeaderAndGame(game, games)
        }else{
            if(isHeaderExistForGame(game, games)){
                if(!games.add(game)){
                    games.remove(game)
                    games.add(game)
                }
            }else{
                addHeaderAndGame(game, games)
            }
        }

        notifyDataSetChanged()
    }

    private fun addHeaderAndGame(game: Game, gameSet: TreeSet<Game>) {
        var gameCopy= game.clone() as Game
        gameCopy.number= ""
        //Header
        gameCopy.type= 0
        gameSet.add(gameCopy)
        if(!gameSet.add(game)){
            gameSet.remove(game)
            gameSet.add(game)
        }
    }

    private fun isHeaderExistForGame(game: Game, gameSet: TreeSet<Game>): Boolean {
        gameSet.forEach {
            if (it.position == game.position) return true
        }
        return false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        context= parent.context
        if(viewType== ITEM)
            return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.game_slot_row_layout, null))
        else if(viewType== FOOTER)
            return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.game_slot_footer_layout, null))
        else
            return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.game_slot_header_layout, null))
    }

    override fun getItemViewType(position: Int): Int {

        var game= games.elementAt(position)
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
        else if(game.position== 4){
            return FOOTER
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
                var game= games.elementAt(position)
                holder.txtGame.text= game.number
                holder.txtOption.text= game.opt
                holder.txtAmount.text= String.format("%.0f", game.amount)
                holder.txtLost.text= String.format("%.0f", game.amountWin)
                if(game.amountWin > 0){
                    holder.txtGame.setTextColor(ContextCompat.getColor(context, R.color.main_red))
                    holder.txtOption.setTextColor(ContextCompat.getColor(context, R.color.main_red))
                    holder.txtAmount.setTextColor(ContextCompat.getColor(context, R.color.main_red))
                    holder.txtLost.setTextColor(ContextCompat.getColor(context, R.color.main_red))
                }else{
                    holder.txtGame.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    holder.txtOption.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    holder.txtAmount.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    holder.txtLost.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
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
            FOOTER->{
                var game= games.elementAt(position)
                val result= game.amount- game.amountWin

                holder.txtTotal.text= context.getString(R.string.total_amount, game.amount, game.amountWin, result)
                if(result>= 0){
                    holder.txtTotal.setTextColor(ContextCompat.getColor(context, R.color.main_green))
                }else{
                    holder.txtTotal.setTextColor(ContextCompat.getColor(context, R.color.main_red))
                }
            }
        }

    }



    override fun getItemCount(): Int {
        return games.size
    }

    class CustomViewHolder(var item: View) : RecyclerView.ViewHolder(item){
        val txtGame by lazy { item.txtGame }
        val txtOption by lazy { item.txtOption }
        val txtAmount by lazy { item.txtAmount }
        val txtLost by lazy { item.txtLost }
        val txtTotal by lazy { item.txtTotal }
    }
}