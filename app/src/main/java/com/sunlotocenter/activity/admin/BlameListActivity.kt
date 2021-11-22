package com.sunlotocenter.activity.admin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunlotocenter.activity.ProtectedActivity
import com.sunlotocenter.activity.R
import com.sunlotocenter.adapter.BlameAdapter
import com.sunlotocenter.dao.Blame
import com.sunlotocenter.dao.User
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.listener.LoadMoreListener
import com.sunlotocenter.model.UserViewModel
import com.sunlotocenter.utils.DividerItemDecorator
import com.sunlotocenter.utils.REFRESH_REQUEST_CODE
import com.sunlotocenter.utils.USER_EXTRA
import kotlinx.android.synthetic.main.activity_blame_list.*
import kotlinx.android.synthetic.main.activity_blame_list.progressBar
import kotlinx.android.synthetic.main.activity_blame_list.swpLayout
import kotlinx.android.synthetic.main.activity_blame_list.toolbar
import kotlinx.android.synthetic.main.activity_blame_list.txtInfo
import kotlinx.android.synthetic.main.activity_employee_list.*

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
        btnAdd.setOnClickListener {
            startActivityForResult(Intent(this, BlameActivity::class.java).putExtra(USER_EXTRA, userExtra), REFRESH_REQUEST_CODE)
        }
        userViewModel.loadBlamesForUser(true, userExtra!!.sequence.id!!)

        swpLayout.isRefreshing= true
        swpLayout.setOnRefreshListener {
            userViewModel.loadBlamesForUser(true, userExtra!!.sequence.id!!)
        }
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
        userViewModel.loadBlamesForUser(false, userExtra!!.sequence.id!!)
        progressBar.progressiveStart()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== REFRESH_REQUEST_CODE && resultCode== Activity.RESULT_OK){
            userViewModel.loadBlamesForUser(true, userExtra!!.sequence.id!!)
            setResult(Activity.RESULT_OK, Intent())
        }
    }
}