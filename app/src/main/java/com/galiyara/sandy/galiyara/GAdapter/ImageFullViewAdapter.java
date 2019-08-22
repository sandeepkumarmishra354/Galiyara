package com.galiyara.sandy.galiyara.GAdapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.galiyara.sandy.galiyara.GActivity.ImageViewActivity;
import com.galiyara.sandy.galiyara.GHelper.GSettings;
import com.galiyara.sandy.galiyara.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.ArrayList;

import androidx.viewpager.widget.PagerAdapter;

public class ImageFullViewAdapter extends PagerAdapter {
    private LayoutInflater mInflater;
    private ArrayList<String> imageList;
    private ImageViewActivity imageViewActivity;
    private RequestManager glide;

    public ImageFullViewAdapter(Context c, ArrayList<String> il) {
        //Context mContext = c;
        this.imageList = il;
        mInflater = LayoutInflater.from(c);
        imageViewActivity = (ImageViewActivity) c;
        this.glide = Glide.with(c);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }
    @Override
    public boolean isViewFromObject(@NonNull View view, Object object) {
        return view == object;
    }
    @NonNull
    @Override
    public View instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = mInflater.inflate(R.layout.view_pager_item,container,false);
        itemView.setTag(position);
        PhotoView photoView = itemView.findViewById(R.id.fullScreenImageView);
        RequestOptions rq;
        if(GSettings.getGeneralSetting().getShowHq())
            rq = new RequestOptions().override(Target.SIZE_ORIGINAL,
                    Target.SIZE_ORIGINAL);
        else
            rq = new RequestOptions();
        glide.load(imageList.get(position))
             .apply(rq)
             .into(photoView);

        photoView.setMaximumScale(10f);
        photoView.setMediumScale(5f);
        container.addView(itemView);
        return itemView;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        PhotoView imgView = view.findViewById(R.id.fullScreenImageView);
        glide.clear(imgView);
        container.removeView(view);
    }
    @Override
    public int getItemPosition(@NonNull Object object) {
        return ImageFullViewAdapter.POSITION_NONE;
    }

    public void removeAndUpdateAdapter(String imgPath) {
        if(deleteFilePermanently(imgPath))
            removeImageFromAdapter(imgPath);
    }

    public void removeImageFromAdapter(String imgPath) {
        imageList.remove(imgPath);
        notifyDataSetChanged();
    }

    private boolean deleteFilePermanently(String filePath) {
        File file = new File(filePath);
        boolean status = false;
        if(file.exists()) {
            if(file.delete()) {
                imageViewActivity.updateDeletedMedia(filePath);
                status = true;
            }
        }

        return status;
    }

    public void updateAdapterAfterRename(String oldImg,String newImg) {
        if(imageList.contains(oldImg)) {
            int pos = imageList.indexOf(oldImg);
            imageList.set(pos,newImg);
            notifyDataSetChanged();
        }
    }
    public void addNewFileToAdapter(String newFile) {
        if(!imageList.contains(newFile)) {
            imageList.add(newFile);
            notifyDataSetChanged();
        }
    }
}
