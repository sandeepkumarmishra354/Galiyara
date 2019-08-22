package com.galiyara.sandy.galiyara.GAdapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.galiyara.sandy.galiyara.GActivity.TrashActivity;
import com.galiyara.sandy.galiyara.GInterface.AdapterEventListener;
import com.galiyara.sandy.galiyara.R;

import java.util.ArrayList;

public class TrashAdapter extends RecyclerView.Adapter<TrashAdapter.CustomViewHolder> {

    private ArrayList<String> trashedImages;
    private LayoutInflater layoutInflater;
    private AdapterEventListener listener;
    private TrashActivity trashActivity;
    private RequestManager glide;

    public TrashAdapter(Context ctx, ArrayList<String> list,AdapterEventListener listener) {
        this.trashedImages = new ArrayList<>(list);
        this.layoutInflater = LayoutInflater.from(ctx);
        this.trashActivity = (TrashActivity) ctx;
        this.glide = Glide.with(ctx);
        this.listener = listener;
    }

    @Override
    @NonNull
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.album_image_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        String imgPath = trashedImages.get(position);
        holder.bind(imgPath);
        holder.bindCallbacks(imgPath);
    }

    @Override
    public int getItemCount() {
        return trashedImages.size();
    }

    @Override
    public void onViewRecycled(@NonNull CustomViewHolder viewHolder) {
        super.onViewRecycled(viewHolder);
        glide.clear(viewHolder.imageView);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CardView cardView;

        CustomViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.albumImageCardView);
            imageView = itemView.findViewById(R.id.albumThumbnail);
        }

        void bind(String imgPath) {
            glide.load(imgPath)
                    .thumbnail(0.4f)
                    .into(imageView);
            itemView.setActivated(trashActivity.isSelected(imgPath));
        }

        void bindCallbacks(String imgPath) {
            imageView.setOnClickListener(v -> {
                if(!trashActivity.getActionMode())
                    listener.itemClicked(imgPath);
                else {
                    itemView.setActivated(!itemView.isActivated());
                    trashActivity.updateSelection(itemView.isActivated(),imgPath);
                }
            });
            imageView.setOnLongClickListener(v -> {
                boolean status = false;
                if(!trashActivity.getActionMode())
                    if(listener != null) {
                        itemView.setActivated(true);
                        listener.itemLongClicked(imgPath);
                        status = true;
                    }
                return status;
            });
        }
    }

    public void removeFromAdapter(ArrayList<String> list) {
        trashedImages.removeAll(list);
        notifyDataSetChanged();
    }
}
