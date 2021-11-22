package com.sunlotocenter.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sunlotocenter.activity.fragment.*
import com.sunlotocenter.listener.AddGameListener

class TabsPagerAdapter(activity: AppCompatActivity, val itemsCount: Int, var addGameListener: AddGameListener) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return itemsCount
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BorletFragment(addGameListener)
            1 -> MarriageFragment(addGameListener)
            2 -> Loto3Fragment(addGameListener)
            3 -> Loto4Fragment(addGameListener)
            4 -> Loto5Fragment(addGameListener)
            else -> BorletFragment(addGameListener)
        }
    }
}