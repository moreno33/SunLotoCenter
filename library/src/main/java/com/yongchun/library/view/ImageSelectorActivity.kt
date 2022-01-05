package com.yongchun.library.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.yongchun.library.adapter.ImageListAdapter
import com.yongchun.library.model.PickerViewModel
import com.yongchun.library.model.AlbumItem
import com.yongchun.library.model.ImageItem
import com.yongchun.library.adapter.ImageListAdapter.OnImageSelectChangedListener
import androidx.core.content.FileProvider
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yongchun.library.R
import com.yongchun.library.adapter.LoadMoreListener
import com.yongchun.library.utils.*
import com.yongchun.library.utils.createTempImageFile
import kotlinx.android.synthetic.main.activity_imageselector.*
import kotlinx.android.synthetic.main.error_layout.*
import java.lang.Exception
/**
 * Created by dee on 15/11/19.
 */
class ImageSelectorActivity : AppCompatActivity(), LoadMoreListener.OnLoadMoreListener {
    private var isPermissionGranted = false
    private var isSaveState = false
    private val path: String? = null
    private var maxSelectNum = 9
    private var selectMode = MODE_MULTIPLE
    private var showCamera = true
    private var enablePreview = true
    private var enableCrop = false
    private var forceCamera = false
    private val spanCount = 3
    private var imageAdapter: ImageListAdapter? = null
    private var folderWindow: FolderWindow? = null
    private var cameraPath: String? = null
    private var mainViewModel: PickerViewModel? = null
    private var loadMoreListener: LoadMoreListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imageselector)
        mainViewModel = ViewModelProvider(
            this, SavedStateViewModelFactory(
                application, this
            )
        ).get(
            PickerViewModel::class.java
        )

        maxSelectNum = intent.getIntExtra(EXTRA_MAX_SELECT_NUM, 9)
        selectMode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_MULTIPLE)
        showCamera = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true)
        enablePreview = intent.getBooleanExtra(EXTRA_ENABLE_PREVIEW, true)
        enableCrop = intent.getBooleanExtra(EXTRA_ENABLE_CROP, false)
        if (selectMode == MODE_MULTIPLE) {
            enableCrop = false
        } else {
            enablePreview = false
        }
        if (savedInstanceState != null) {
            cameraPath = savedInstanceState.getString(BUNDLE_CAMERA_PATH)
        }
        initView()
        registerListener()
        mainViewModel!!.init(contentResolver)
        if (savedInstanceState != null) {
            isSaveState = true
            mainViewModel!!.loadSaveState()
        } else {
            mainViewModel!!.bindArguments(intent.extras)
        }
        checkStoragePermission()
    }

    override fun onResume() {
        super.onResume()
        checkStoragePermission()
    }

    fun initView() {
        folderWindow = FolderWindow(this)
        toolbar.setTitle(R.string.picture)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.mipmap.ic_back)
        done_text.visibility = if (selectMode == MODE_MULTIPLE) View.VISIBLE else View.GONE
        preview_text.setVisibility(if (enablePreview) View.VISIBLE else View.GONE)

        setImagesAdapter()
    }

    private fun setImagesAdapter() {
        folder_list.setHasFixedSize(true)
        folder_list.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount,
                ScreenUtils.dip2px(this, 2f),
                false
            )
        )
        folder_list.setLayoutManager(GridLayoutManager(this, spanCount))
        imageAdapter = ImageListAdapter(this, maxSelectNum, selectMode, showCamera, enablePreview)
        imageAdapter!!.bindImages(if (isSaveState && !mainViewModel!!.saveStateImages.isEmpty()) mainViewModel!!.saveStateImages else mainViewModel!!.dumpImagesList)
        mainViewModel!!.saveStateImages.clear()
        folder_list.adapter = imageAdapter
        observe()
        setLoadMoreListener()
    }

    private fun observe() {
        mainViewModel?.mDirectCamera?.observe(this, { aBoolean: Boolean -> forceCamera = aBoolean })
        mainViewModel
        mainViewModel?.mAlbums?.observe(
            this, {
                folderWindow?.bindFolder(it, mainViewModel)
            })
        mainViewModel!!.mLastAddedImages.observe(
            this,
            { imageItems: ArrayList<ImageItem> -> addImages(imageItems) })
        mainViewModel!!.mNotifyPosition.observe(this, { integer: Int? ->
            imageAdapter!!.notifyItemChanged(
                integer!!
            )
        })
        mainViewModel!!.mNotifyInsert.observe(this, { item: ImageItem? ->
            imageAdapter!!.selectedImages.add(0, item)
            imageAdapter!!.onImageSelectChangedListener.onChange(imageAdapter!!.selectedImages)
            imageAdapter!!.notifyDataSetChanged()
        })
        mainViewModel!!.mDoneEnabled.observe(this, { aBoolean: Boolean? -> })
        mainViewModel!!.showOverLimit.observe(this, { aBoolean: Boolean? -> showLimitMsg() })
    }

    private fun addImages(it: ArrayList<ImageItem>) {
        loadMoreListener!!.setFinished(false)
        if (it.isEmpty()) {
            loadMoreListener!!.setFinished(true)
            loadMoreListener!!.setLoaded()
            return
        }
        val isFirstPage = mainViewModel!!.mPage == 0
        isPermissionGranted = true
        if (it.size < PAGE_SIZE) {
            loadMoreListener!!.setFinished(true)
        }
        val lastPos = imageAdapter!!.images.size
        if (isFirstPage) {
            imageAdapter!!.bindImages(it)
            imageAdapter!!.notifyDataSetChanged()
        } else {
            imageAdapter!!.images.addAll(it)
            imageAdapter!!.notifyItemRangeInserted(lastPos, it.size)
        }
        loadMoreListener!!.setLoaded()
        if (forceCamera) {
            checkCameraPermission()
            forceCamera = false
        }
    }

    fun registerListener() {
        toolbar!!.setNavigationOnClickListener { v: View? -> finish() }
        folder_layout!!.setOnClickListener { v: View? ->
            if (folderWindow!!.isShowing) {
                folderWindow!!.dismiss()
            } else {
                folderWindow!!.showAsDropDown(toolbar)
            }
        }
        imageAdapter!!.onImageSelectChangedListener = object : OnImageSelectChangedListener {
            override fun onChange(selectImages: ArrayList<ImageItem>) {
                val enable = selectImages.size != 0
                done_text!!.isEnabled = if (enable) true else false
                preview_text.isEnabled = if (enable) true else false
                if (enable) {
                    done_text!!.text = getString(R.string.done_num, selectImages.size, maxSelectNum)
                    preview_text.text = getString(R.string.preview_num, selectImages.size)
                } else {
                    done_text.setText(R.string.done)
                    preview_text.setText(R.string.preview)
                }
            }

            override fun onTakePhoto() {
                checkCameraPermission()
            }

            override fun onPictureClick(media: ImageItem, position: Int) {
                if (enablePreview) {
                    startPreview(imageAdapter!!.images, position)
                } else if (enableCrop) {
                    startCrop(media.imagePath)
                } else {
                    onSelectDone(media.imagePath)
                }
            }
        }
        done_text.setOnClickListener { v: View? ->
            onSelectDone(
                imageAdapter!!.selectedImages
            )
        }
        folderWindow!!.setOnItemClickListener { item: AlbumItem, pos: Int ->
            folderWindow!!.dismiss()
            folder_name!!.text = item.name
            mainViewModel!!.onAlbumChanged(item, pos)
            imageAdapter!!.images.clear()
            imageAdapter!!.notifyDataSetChanged()
        }
        preview_text.setOnClickListener { v: View? ->
            startPreview(
                imageAdapter!!.selectedImages, 0
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {

            // on take photo success
            if (requestCode == REQUEST_CAMERA) {
                mainViewModel!!.addCameraItem(imageAdapter!!.images)
                //                setDoneVisibilty(true)
//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(cameraPath))));
//                if (enableCrop) {
//                    startCrop(cameraPath);
//                } else {
//                    onSelectDone(cameraPath);
//                }
            } else if (requestCode == ImagePreviewActivity.REQUEST_PREVIEW) {
                val isDone = data!!.getBooleanExtra(ImagePreviewActivity.OUTPUT_ISDONE, false)
                val images =
                    data.getSerializableExtra(ImagePreviewActivity.OUTPUT_LIST) as ArrayList<ImageItem>?
                if (isDone) {
                    onSelectDone(images)
                } else {
                    imageAdapter!!.bindSelectImages(images)
                }
            } else if (requestCode == ImageCropActivity.REQUEST_CROP) {
                val path = data!!.getStringExtra(ImageCropActivity.OUTPUT_PATH)
                onSelectDone(path)
            }
        }
    }

    /**
     * start to camera、preview、crop
     */
    fun startCamera() {
        checkCameraPermission()
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            val cameraFile = FileUtils.createCameraFile(this)
            cameraPath = cameraFile.absolutePath
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile))
            startActivityForResult(cameraIntent, REQUEST_CAMERA)
        }
    }

    fun startPreview(previewImages: List<ImageItem?>?, position: Int) {
        ImagePreviewActivity.startPreview(
            this,
            previewImages,
            imageAdapter!!.selectedImages,
            maxSelectNum,
            position
        )
    }

    fun startCrop(path: String?) {
        ImageCropActivity.startCrop(this, path)
    }

    private fun showLimitMsg() {
//        Snackbar.make(rootView, R.string.over_limit_msg, Snackbar.LENGTH_LONG).show();
    }

    /**
     * on select done
     *
     * @param medias
     */
    fun onSelectDone(medias: List<ImageItem>?) {
        val images = ArrayList<String?>()
        for ((imagePath) in medias!!) {
            images.add(imagePath)
        }
        onResult(images)
    }

    fun onSelectDone(path: String?) {
        val images = ArrayList<String?>()
        images.add(path)
        onResult(images)
    }

    fun onResult(images: ArrayList<String?>?) {
        setResult(RESULT_OK, Intent().putStringArrayListExtra(REQUEST_OUTPUT, images))
        finish()
    }

    private fun checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission(Manifest.permission.CAMERA)) {
                cameraPermissionGranted()
            } else {
                requestPermission(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
            }
        } else {
            cameraPermissionGranted()
        }
    }

    private fun hideAlert() {
        alert!!.visibility = View.GONE
    }

    //
    //    private lateinit var mainViewModel: PickerViewModel
    //    private var mAlbumAdapter: AlbumsAdapter? = null
    //    private var mImagesAdapter: ImagesAdapter? = null
    //    private var loadMoreListener: LoadMoreListener? = null
    //
    //    private var isSaveState = false
    //    private var forceCamera = false
    //
    //    private View alert;
    //    private lateinit var albumsSpinner: AppCompatSpinner
    //    private lateinit var rvImages: RecyclerView
    //    private lateinit var changeAlbum: View
    //    private lateinit var rootView: View
    //
    //
    //    override fun onCreate(savedInstanceState: Bundle?) {
    //        super.onCreate(savedInstanceState)
    //        setContentView(R.layout.activity_image_picker_gligar)
    //
    //
    //        albumsSpinner = findViewById(R.id._albums_spinner)
    //        rvImages = findViewById(R.id._rv_images)
    //        changeAlbum = findViewById(R.id._change_album)
    //        rootView = findViewById(R.id._v_rootView)
    //
    //
    //        mainViewModel = ViewModelProvider(this, SavedStateViewModelFactory(application, this)).get(
    //                PickerViewModel::class.java
    //        )
    //        mainViewModel.init(contentResolver)
    //        if (savedInstanceState != null) {
    //            isSaveState = true
    //            mainViewModel.loadSaveState()
    //        } else {
    //            mainViewModel.bindArguments(intent.extras)
    //
    //        }
    //        setImagesAdapter()
    //        icDone.setOnClickListener { sendResults() }
    //    }
    //
    //    private fun openCamera() = checkCameraPermission()
    private fun loadAlbums() {
        isPermissionGranted = true
        mainViewModel!!.loadAlbums()
    }

    private fun storagePermissionGranted() {
        hideAlert()
        imageAdapter?.setShowCamera(true)
        folder_layout.visibility = View.VISIBLE
        preview_text.visibility = View.VISIBLE
        loadAlbums()
    }

    private fun cameraPermissionGranted() {
        //hideAlert();
//        try {
//            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
//                File cameraFile = FileUtils.createCameraFile(this);
//                cameraPath = cameraFile.getAbsolutePath();
//                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
//                startActivityForResult(cameraIntent, REQUEST_CAMERA);
//            }
//        } catch (Exception e) {}


//        hideAlert()
        try {
            val photoFile = createTempImageFile(this)
            mainViewModel!!.mCurrentPhotoPath = photoFile.absolutePath
            cameraPath = photoFile.absolutePath
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val myPhotoFileUri = FileProvider.getUriForFile(
                this,
                this.applicationContext.packageName + ".provider",
                photoFile
            )
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, myPhotoFileUri)
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(
                Intent.createChooser(cameraIntent, ""),
                REQUEST_CAMERA
            )
        } catch (e: Exception) {
            Log.e("Picker", e.message, e)
        }
    }

    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                storagePermissionGranted()
            } else {
                requestPermission(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_REQUEST_CODE
                )
            }
        } else {
            storagePermissionGranted()
        }
    }

    private fun checkPermission(permission: String): Boolean {
        val result = ContextCompat.checkSelfPermission(this, permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(permissions: Array<String>, requestCode: Int) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
            imageAdapter!!.images.clear()
            showAlert()
        } else {
            ActivityCompat.requestPermissions(this, permissions, requestCode)
        }
    }

    private fun showAlert() {
        alert_btn!!.setOnClickListener { v: View? -> showAppPage() }
        alert.visibility = View.VISIBLE
        imageAdapter!!.setShowCamera(false)
        folder_layout.visibility = View.GONE
        folder_layout.visibility = View.GONE
        imageAdapter?.notifyDataSetChanged()
    }

    //    override fun onItemClicked(position: Int) {
    //        when (position) {
    //            0 -> openCamera()
    //            else -> {
    //                mainViewModel.setImageSelection(position, mImagesAdapter?.images)
    //            }
    //        }
    //    }
    //    private void setDoneVisibilty(boolean visible) {
    //        icDone.visibility = visible ? View.VISIBLE : View.GONE;
    //    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                storagePermissionGranted()
            }
            REQUEST_CAMERA -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraPermissionGranted()
            } else {
                showAlert()
            }
        }
    }

    private fun sendResults() {
        val images = mainViewModel!!.getSelectedPaths()
        val intent = Intent()
        intent.putExtra(GligarPicker.IMAGES_RESULT, images)
        setResult(RESULT_OK, intent)
        finish()
    }

    //    private fun showLimitMsg() {
    //        Snackbar.make(rootView, R.string.over_limit_msg, Snackbar.LENGTH_LONG).show()
    //    }
    private fun showAppPage() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun setLoadMoreListener() {
        if (loadMoreListener != null) {
            folder_list.removeOnScrollListener(loadMoreListener!!)
        }
        loadMoreListener = LoadMoreListener((folder_list.layoutManager as GridLayoutManager?)!!)
        loadMoreListener!!.setOnLoadMoreListener(this)
        folder_list.addOnScrollListener(loadMoreListener!!)
    }

    override fun onLoadMore() {
        if (!isPermissionGranted) {
            return
        }
        mainViewModel!!.loadMoreImages()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (mainViewModel != null) {
            mainViewModel!!.saveStateImages =
                if (imageAdapter!!.images != null) imageAdapter!!.images else ArrayList()
            mainViewModel!!.saveState()
        }
        super.onSaveInstanceState(outState)
        outState.putString(BUNDLE_CAMERA_PATH, cameraPath)
    }

    companion object {
        const val REQUEST_IMAGE = 66
        const val REQUEST_CAMERA = 67
        const val BUNDLE_CAMERA_PATH = "CameraPath"
        const val REQUEST_OUTPUT = "outputList"
        const val EXTRA_SELECT_MODE = "SelectMode"
        const val EXTRA_SHOW_CAMERA = "ShowCamera"
        const val EXTRA_ENABLE_PREVIEW = "EnablePreview"
        const val EXTRA_ENABLE_CROP = "EnableCrop"
        const val EXTRA_MAX_SELECT_NUM = "MaxSelectNum"
        const val MODE_MULTIPLE = 1
        const val MODE_SINGLE = 2
        private const val CAMERA_PERMISSION_REQUEST_CODE = 400
        private const val STORAGE_PERMISSION_REQUEST_CODE = 500
        private const val REQUEST_CODE_CAMERA_IMAGE = 102
        const val EXTRA_LIMIT = "limit"
        const val EXTRA_CAMERA_DIRECT = "camera_direct"
        const val EXTRA_DISABLE_CAMERA = "disable_camera"
        fun start(
            activity: AppCompatActivity,
            maxSelectNum: Int,
            mode: Int,
            isShow: Boolean,
            enablePreview: Boolean,
            enableCrop: Boolean
        ) {
            val intent = Intent(activity, ImageSelectorActivity::class.java)
            intent.putExtra(EXTRA_MAX_SELECT_NUM, maxSelectNum)
            intent.putExtra(EXTRA_SELECT_MODE, mode)
            intent.putExtra(EXTRA_SHOW_CAMERA, isShow)
            intent.putExtra(EXTRA_ENABLE_PREVIEW, enablePreview)
            intent.putExtra(EXTRA_ENABLE_CROP, enableCrop)
            activity.startActivityForResult(intent, REQUEST_IMAGE)
        }

        fun startActivityForResult(fragment: Fragment, requestCode: Int, intent: Intent) {
            intent.setClass(fragment.requireContext(), ImageSelectorActivity::class.java)
            fragment.startActivityForResult(intent, requestCode)
        }

        fun startActivityForResult(activity: Activity, requestCode: Int, intent: Intent) {
            intent.setClass(activity, ImageSelectorActivity::class.java)
            activity.startActivityForResult(intent, requestCode)
        }
    }
}