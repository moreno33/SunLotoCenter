package com.sunlotocenter.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunlotocenter.adapter.AutComposeAdapter
import com.sunlotocenter.dao.Game
import com.sunlotocenter.extensions.enableHome
import kotlinx.android.synthetic.main.activity_auto_compose.*
import kotlinx.android.synthetic.main.activity_auto_compose.rclMarryAll

import android.content.Intent
import com.sunlotocenter.R
import java.util.*


class AutoComposeActivity :  ProtectedActivity() {

    private lateinit var autComposeAdapter: AutComposeAdapter

    override fun getLayoutId(): Int {
        return R.layout.activity_auto_compose
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)
        val gamesExtra= intent.getSerializableExtra(GAMES_EXTRA) as Set<Game>
        val title= intent.getStringExtra(TITLE_EXTRA)
        supportActionBar?.title= title

        setupAdapter(gamesExtra)

        //Control all amount
        edxMarryAll.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if(s!= null && s.isNotEmpty()){
                    gamesExtra.forEach {
                        it.amount= s.toString().toDouble()
                    }
                    autComposeAdapter.notifyDataSetChanged()
                }
            }

        })

        //Manage button
        manageButtons()
    }

    private fun manageButtons() {
        btnAdd.setOnClickListener {
            val returnIntent = Intent()
            returnIntent.putExtra(GAMES_EXTRA, autComposeAdapter.dataSet as TreeSet)
            setResult(RESULT_OK, returnIntent)
            finish()
        }
        btnCancel.setOnClickListener {
            val returnIntent = Intent()
            setResult(RESULT_CANCELED, returnIntent)
            finish()
        }
    }

    private fun setupAdapter(gamesExtra: Set<Game>) {
        rclMarryAll.setHasFixedSize(true)
        rclMarryAll.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        autComposeAdapter= AutComposeAdapter(gamesExtra)
        rclMarryAll.adapter= autComposeAdapter
    }

    companion object{
        val GAMES_EXTRA= "com.sunlotocenter.activity.MARRY_ALL_GAMES_EXTRA"
        val REQUEST_CODE= 100
        val TITLE_EXTRA= "com.sunlotocenter.activity.TITLE"
    }
}