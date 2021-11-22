package com.yongchun.library.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yongchun.library.R;
import com.yongchun.library.model.AlbumItem;
import com.yongchun.library.model.ImageItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dee on 15/11/20.
 */
public class ImageFolderAdapter extends RecyclerView.Adapter<ImageFolderAdapter.ViewHolder>{
    private Context context;
    private List<AlbumItem> folders = new ArrayList<>();
    private int checkedIndex = 0;

    private OnItemClickListener onItemClickListener;
    public ImageFolderAdapter(Context context) {
        this.context = context;
    }

    public void bindFolder(List<AlbumItem> folders){
        this.folders = folders;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_folder,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final AlbumItem folder = folders.get(position);
        holder.folderName.setText(folder.getName());

        holder.isSelected.setVisibility(checkedIndex == position ? View.VISIBLE : View.GONE);

        holder.contentView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                checkedIndex = position;
                notifyDataSetChanged();
                onItemClickListener.onItemClick(folder, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView folderName;
        ImageView isSelected;

        View contentView;
        public ViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            folderName = (TextView) itemView.findViewById(R.id.folder_name);
            isSelected = (ImageView) itemView.findViewById(R.id.is_selected);
        }
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener{
        void onItemClick(AlbumItem albumItem, int position);
    }
}
