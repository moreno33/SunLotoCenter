package com.sunlotocenter.activity

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunlotocenter.BuildConfig
import com.sunlotocenter.MyApplication
import com.sunlotocenter.R
import com.sunlotocenter.adapter.VersionAdapter
import com.sunlotocenter.dao.*
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.listener.LoadMoreListener
import com.sunlotocenter.model.UserViewModel
import com.sunlotocenter.utils.*
import com.sunlotocenter.validator.Form
import com.sunlotocenter.validator.MinLengthValidator
import kotlinx.android.synthetic.main.activity_version.*
import kotlinx.android.synthetic.main.activity_version.toolbar
import org.modelmapper.ModelMapper

class VersionActivity :
    ProtectedActivity(),
    LoadMoreListener.OnLoadMoreListener {

    private var loadMoreListener: LoadMoreListener?= null
    private lateinit var versionAdapter: VersionAdapter
    private lateinit var userViewModel: UserViewModel
    private var isSaveState= false
    private var versionExtra: Version?= null
    private var version: Version?= null
    private var form= Form()

    override fun getLayoutId(): Int {
        return R.layout.activity_version
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)
        userViewModel= ViewModelProvider(this, SavedStateViewModelFactory(application, this))
            .get(UserViewModel::class.java)

        if(savedInstanceState!=  null){
            isSaveState= true
            userViewModel.loadSaveState()
        }
        setUpAdapter()
        userViewModel.loadVersions(true)

        swpLayout.isRefreshing= true
        swpLayout.setOnRefreshListener {
            userViewModel.loadVersions(true)
        }

        fillControl()
        prepareControl()
    }

    private fun fillControl() {
        edxVersionCode.text= BuildConfig.VERSION_CODE.toString()
        edxVersionName.text= BuildConfig.VERSION_NAME
        versionExtra= intent.extras?.getSerializable(VERSION_EXTRA) as Version?
        if (versionExtra!= null){
            versionExtra?.versionCode.let{edxVersionCode.text= it!!.toString()}
            versionExtra?.versionName.let{edxVersionName.text= it!!.toString()}
        }
    }

    private fun prepareControl() {

        //Add validator
        edxVersionCode.addValidator(MinLengthValidator(1, getString(R.string.min_length_validator_error, 1)))
        edxVersionName.addValidator(MinLengthValidator(1, getString(R.string.min_length_validator_error, 1)))

        form.addInput(edxVersionCode, edxVersionName)

        btnSubmit.setOnClickListener {
            submit()
        }
    }

    private fun submit() {
        if(form.isValid()){

            if(BuildConfig.VERSION_CODE != edxVersionCode.text.toInt() ||
                BuildConfig.VERSION_NAME != edxVersionName.text){
                showDialog(this,
                    getString(R.string.internet_error_title),
                    getString(R.string.incorrect_version, BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME),
                    getString(R.string.ok),
                    object : ClickListener {
                        override fun onClick(): Boolean {
                            userViewModel.loadVersions(true)
                            return false
                        }

                    }, false, DialogType.ERROR)
            }else{
                version= Version()
                if(versionExtra!= null){
                    ModelMapper().map(versionExtra, version)
                }
                version!!.author= MyApplication.getInstance().connectedUser!!
                version!!.versionCode= edxVersionCode.text.toInt()
                version!!.versionName= edxVersionName.text

                dialog.show()
                clearControls()
                userViewModel.saveVersion(version!!)
            }
        }
    }

    private fun clearControls() {
        edxVersionCode.text= ""
        edxVersionName.text= ""
    }


    private fun setUpAdapter() {
        rclVersion.setHasFixedSize(true)
        rclVersion.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        versionAdapter= VersionAdapter(
            if(isSaveState) userViewModel.versions else arrayListOf()
        )
        userViewModel.versions.clear()
        rclVersion.adapter= versionAdapter
        rclVersion.addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable(this, R.drawable.item_divider)))

        observe()
        setLoadMoreListener()
    }

    private fun observe() {
        userViewModel.lastAddedVersionsData.observe(this,
            { versions ->
                if(versions.isEmpty() && userViewModel.page== 0){
                    txtInfo.visibility= View.VISIBLE
                }else{
                    txtInfo.visibility= View.GONE
                }
                addVersions(versions)
                progressBar.progressiveStop()
                swpLayout.isRefreshing= false
            })

        userViewModel.versionData.observe(this,
            {
                versionListener(it)
            })
    }

    private fun versionListener(it: Response<Version>?) {
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
            if(it.success){
                showDialog(this,
                    getString(R.string.success_title),
                    getString(R.string.version_susccess_message),
                    getString(R.string.ok),
                    object : ClickListener {
                        override fun onClick(): Boolean {
                            userViewModel.loadVersions(true)
                            return false
                        }

                    }, false, DialogType.SUCCESS)
            }else{
                showDialog(this,
                    getString(R.string.internet_error_title),
                    it.message!!,
                    getString(R.string.ok),
                    object : ClickListener {
                        override fun onClick(): Boolean {
                            userViewModel.loadVersions(true)
                            return false
                        }

                    }, false, DialogType.ERROR)
            }

        }
    }

    private fun addVersions(versions: ArrayList<Version>) {
        loadMoreListener?.setFinished(false)
        if(versions.isEmpty()){
            loadMoreListener?.setFinished(true)
            loadMoreListener?.setLoaded()
            return
        }
        val isFirstPage= userViewModel.page== 0

        if(versions.size< LoadMoreListener.SIZE_PER_PAGE)
            loadMoreListener?.setFinished(true)
        val lastPosition= versionAdapter.versions.size
        if(isFirstPage)
            versionAdapter.versions.clear()

        versionAdapter.versions.addAll(versions)

        if(isFirstPage){
            versionAdapter.notifyDataSetChanged()
        } else{
            versionAdapter.notifyItemRangeInserted(lastPosition, versions.size)
        }
        loadMoreListener?.setLoaded()

    }

    private fun setLoadMoreListener() {
        loadMoreListener?.let { rclVersion.removeOnScrollListener(loadMoreListener!!) }
        loadMoreListener=  LoadMoreListener(rclVersion.layoutManager as LinearLayoutManager)
        loadMoreListener?.setOnLoadMoreListener(this)
        rclVersion.addOnScrollListener(loadMoreListener!!)
    }

    override fun onLoadMore() {
        userViewModel.loadVersions(false)
        progressBar.progressiveStart()
    }


}