package com.yongchun.library.view;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yongchun.library.R;

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by dee on 15/11/25.
 */
public class ImagePreviewFragment extends Fragment {
    public static final String PATH = "path";

    public static ImagePreviewFragment getInstance(String path) {
        ImagePreviewFragment fragment = new ImagePreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PATH, path);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_image_preview, container, false);
        final ImageView imageView = (ImageView) contentView.findViewById(R.id.preview_image);
        final PhotoViewAttacher mAttacher = new PhotoViewAttacher(imageView);

        Glide.with(this)
                .asBitmap()
                .load(new File(getArguments().getString(PATH)))
//                .error(R.drawable.no_result)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new CustomTarget<Bitmap>(480, 800) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(resource);
                        imageView.buildDrawingCache();
                        mAttacher.update();
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) { }
                });

//
//        Glide.with(container.getContext())
//                .asBitmap()
//                .load()
//                .into(new SimpleTarget<Bitmap>(480, 800) {
//                    @Override
//                    public void onResourceReady(Bitmap resource, GlideAnimation? super Bitmap> glideAnimation) {
//                        imageView.setImageBitmap(resource);
//                        mAttacher.update();
//                    }
//                });
        mAttacher.setOnViewTapListener((view, x, y) -> {
            ImagePreviewActivity activity = (ImagePreviewActivity) getActivity();
            activity.switchBarVisibility();
        });
        return contentView;
    }
}
