package com.galiyara.sandy.galiyara.GAdapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.galiyara.sandy.galiyara.GActivity.AlbumsActivity;
import com.galiyara.sandy.galiyara.GInterface.AdapterEventListener;
import com.galiyara.sandy.galiyara.R;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.CustomViewHolder> {
    private ArrayList<String> imagesPath;
    private LayoutInflater layoutInflater;
    private RequestManager glide;
    private RequestOptions options;
    private AdapterEventListener listener;
    private AlbumsActivity albumsActivity;

    public AlbumAdapter(Context c, ArrayList<String> i, RequestManager g,RequestOptions op, AdapterEventListener listener) {
        this.imagesPath = i;
        this.glide = g;
        this.options = op;
        this.listener = listener;
        this.layoutInflater = LayoutInflater.from(c);
        this.albumsActivity = (AlbumsActivity) c;
    }

    @Override
    @NonNull
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.album_image_item,parent,false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        String imgPath = imagesPath.get(position);
        holder.bind(imgPath);
    }

    @Override
    public int getItemCount() {
        return imagesPath.size();
    }

    @Override
    public void onViewRecycled(@NonNull CustomViewHolder viewHolder) {
        super.onViewRecycled(viewHolder);
        glide.clear(viewHolder.imageView);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CustomViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.albumThumbnail);
        }
        void bindCallback(final String imagePath) {
            itemView.setOnClickListener(v -> {
                if(!albumsActivity.isInActionMode)
                    listener.itemClicked(imagePath);
                else {
                    itemView.setActivated(!itemView.isActivated());
                    albumsActivity.updateSelection(itemView.isActivated(),imagePath);
                }
            });
            itemView.setOnLongClickListener(v -> {
                if(!albumsActivity.isInActionMode) {
                    albumsActivity.isInActionMode = true;
                    itemView.setActivated(true);
                    listener.itemLongClicked(imagePath);
                    return true;
                }
                return false;
            });
        }
        void bind(String imgPath) {
            glide.load(imgPath).apply(options).thumbnail(0.4f).into(imageView);
            itemView.setActivated(albumsActivity.isAlreadySelected(imgPath));
            bindCallback(imgPath);
        }
    }

    public void updateAdapter(ArrayList<String> selectedImage) {
        for(String i : selectedImage) {
            try {
                int pos = imagesPath.indexOf(i);
                imagesPath.remove(i);
                notifyItemRemoved(pos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateForIndividual(String imgPath) {
        if(imagesPath.contains(imgPath)) {
            int pos = imagesPath.indexOf(imgPath);
            imagesPath.remove(imgPath);
            albumsActivity.allImageList.remove(imgPath);
            notifyItemRemoved(pos);
        }
    }
    public void updateAfterRename(String oldImg,String newImg) {
        if(imagesPath.contains(oldImg)) {
            int pos = imagesPath.indexOf(oldImg);
            imagesPath.set(pos,newImg);
            notifyDataSetChanged();
        }
    }
    public void addNewFileToAdapter(String newFile) {
        if(!imagesPath.contains(newFile)) {
            try {
                imagesPath.add(0,newFile);
                notifyItemInserted(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void refresh(ArrayList<String> images) {
        imagesPath.clear();
        notifyDataSetChanged();
        imagesPath.addAll(images);
        notifyItemRangeInserted(0,imagesPath.size());
    }
}
