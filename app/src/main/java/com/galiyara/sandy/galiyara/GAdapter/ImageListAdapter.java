package com.galiyara.sandy.galiyara.GAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.galiyara.sandy.galiyara.GActivity.MainActivity;
import com.galiyara.sandy.galiyara.GAlbumModel.Albums;
import com.galiyara.sandy.galiyara.GInterface.AdapterEventListener;
import com.galiyara.sandy.galiyara.GFileUtil.GFileUtils;
import com.galiyara.sandy.galiyara.R;

import java.util.ArrayList;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.CustomViewHolder> {
    private ArrayList<Albums> imagesPath = new ArrayList<>();
    private Context mContext;
    private LayoutInflater layoutInflater;
    private RequestManager glide;
    private RequestOptions options;
    private AdapterEventListener listener;
    private MainActivity mainActivity;

    public ImageListAdapter(Context c, ArrayList<Albums> i, RequestManager g,RequestOptions op, AdapterEventListener listener) {
        this.imagesPath.addAll(i);
        this.mContext = c;
        this.glide = g;
        this.options = op;
        this.listener = listener;
        this.mainActivity = (MainActivity) c;
        this.layoutInflater = LayoutInflater.from(c);
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    @Override
    @NonNull
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.recycler_view_item,parent,false);
        return new CustomViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Albums albm = imagesPath.get(position);
        holder.bind(albm);
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
        TextView textView;
        TextView countView;

        CustomViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.thumbnail);
            textView = itemView.findViewById(R.id.title);
            countView = itemView.findViewById(R.id.titleCount);
        }
        void bindCallback(final String folderPath,String albumName) {
            itemView.setOnClickListener(v -> {
                if(!mainActivity.isInActionMode())
                    listener.itemClicked(folderPath,albumName);
                else {
                    boolean status = mainActivity.isAlreadySelected(folderPath);
                    itemView.setActivated(!status);
                    mainActivity.updateSelection(!status,folderPath);
                }
            });
            itemView.setOnLongClickListener(v -> {
                if(!mainActivity.isInActionMode()) {
                    listener.itemLongClicked(folderPath);
                    itemView.setActivated(true);
                    return true;
                }
                return false;
            });
        }
        @SuppressLint("DefaultLocale")
        void bind(Albums albm) {
            String photos = mContext.getString(R.string.photos);
            String countStr = String.format("%d %s",albm.getCount(),photos);
            glide.load(albm.getImagePath()).apply(options).thumbnail(0.4f).into(imageView);
            String folderPath = GFileUtils.getFilePathOnly(albm.getImagePath());
            itemView.setActivated(mainActivity.isAlreadySelected(folderPath));
            textView.setText(albm.getImageFolder());
            countView.setText(countStr);
            bindCallback(folderPath,albm.getImageFolder());
        }
    }

    public void removeAlbum(ArrayList<String> albums) {
        for(String a : albums) {
            for(Albums al : imagesPath) {
                String p = GFileUtils.getFilePathOnly(al.getImagePath());
                if(p.equals(a)) {
                    try {
                        int pos = imagesPath.indexOf(al);
                        imagesPath.remove(al);
                        notifyItemRemoved(pos);
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void refresh(ArrayList<Albums> data) {
        imagesPath.clear();
        notifyDataSetChanged();
        imagesPath.addAll(data);
        notifyItemRangeInserted(0,imagesPath.size());
    }
}
