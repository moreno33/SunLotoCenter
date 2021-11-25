package com.sunlotocenter.activity.admin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunlotocenter.activity.ProtectedActivity
import com.sunlotocenter.activity.R
import com.sunlotocenter.adapter.BankListAdapter
import com.sunlotocenter.dao.Bank
import com.sunlotocenter.dao.Response
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.listener.LoadMoreListener
import com.sunlotocenter.listener.SaveBankListener
import com.sunlotocenter.model.BankViewModel
import com.sunlotocenter.utils.ClickListener
import com.sunlotocenter.utils.DialogType
import com.sunlotocenter.utils.REFRESH_REQUEST_CODE
import kotlinx.android.synthetic.main.activity_bank_list.*
import kotlinx.android.synthetic.main.activity_bank_list.toolbar
import kotlinx.android.synthetic.main.activity_prevent_trouble.*

class BankListActivity : ProtectedActivity(),
    LoadMoreListener.OnLoadMoreListener,
    SaveBankListener{

    private var loadMoreListener: LoadMoreListener?= null
    private lateinit var bankListAdapter: BankListAdapter
    private lateinit var bankViewModel: BankViewModel
    private var isSaveState= false
    private var total= 0L

    override fun getLayoutId(): Int {
        return R.layout.activity_bank_list
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)
        bankViewModel= ViewModelProvider(this, SavedStateViewModelFactory(application, this))
            .get(BankViewModel::class.java)

        if(savedInstanceState!=  null){
            isSaveState= true
            bankViewModel.loadSaveState()
        }

        setUpAdapter()

        bankViewModel.loadBanks(true)
        swpLayout.isRefreshing= true
        swpLayout.setOnRefreshListener {
            bankViewModel.loadBanks(true)
        }

        txtTotal.text= getString(R.string.loading)

        imgFilter.setOnClickListener {
//            showFilter()
        }
    }

    private fun setUpAdapter() {
        rclBanks.setHasFixedSize(true)
        rclBanks.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        bankListAdapter= BankListAdapter(
            if(isSaveState) bankViewModel.banks else arrayListOf(),
            this
        )
        bankViewModel.banks.clear()

        rclBanks.adapter= bankListAdapter

        registerForContextMenu(rclBanks)


        observe()
        setLoadMoreListener()

        fltAdd.setOnClickListener {
            startActivityForResult(Intent(this, CreateBankActivity::class.java), REFRESH_REQUEST_CODE)
        }
    }

    private fun observe() {
        bankViewModel.lastAddedBanksData.observe(this,
            { banks ->
                if(banks.isEmpty() && bankViewModel.page== 0){
                    txtInfo.visibility= View.VISIBLE
                }else{
                    txtInfo.visibility= View.GONE
                }
                addBanks(banks)
                progressBar.progressiveStop()
                swpLayout.isRefreshing= false
            })
        bankViewModel.bankTotalData.observe(this, {
                t->
            if(total!= t || t.toInt()== 0){
                total= t
                txtTotal.text= getString(R.string.employee_total, total)
            }
        })

        bankViewModel.bankResponseData.observe(this,
            {
                saveListener(it)
            })
    }

    private fun saveListener(it: Response<Bank>?) {
        dialog.dismiss()
        if(it== null){
            com.sunlotocenter.utils.showDialog(this,
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
            if(it.success){
                com.sunlotocenter.utils.showDialog(this,
                    getString(R.string.success_title),
                    getString(R.string.bank_updated_success_message),
                    getString(R.string.ok),
                    object : ClickListener {
                        override fun onClick(): Boolean {
                            return false
                        }
                    }, false, DialogType.SUCCESS)
                bankViewModel.loadBanks(true)
            }else{
                com.sunlotocenter.utils.showDialog(this,
                    getString(R.string.internet_error_title),
                    it.message!!,
                    getString(R.string.ok),
                    object : ClickListener {
                        override fun onClick(): Boolean {
                            return false
                        }
                    }, false, DialogType.ERROR)
            }

        }
    }

    private fun addBanks(banks: ArrayList<Bank>) {
        loadMoreListener?.setFinished(false)
        if(banks.isEmpty()){
            loadMoreListener?.setFinished(true)
            loadMoreListener?.setLoaded()
            return
        }
        val isFirstPage= bankViewModel.page== 0
        if(banks.size< 10)
            loadMoreListener?.setFinished(true)
        val lastPosition= bankListAdapter.banks.size
        if(isFirstPage)
            bankListAdapter.banks.clear()

        bankListAdapter.banks.addAll(banks)

        if(isFirstPage){
            bankListAdapter.notifyDataSetChanged()
        } else{
            bankListAdapter.notifyItemRangeInserted(lastPosition, banks.size)
        }
        loadMoreListener?.setLoaded()

    }

    private fun setLoadMoreListener() {
        loadMoreListener?.let { rclblock.removeOnScrollListener(loadMoreListener!!) }
        loadMoreListener=  LoadMoreListener(rclBanks.layoutManager as LinearLayoutManager)
        loadMoreListener?.setOnLoadMoreListener(this)
        rclBanks.addOnScrollListener(loadMoreListener!!)
    }

    override fun onLoadMore() {
        bankViewModel.loadBanks(false)
        progressBar.progressiveStart()
    }

    override fun save(bank: Bank) {
        dialog.show()
        bankViewModel.save(bank)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== REFRESH_REQUEST_CODE && resultCode== Activity.RESULT_OK){
            bankViewModel.loadBanks(true)
        }
    }
}