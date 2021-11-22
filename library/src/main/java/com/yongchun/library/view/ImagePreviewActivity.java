package com.yongchun.library.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.yongchun.library.R;
import com.yongchun.library.model.ImageItem;
import com.yongchun.library.widget.PreviewViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dee on 15/11/24.
 */
public class ImagePreviewActivity extends AppCompatActivity {
    public static final int REQUEST_PREVIEW = 68;
    public static final String EXTRA_PREVIEW_LIST = "previewList";
    public static final String EXTRA_PREVIEW_SELECT_LIST = "previewSelectList";
    public static final String EXTRA_MAX_SELECT_NUM = "maxSelectNum";
    public static final String EXTRA_POSITION = "position";

    public static final String OUTPUT_LIST = "outputList";
    public static final String OUTPUT_ISDONE = "isDone";

    private LinearLayout barLayout;
    private RelativeLayout selectBarLayout;
    private Toolbar toolbar;
    private TextView doneText;
    private CheckBox checkboxSelect;
    private PreviewViewPager viewPager;


    private int position;
    private int maxSelectNum;
    private List<ImageItem> images = new ArrayList<>();
    private List<ImageItem> selectImages = new ArrayList<>();


    private boolean isShowBar = true;


    public static void startPreview(AppCompatActivity context, List<ImageItem> images, List<ImageItem> selectImages, int maxSelectNum, int position) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        intent.putExtra(EXTRA_PREVIEW_LIST, (ArrayList) images);
        intent.putExtra(EXTRA_PREVIEW_SELECT_LIST, (ArrayList) selectImages);
        intent.putExtra(EXTRA_POSITION, position);
        intent.putExtra(EXTRA_MAX_SELECT_NUM, maxSelectNum);
        context.startActivityForResult(intent, REQUEST_PREVIEW);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        initView();
        registerListener();
    }

    public void initView() {
        images = (List<ImageItem>) getIntent().getSerializableExtra(EXTRA_PREVIEW_LIST);
        selectImages = (List<ImageItem>) getIntent().getSerializableExtra(EXTRA_PREVIEW_SELECT_LIST);
        maxSelectNum = getIntent().getIntExtra(EXTRA_MAX_SELECT_NUM, 9);
        position = getIntent().getIntExtra(EXTRA_POSITION, 1);

        barLayout = findViewById(R.id.bar_layout);
        selectBarLayout = findViewById(R.id.select_bar_layout);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle((position + 1) + "/" + images.size());
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_back);


        doneText = findViewById(R.id.done_text);
        onSelectNumChange();

        checkboxSelect = (CheckBox) findViewById(R.id.checkbox_select);
        onImageSwitch(position);


        viewPager = findViewById(R.id.preview_pager);
        viewPager.setAdapter(new SimpleFragmentAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(position);
    }

    public void registerListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                toolbar.setTitle(position + 1 + "/" + images.size());
                onImageSwitch(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDoneClick(false);
            }
        });
        checkboxSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = checkboxSelect.isChecked();
                if (selectImages.size() >= maxSelectNum && isChecked) {
                    Toast.makeText(ImagePreviewActivity.this, getString(R.string.message_max_num, maxSelectNum), Toast.LENGTH_LONG).show();
                    checkboxSelect.setChecked(false);
                    return;
                }
                ImageItem image = images.get(viewPager.getCurrentItem());
                if (isChecked) {
                    selectImages.add(image);
                } else {
                    for (ImageItem media : selectImages) {
                        if (media.getImagePath().equals(image.getImagePath())) {
                            selectImages.remove(media);
                            break;
                        }
                    }
                }
                onSelectNumChange();
            }
        });
        doneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDoneClick(true);
            }
        });
    }

    public class SimpleFragmentAdapter extends FragmentPagerAdapter {
        public SimpleFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ImagePreviewFragment.getInstance(images.get(position).getImagePath());
        }

        @Override
        public int getCount() {
            return images.size();
        }
    }

    public void onSelectNumChange() {
        boolean enable = selectImages.size() != 0;
        doneText.setEnabled(enable);
        if (enable) {
            doneText.setText(getString(R.string.done_num, selectImages.size(), maxSelectNum));
        } else {
            doneText.setText(R.string.done);
        }
    }

    public void onImageSwitch(int position) {
        checkboxSelect.setChecked(isSelected(images.get(position)));
    }

    public boolean isSelected(ImageItem image) {
        for (ImageItem media : selectImages) {
            if (media.getImagePath().equals(image.getImagePath())) {
                return true;
            }
        }
        return false;
    }

    private void hideStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    private void showStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    public void switchBarVisibility() {
        barLayout.setVisibility(isShowBar ? View.GONE : View.VISIBLE);
        toolbar.setVisibility(isShowBar ? View.GONE : View.VISIBLE);
        selectBarLayout.setVisibility(isShowBar ? View.GONE : View.VISIBLE);
        if (isShowBar) {
            hideStatusBar();
        } else {
            showStatusBar();
        }
        isShowBar = !isShowBar;
    }
    public void onDoneClick(boolean isDone){
        Intent intent = new Intent();
        intent.putExtra(OUTPUT_LIST,(ArrayList)selectImages);
        intent.putExtra(OUTPUT_ISDONE,isDone);
        setResult(RESULT_OK,intent);
        finish();
    }
}
