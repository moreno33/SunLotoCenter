package com.yongchun.library.view;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yongchun.library.R;
import com.yongchun.library.adapter.ImageListAdapter;
import com.yongchun.library.adapter.LoadMoreListener;
import com.yongchun.library.model.ImageItem;
import com.yongchun.library.model.PickerViewModel;
import com.yongchun.library.utils.FileUtils;
import com.yongchun.library.utils.GridSpacingItemDecoration;
import com.yongchun.library.utils.ScreenUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.yongchun.library.utils.CursorUriKt.PAGE_SIZE;
import static com.yongchun.library.utils.DoWhileKt.createTempImageFile;
import static com.yongchun.library.utils.GligarPicker.IMAGES_RESULT;


/**
 * Created by dee on 15/11/19.
 */
public class ImageSelectorActivity extends AppCompatActivity implements LoadMoreListener.OnLoadMoreListener {
    public final static int REQUEST_IMAGE = 66;
    public final static int REQUEST_CAMERA = 67;

    public final static String BUNDLE_CAMERA_PATH = "CameraPath";

    public final static String REQUEST_OUTPUT = "outputList";

    public final static String EXTRA_SELECT_MODE = "SelectMode";
    public final static String EXTRA_SHOW_CAMERA = "ShowCamera";
    public final static String EXTRA_ENABLE_PREVIEW = "EnablePreview";
    public final static String EXTRA_ENABLE_CROP = "EnableCrop";
    public final static String EXTRA_MAX_SELECT_NUM = "MaxSelectNum";

    public final static int MODE_MULTIPLE = 1;
    public final static int MODE_SINGLE = 2;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 400;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 500;

    private static final int REQUEST_CODE_CAMERA_IMAGE = 102;

    private boolean isPermissionGranted = false;
    private boolean isSaveState = false;
    private String path;
    private ImageView alertBtn;
    private View alert;

    private int maxSelectNum = 9;
    private int selectMode = MODE_MULTIPLE;
    private boolean showCamera = true;
    private boolean enablePreview = true;
    private boolean enableCrop = false;
    private boolean forceCamera = false;

    private int spanCount = 3;

    private Toolbar toolbar;
    private TextView doneText;

    private TextView previewText;

    private RecyclerView recyclerView;
    private ImageListAdapter imageAdapter;

    private LinearLayout folderLayout;
    private TextView folderName;
    private FolderWindow folderWindow;

    private String cameraPath;

    private PickerViewModel mainViewModel;
    private LoadMoreListener loadMoreListener = null;

    public final static String EXTRA_LIMIT = "limit";
    public final static String EXTRA_CAMERA_DIRECT = "camera_direct";
    public final static String EXTRA_DISABLE_CAMERA = "disable_camera";

    public static void start(AppCompatActivity activity, int maxSelectNum, int mode, boolean isShow, boolean enablePreview, boolean enableCrop) {
        Intent intent = new Intent(activity, ImageSelectorActivity.class);
        intent.putExtra(EXTRA_MAX_SELECT_NUM, maxSelectNum);
        intent.putExtra(EXTRA_SELECT_MODE, mode);
        intent.putExtra(EXTRA_SHOW_CAMERA, isShow);
        intent.putExtra(EXTRA_ENABLE_PREVIEW, enablePreview);
        intent.putExtra(EXTRA_ENABLE_CROP, enableCrop);
        activity.startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageselector);
        mainViewModel = new ViewModelProvider(this, new SavedStateViewModelFactory(getApplication(), this)).get(
                PickerViewModel.class
        );

        alert = findViewById(R.id.alert);
        alertBtn= alert.findViewById(R.id.alert_btn);

        maxSelectNum = getIntent().getIntExtra(EXTRA_MAX_SELECT_NUM, 9);
        selectMode = getIntent().getIntExtra(EXTRA_SELECT_MODE, MODE_MULTIPLE);
        showCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        enablePreview = getIntent().getBooleanExtra(EXTRA_ENABLE_PREVIEW, true);
        enableCrop = getIntent().getBooleanExtra(EXTRA_ENABLE_CROP, false);

        if (selectMode == MODE_MULTIPLE) {
            enableCrop = false;
        } else {
            enablePreview = false;
        }
        if (savedInstanceState != null) {
            cameraPath = savedInstanceState.getString(BUNDLE_CAMERA_PATH);
        }
        initView();
        registerListener();

        mainViewModel.init(getContentResolver());
        if (savedInstanceState != null) {
            isSaveState = true;
            mainViewModel.loadSaveState();
        } else {
            mainViewModel.bindArguments(getIntent().getExtras());

        }
        checkStoragePermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkStoragePermission();
    }

    public void initView() {
        folderWindow = new FolderWindow(this);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.picture);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_back);

        doneText = findViewById(R.id.done_text);
        doneText.setVisibility(selectMode == MODE_MULTIPLE ? View.VISIBLE : View.GONE);

        previewText = findViewById(R.id.preview_text);
        previewText.setVisibility(enablePreview ? View.VISIBLE : View.GONE);

        folderLayout = findViewById(R.id.folder_layout);
        folderName = findViewById(R.id.folder_name);

        setImagesAdapter();

    }

    private void setImagesAdapter() {

        recyclerView = findViewById(R.id.folder_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, ScreenUtils.dip2px(this, 2), false));
        recyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));

        imageAdapter = new ImageListAdapter(this, maxSelectNum, selectMode, showCamera,enablePreview);

        imageAdapter.bindImages((isSaveState && !mainViewModel.getSaveStateImages().isEmpty()) ?
                mainViewModel.getSaveStateImages():
                mainViewModel.getDumpImagesList());
        mainViewModel.getSaveStateImages().clear();
        recyclerView.setAdapter(imageAdapter);
        observe();
        setLoadMoreListener();

    }

    private void observe() {

        mainViewModel.getMDirectCamera$library_debug().observe(this, aBoolean -> forceCamera = aBoolean);

        mainViewModel.getMAlbums().observe(this, albumItems -> {
            folderWindow.bindFolder(albumItems, mainViewModel);
        });
        mainViewModel.getMLastAddedImages().observe(this, imageItems -> addImages(imageItems));

        mainViewModel.getMNotifyPosition$library_debug().observe(this, integer -> imageAdapter.notifyItemChanged(integer));

        mainViewModel.getMNotifyInsert().observe(this, item -> {
            imageAdapter.getSelectedImages().add(0, item);

            imageAdapter.getOnImageSelectChangedListener().onChange(imageAdapter.getSelectedImages());
            imageAdapter.notifyDataSetChanged();
        });

        mainViewModel.getMDoneEnabled$library_debug().observe(this, aBoolean -> {
//                setDoneVisibilty(aBoolean);
        });

        mainViewModel.getShowOverLimit$library_debug().observe(this, aBoolean -> showLimitMsg());
    }

    private void addImages(ArrayList<ImageItem> it) {
        loadMoreListener.setFinished(false);
        if (it.isEmpty()) {
            loadMoreListener.setFinished(true);
            loadMoreListener.setLoaded();
            return;
        }
        boolean isFirstPage = mainViewModel.getMPage() == 0;
        isPermissionGranted = true;
        if (it.size() < PAGE_SIZE) {
            loadMoreListener.setFinished(true);
        }

        int lastPos = imageAdapter.getImages().size();
        if (isFirstPage) {
            imageAdapter.bindImages(it);
            imageAdapter.notifyDataSetChanged();
        } else {
            imageAdapter.getImages().addAll(it);
            imageAdapter.notifyItemRangeInserted(lastPos, it.size());
        }
        loadMoreListener.setLoaded();
        if (forceCamera) {
            checkCameraPermission();
            forceCamera = false;
        }
    }

    public void registerListener() {
        toolbar.setNavigationOnClickListener(v -> finish());
        folderLayout.setOnClickListener(v -> {
            if (folderWindow.isShowing()) {
                folderWindow.dismiss();
            } else {
                folderWindow.showAsDropDown(toolbar);
            }
        });
        imageAdapter.setOnImageSelectChangedListener(new ImageListAdapter.OnImageSelectChangedListener() {
            @Override
            public void onChange(ArrayList<ImageItem> selectImages) {
                boolean enable = selectImages.size() != 0;
                doneText.setEnabled(enable ? true : false);
                previewText.setEnabled(enable ? true : false);
                if (enable) {
                    doneText.setText(getString(R.string.done_num, selectImages.size(), maxSelectNum));
                    previewText.setText(getString(R.string.preview_num, selectImages.size()));
                } else {
                    doneText.setText(R.string.done);
                    previewText.setText(R.string.preview);
                }
            }

            @Override
            public void onTakePhoto() {
                checkCameraPermission();
            }

            @Override
            public void onPictureClick(ImageItem media, int position) {
                if (enablePreview) {
                    startPreview(imageAdapter.getImages(), position);
                } else if (enableCrop) {
                    startCrop(media.getImagePath());
                } else {
                    onSelectDone(media.getImagePath());
                }
            }
        });
        doneText.setOnClickListener(v -> onSelectDone(imageAdapter.getSelectedImages()));

        folderWindow.setOnItemClickListener((item, pos) -> {
            folderWindow.dismiss();
            folderName.setText(item.getName());
            mainViewModel.onAlbumChanged(item, pos);
            imageAdapter.getImages().clear();
            imageAdapter.notifyDataSetChanged();


        });
        previewText.setOnClickListener(v -> startPreview(imageAdapter.getSelectedImages(), 0));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            // on take photo success
            if (requestCode == REQUEST_CAMERA) {


                mainViewModel.addCameraItem(imageAdapter.getImages());
//                setDoneVisibilty(true)
//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(cameraPath))));
//                if (enableCrop) {
//                    startCrop(cameraPath);
//                } else {
//                    onSelectDone(cameraPath);
//                }
            }
            //on preview select change
            else if (requestCode == ImagePreviewActivity.REQUEST_PREVIEW) {
                boolean isDone = data.getBooleanExtra(ImagePreviewActivity.OUTPUT_ISDONE, false);
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePreviewActivity.OUTPUT_LIST);
                if (isDone) {
                    onSelectDone(images);
                }else{
                    imageAdapter.bindSelectImages(images);
                }
            }
            // on crop success
            else if (requestCode == ImageCropActivity.REQUEST_CROP) {
                String path = data.getStringExtra(ImageCropActivity.OUTPUT_PATH);
                onSelectDone(path);
            }
        }
    }

    /**
     * start to camera、preview、crop
     */
    public void startCamera() {

        checkCameraPermission();
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File cameraFile = FileUtils.createCameraFile(this);
            cameraPath = cameraFile.getAbsolutePath();
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }
    }

    public void startPreview(List<ImageItem> previewImages, int position) {
        ImagePreviewActivity.startPreview(this, previewImages, imageAdapter.getSelectedImages(), maxSelectNum, position);
    }

    public void startCrop(String path) {
        ImageCropActivity.startCrop(this, path);
    }


    private void showLimitMsg() {
//        Snackbar.make(rootView, R.string.over_limit_msg, Snackbar.LENGTH_LONG).show();
    }

    /**
     * on select done
     *
     * @param medias
     */
    public void onSelectDone(List<ImageItem> medias) {
        ArrayList<String> images = new ArrayList<>();
        for (ImageItem media : medias) {
            images.add(media.getImagePath());
        }
        onResult(images);
    }

    public void onSelectDone(String path) {
        ArrayList<String> images = new ArrayList<>();
        images.add(path);
        onResult(images);
    }

    public void onResult(ArrayList<String> images) {
        setResult(RESULT_OK, new Intent().putStringArrayListExtra(REQUEST_OUTPUT, images));
        finish();
    }


    private void checkCameraPermission() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission(android.Manifest.permission.CAMERA)) {
                cameraPermissionGranted();
            } else {
                requestPermission(new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA);
            }
        } else {
            cameraPermissionGranted();
        }
    }


    private void hideAlert() {
        alert.setVisibility(View.GONE);
    }


    public static void startActivityForResult(Fragment fragment, int requestCode, Intent intent) {
        intent.setClass(fragment.getContext(), ImageSelectorActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void startActivityForResult(Activity activity, int requestCode, Intent intent) {
        intent.setClass(activity, ImageSelectorActivity.class);
        activity.startActivityForResult(intent, requestCode);
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


    private void loadAlbums() {
        isPermissionGranted = true;
        mainViewModel.loadAlbums();
    }

    private void storagePermissionGranted() {
        hideAlert();

        imageAdapter.setShowCamera(true);
        folderLayout.setVisibility(View.VISIBLE);
        previewText.setVisibility(View.VISIBLE);

        loadAlbums();
    }

    private void cameraPermissionGranted() {
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
            File photoFile = createTempImageFile(this);
            mainViewModel.setMCurrentPhotoPath(photoFile.getAbsolutePath());
            cameraPath= photoFile.getAbsolutePath();
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri myPhotoFileUri = FileProvider.getUriForFile(
                    this,
                    this.getApplicationContext().getPackageName() + ".provider",
                    photoFile
            );
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, myPhotoFileUri);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(
                    Intent.createChooser(cameraIntent, ""),
                    REQUEST_CAMERA
            );
        } catch (Exception e) {
            Log.e("Picker", e.getMessage(), e);
        }
    }


    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                storagePermissionGranted();
            } else {
                requestPermission(
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_REQUEST_CODE
                );
            }
        } else {
            storagePermissionGranted();
        }
    }

    private boolean checkPermission(String permission) {
        int result = ContextCompat.checkSelfPermission(this, permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String[] permissions, int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
            imageAdapter.getImages().clear();
            showAlert();
        } else {
            ActivityCompat.requestPermissions(this, permissions, requestCode);
        }
    }

    private void showAlert() {
        alertBtn.setOnClickListener(v -> showAppPage());
        alert.setVisibility(View.VISIBLE);
        imageAdapter.setShowCamera(false);
        folderLayout.setVisibility(View.GONE);
        previewText.setVisibility(View.GONE);
        imageAdapter.notifyDataSetChanged();
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




    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case STORAGE_PERMISSION_REQUEST_CODE:
                if (grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    storagePermissionGranted();
                }
                break;
            case REQUEST_CAMERA:
                if (grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraPermissionGranted();
                } else {
                    showAlert();
                }
                break;

        }
    }

    private void sendResults() {
        String[] images = mainViewModel.getSelectedPaths();
        Intent intent = new Intent();
        intent.putExtra(IMAGES_RESULT, images);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

//    private fun showLimitMsg() {
//        Snackbar.make(rootView, R.string.over_limit_msg, Snackbar.LENGTH_LONG).show()
//    }


    private void showAppPage() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", getPackageName(), null)
        );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void setLoadMoreListener() {
        if (loadMoreListener != null) {
            recyclerView.removeOnScrollListener(loadMoreListener);
        }
        loadMoreListener = new LoadMoreListener((GridLayoutManager)recyclerView.getLayoutManager());
        loadMoreListener.setOnLoadMoreListener(this);
        recyclerView.addOnScrollListener(loadMoreListener);
    }

    @Override
    public void onLoadMore() {
        if (!isPermissionGranted) {
            return;
        }
        mainViewModel.loadMoreImages();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mainViewModel!= null) {
            mainViewModel.setSaveStateImages(imageAdapter.getImages()!= null ? imageAdapter.getImages() : new ArrayList<>());
            mainViewModel.saveState();
        }
        super.onSaveInstanceState(outState);

        outState.putString(BUNDLE_CAMERA_PATH, cameraPath);
    }
}
