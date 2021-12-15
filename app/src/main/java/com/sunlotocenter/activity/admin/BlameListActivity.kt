package com.sunlotocenter.activity.admin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunlotocenter.MyApplication
import com.sunlotocenter.activity.ProtectedActivity
import com.sunlotocenter.R
import com.sunlotocenter.adapter.BlameAdapter
import com.sunlotocenter.dao.Blame
import com.sunlotocenter.dao.Response
import com.sunlotocenter.dao.User
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.listener.LoadMoreListener
import com.sunlotocenter.model.UserViewModel
import com.sunlotocenter.utils.*
import kotlinx.android.synthetic.main.activity_blame.*
import kotlinx.android.synthetic.main.activity_blame_list.*
import kotlinx.android.synthetic.main.activity_blame_list.edxMessage
import kotlinx.android.synthetic.main.activity_blame_list.progressBar
import kotlinx.android.synthetic.main.activity_blame_list.swpLayout
import kotlinx.android.synthetic.main.activity_blame_list.toolbar
import kotlinx.android.synthetic.main.activity_blame_list.txtInfo
import kotlinx.android.synthetic.main.activity_employee_list.*
import kotlinx.android.synthetic.main.blame_layout.*

class BlameListActivity :
    ProtectedActivity(),
    LoadMoreListener.OnLoadMoreListener {

    private var loadMoreListener: LoadMoreListener?= null
    private lateinit var blameAdpter: BlameAdapter
    private lateinit var userViewModel: UserViewModel
    private var isSaveState= false
    private var userExtra: User? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_blame_list
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)
        userViewModel= ViewModelProvider(this, SavedStateViewModelFactory(application, this))
            .get(UserViewModel::class.java)

        userExtra= intent.extras?.getSerializable(USER_EXTRA) as User?

        supportActionBar?.title= "${userExtra?.firstName} ${userExtra?.lastName}"

        if(savedInstanceState!=  null){
            isSaveState= true
            userViewModel.loadSaveState()
        }
        setUpAdapter()
        userViewModel.loadBlamesForUser(true, userExtra!!.sequence!!.id!!)

        swpLayout.isRefreshing= true
        swpLayout.setOnRefreshListener {
            userViewModel.loadBlamesForUser(true, userExtra!!.sequence!!.id!!)
        }

        imgSubmit.setOnClickListener {
            if(edxMessage.text.trim().isNotEmpty()){
                submit(edxMessage.text.toString())
            }
        }
    }

    private fun submit(text: String) {
        dialog.show()
        edxMessage.text.clear()
        userViewModel.addBlame(Blame(null, userExtra!!, MyApplication.getInstance().connectedUser, text))
    }

    private fun setUpAdapter() {
        rclBlame.setHasFixedSize(true)
        rclBlame.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        blameAdpter= BlameAdapter(
            if(isSaveState) userViewModel.blames else arrayListOf()
        )
        userViewModel.blames.clear()

        rclBlame.adapter= blameAdpter
        rclBlame.addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable(this, R.drawable.item_divider)))

        observe()
        setLoadMoreListener()
    }

    private fun observe() {
        userViewModel.lastAddedBlamesData.observe(this,
            { blames ->
                if(blames.isEmpty() && userViewModel.page== 0){
                    txtInfo.visibility= View.VISIBLE
                }else{
                    txtInfo.visibility= View.GONE
                }
                addBlames(blames)
                progressBar.progressiveStop()
                swpLayout.isRefreshing= false
            })

        userViewModel.blameData.observe(this,
            {
                blameListener(it)
            })
    }

    private fun blameListener(it: Response<Blame>?) {
        dialog.dismiss()
        if(it== null){
            showDialog(this,
                getString(R.string.internet_error_title),
                getString(
                    R.string.internet_error_message
                ),
                getString(R.string.ok),
                object : ClickListener {
                    override fun onClick(): Boolean {
                        return false
                    }

                }, true, DialogType.ERROR)
        }else{
            showDialog(this,
                getString(R.string.success_title),
                getString(R.string.blame_susccess_message),
                getString(R.string.ok),
                object : ClickListener {
                    override fun onClick(): Boolean {
                        userViewModel.loadBlamesForUser(true, userExtra!!.sequence!!.id!!)
                        return false
                    }

                }, false, DialogType.SUCCESS)
        }
    }

    private fun addBlames(blames: ArrayList<Blame>) {
        loadMoreListener?.setFinished(false)
        if(blames.isEmpty()){
            loadMoreListener?.setFinished(true)
            loadMoreListener?.setLoaded()
            return
        }
        val isFirstPage= userViewModel.page== 0
        if(blames.size< LoadMoreListener.SIZE_PER_PAGE)
            loadMoreListener?.setFinished(true)
        val lastPosition= blameAdpter.blames.size
        if(isFirstPage)
            blameAdpter.blames.clear()

        blameAdpter.blames.addAll(blames)

        if(isFirstPage){
            blameAdpter.notifyDataSetChanged()
        } else{
            blameAdpter.notifyItemRangeInserted(lastPosition, blames.size)
        }
        loadMoreListener?.setLoaded()

    }

    private fun setLoadMoreListener() {
        loadMoreListener?.let { rclBlame.removeOnScrollListener(loadMoreListener!!) }
        loadMoreListener=  LoadMoreListener(rclBlame.layoutManager as LinearLayoutManager)
        loadMoreListener?.setOnLoadMoreListener(this)
        rclBlame.addOnScrollListener(loadMoreListener!!)
    }

    override fun onLoadMore() {
        userViewModel.loadBlamesForUser(false, userExtra!!.sequence!!.id!!)
        progressBar.progressiveStart()
    }

}