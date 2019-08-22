package com.galiyara.sandy.galiyara.GActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.galiyara.sandy.galiyara.GAdapter.AlbumAdapter;
import com.galiyara.sandy.galiyara.GDialogs.FolderChooserDialog;
import com.galiyara.sandy.galiyara.GInterface.AdapterEventListener;
import com.galiyara.sandy.galiyara.GInterface.AsyncCallbackListener;
import com.galiyara.sandy.galiyara.GInterface.DialogActionListener;
import com.galiyara.sandy.galiyara.GDialogs.DeleteConfirmationDialog;
import com.galiyara.sandy.galiyara.GDialogs.FileOperationDialog;
import com.galiyara.sandy.galiyara.GEventBroadcaster.Broadcaster;
import com.galiyara.sandy.galiyara.GFileUtil.FileCopyMoveDelete;
import com.galiyara.sandy.galiyara.GFileUtil.MediaProvider;
import com.galiyara.sandy.galiyara.GHelper.GSorter;
import com.galiyara.sandy.galiyara.GHelper.GaliyaraHelper;
import com.galiyara.sandy.galiyara.GHelper.ThemeManager;
import com.galiyara.sandy.galiyara.GConstants.GaliyaraConst;
import com.galiyara.sandy.galiyara.R;

import java.util.ArrayList;
import java.util.Objects;

import io.multimoon.colorful.CAppCompatActivity;

public class AlbumsActivity extends CAppCompatActivity implements AsyncCallbackListener {

    public ArrayList<String> allImageList;
    private RecyclerView recyclerView;
    private AlbumAdapter adapter;
    private Toolbar toolbar;
    private String albumName;
    private String onlyAlbumName;
    public boolean isInActionMode = false;
    private ArrayList<String> selectedImagePath = new ArrayList<>();
    private Handler mHandler;
    private int COPY_MOVE_DELETE;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DeleteConfirmationDialog deleteDialog;
    private FileOperationDialog fileOperationDialog;
    private FolderChooserDialog folderChooserDialog;
    private FileCopyMoveDelete fileCopyMoveDelete;
    private MediaProvider GMediaProvider = new MediaProvider();
    private GaliyaraHelper galiyaraHelper = new GaliyaraHelper();
    private AdapterEventListener adapterEventListener;

    private BroadcastReceiver mPhotoDeletedFromFullViewReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String img_path = intent.getStringExtra("img_path");
            adapter.updateForIndividual(img_path);
        }
    };
    private BroadcastReceiver mRenameReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String oldImg = intent.getStringExtra("old_img");
            String newImg = intent.getStringExtra("new_img");
            updateAfterRename(oldImg,newImg);
        }
    };
    private BroadcastReceiver mNewAddedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String img_path = intent.getStringExtra("img_path");
            adapter.addNewFileToAdapter(img_path);
            if(!allImageList.contains(img_path))
                allImageList.add(0,img_path);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GaliyaraHelper.changeLanguage(this);
        setContentView(R.layout.activity_albums);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        albumName = Objects.requireNonNull(getIntent().getExtras()).getString(GaliyaraConst.FOLDER_PATH);
        onlyAlbumName = getIntent().getExtras().getString(GaliyaraConst.ALBUM_NAME);
        toolbar = findViewById(R.id.customToolbar);
        toolbar.setTitle(onlyAlbumName);
        setSupportActionBar(toolbar);
        ThemeManager.setPopupTheme(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        swipeRefreshLayout.setOnRefreshListener(() -> initAlbum(albumName));
        initListener();
        initAlbum(albumName);
        registerEvents();
        LocalBroadcastManager.getInstance(this).registerReceiver(mPhotoDeletedFromFullViewReceiver,
                new IntentFilter(GaliyaraConst.DELETE_FROM_IMAGE_VIEW));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRenameReceiver,
                new IntentFilter(GaliyaraConst.PHOTO_RENAMED));
        LocalBroadcastManager.getInstance(this).registerReceiver(mNewAddedReceiver,
                new IntentFilter(GaliyaraConst.NEW_PHOTO_ADDED));
        mHandler = new Handler();
    }

    private void freeMemOnDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPhotoDeletedFromFullViewReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRenameReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNewAddedReceiver);
        mPhotoDeletedFromFullViewReceiver = null;
        mRenameReceiver = null;
        mNewAddedReceiver = null;
        adapterEventListener = null;
        GMediaProvider = null;
        galiyaraHelper = null;
        fileOperationDialog = null;
        fileCopyMoveDelete = null;
        deleteDialog = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if(menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        else
            return super.onOptionsItemSelected(menuItem);
    }
    @Override
    public void onBackPressed() {
        if(fileOperationDialog == null && !isInActionMode) {
            super.onBackPressed();
            finish();
        }
        else {
            if(fileOperationDialog != null && !isInActionMode) {
                fileOperationDialog.dismissDialog();
            }
            else if(fileOperationDialog == null) {
                deselectAllImages();
                updateTitleBar();
            }
        }
    }
    @Override
    public void onDestroy() {
        freeMemOnDestroy();
        super.onDestroy();
    }
    @Override
    public void onResume() {
        if(GMediaProvider == null)
            GMediaProvider = new MediaProvider();
        if(galiyaraHelper == null)
            galiyaraHelper = new GaliyaraHelper();
        super.onResume();
        if(recyclerView != null)
            animateList();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu,menu);
        return true;
    }

    private void updateAfterRename(String oldImg,String newImg) {
        if(allImageList.contains(oldImg)) {
            int pos = allImageList.indexOf(oldImg);
            allImageList.set(pos,newImg);
            adapter.updateAfterRename(oldImg,newImg);
        }
    }
    private void animateList() {
        recyclerView.scheduleLayoutAnimation();
    }

    private void initListener() {
        if(adapterEventListener == null) {
            adapterEventListener = new AdapterEventListener() {
                @Override
                public void itemLongClicked(String path) {
                    isInActionMode = true;
                    toolbar.getMenu().clear();
                    toolbar.inflateMenu(R.menu.image_selected_menu);
                    updateSelection(true,path);
                }
                @Override
                public void itemClicked(String imagePath) {
                    Intent intent = new Intent(AlbumsActivity.this,ImageViewActivity.class);
                    intent.putExtra(GaliyaraConst.IMAGE_PATH,imagePath);
                    intent.putExtra(GaliyaraConst.ALBUM_NAME,albumName);
                    intent.putStringArrayListExtra(GaliyaraConst.IMAGE_LIST,allImageList);
                    startActivity(intent);
                }
            };
        }
    }

    private void initAlbum(String albumName) {
        new Thread(() -> {
            if(!albumName.equals(GaliyaraConst.ALL_IMAGES))
                allImageList = GMediaProvider.getAllShownImagePath(this,albumName);
            else if(albumName.equals(GaliyaraConst.ALL_IMAGES))
                allImageList = GMediaProvider.getAllImages(this);
            if(adapter == null) {
                RequestOptions options = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)
                        .override(200,200).skipMemoryCache(true);
                final RequestManager glide = Glide.with(this);
                adapter = new AlbumAdapter(this,allImageList,glide,options,adapterEventListener);
            }
            else
                mHandler.post(()->{
                    adapter.refresh(allImageList);
                    swipeRefreshLayout.setRefreshing(false);
                });
            if(recyclerView == null) {
                recyclerView = findViewById(R.id.albumRcView);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
                mHandler.post(()-> {
                    recyclerView.setLayoutManager(gridLayoutManager);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);
                });
            }
            animateList();
        }).start();
    }

    public void updateSelection(boolean activated,String imgPath) {
        if(activated)
            addToSelected(imgPath);
        else
            removeFromSelected(imgPath);

        updateTitleBar();
    }
    private void addToSelected(String img) {
        if(!selectedImagePath.contains(img))
            selectedImagePath.add(img);
    }
    private void removeFromSelected(String img) {
        selectedImagePath.remove(img);
    }

    private void registerEvents() {
        toolbar.setOnMenuItemClickListener(menuItem -> {
            final int mId = menuItem.getItemId();
            if(menuItem.getItemId() == R.id.sortMenuItem)
                ThemeManager.initSortOptions(toolbar);
            return menuItemClicked(mId);
        });
    }
    @SuppressLint("DefaultLocale")
    private void updateTitleBar() {
        if(selectedImagePath.isEmpty()) {
            isInActionMode = false;
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.sort_menu);
            toolbar.setTitle(onlyAlbumName);
        }
        else {
            String tStr = String.format("%d/%d",selectedImagePath.size(),allImageList.size());
            toolbar.setTitle(tStr);
        }
    }

    public boolean isAlreadySelected(String img) {
        return selectedImagePath.contains(img);
    }

    private boolean menuItemClicked(int mId) {
        boolean status = false;
        if(mId == R.id.deleteSelectedMenuItem) {
            if(isInActionMode && !selectedImagePath.isEmpty()) {
                COPY_MOVE_DELETE = GaliyaraConst.DELETE_FILE;
                deleteSelected();
            }
            status = true;
        }
        if(mId == R.id.sAllSelectedMenuItem) {
            if(selectedImagePath.size() == allImageList.size())
                deselectAllImages();
            else
                selectAllImages();

            updateTitleBar();

            status = true;
        }
        if(mId == R.id.shareSelectedMenuItem) {
            if(isInActionMode && !selectedImagePath.isEmpty())
                shareSelectedImage();
            status = true;
        }
        if(mId == R.id.moveSelectedMenuItem) {
            if(isInActionMode && !selectedImagePath.isEmpty()) {
                COPY_MOVE_DELETE = GaliyaraConst.MOVE_FILE;
                moveCopySelectedImage();
            }
            status = true;
        }
        if(mId == R.id.copySelectedMenuItem) {
            if(isInActionMode && !selectedImagePath.isEmpty()) {
                COPY_MOVE_DELETE = GaliyaraConst.COPY_FILE;
                moveCopySelectedImage();
            }
            status = true;
        }
        if(mId == R.id.sortDate) {
            if(GSorter.getSortType() != GaliyaraConst.SORT_BY_DATE) {
                GSorter.saveSortOption(this, GaliyaraConst.SORT_BY_DATE);
                initAlbum(albumName);
                Broadcaster.dataModified(this);
            }
            status = true;
        }
        if(mId == R.id.sortName) {
            if(GSorter.getSortType() != GaliyaraConst.SORT_BY_NAME) {
                GSorter.saveSortOption(this, GaliyaraConst.SORT_BY_NAME);
                initAlbum(albumName);
                Broadcaster.dataModified(this);
            }
            status = true;
        }
        if(mId == R.id.sortSize) {
            if(GSorter.getSortType() != GaliyaraConst.SORT_BY_SIZE) {
                GSorter.saveSortOption(this, GaliyaraConst.SORT_BY_SIZE);
                initAlbum(albumName);
                Broadcaster.dataModified(this);
            }
            status = true;
        }
        if(mId == R.id.sortAsc) {
            GSorter.saveSortOption(this,!GSorter.isSortAscending());
            initAlbum(albumName);
            Broadcaster.dataModified(this);
            status = true;
        }
        return status;
    }

    private void deleteSelected() {
        if(isInActionMode && !selectedImagePath.isEmpty()) {
            if(deleteDialog == null) {
                deleteDialog = new DeleteConfirmationDialog(this);
                deleteDialog.setActionListener(new DialogActionListener() {
                    @Override
                    public void onActionConfirmed() {
                        deleteActionConfirmed();
                        deleteDialog = null;
                    }
                    @Override
                    public void onActionDenied() { deleteDialog = null; }
                });
            }
            deleteDialog.show();
        }
    }

    @SuppressWarnings("unchecked")
    private void deleteActionConfirmed() {
        if(fileCopyMoveDelete == null)
            fileCopyMoveDelete = new FileCopyMoveDelete(this,COPY_MOVE_DELETE,this);
        fileCopyMoveDelete.execute(selectedImagePath);
    }

    private void shareSelectedImage() {
        ArrayList<String> list = new ArrayList<>(selectedImagePath);
        galiyaraHelper.shareSelectedImage(this,list,mHandler);
        deselectAllImages();
        updateTitleBar();
        adapter.notifyDataSetChanged();
    }

    @SuppressWarnings("unchecked")
    private void moveCopySelectedImage() {
        if(folderChooserDialog == null)
            folderChooserDialog = new FolderChooserDialog(this);
        folderChooserDialog.chooseFolder(folder -> {
            if(fileCopyMoveDelete == null)
                fileCopyMoveDelete = new FileCopyMoveDelete(this.getApplicationContext(),COPY_MOVE_DELETE,
                        this,folder+"/");
            fileCopyMoveDelete.execute(selectedImagePath);
            folderChooserDialog = null;
        });
    }

    @Override
    public void taskInitiated() {
        if(fileOperationDialog == null) {
            fileOperationDialog = new FileOperationDialog(this,selectedImagePath.size(),COPY_MOVE_DELETE);
            fileOperationDialog.setActionListener(new DialogActionListener() {
                @Override
                public void onActionConfirmed() { fileOperationDialog = null; }
                @Override
                public void onActionDenied() { fileOperationDialog = null; }
            });
        }
        fileOperationDialog.show();
    }
    @Override
    public void taskCompleted(int d) {
        if(fileOperationDialog != null)
            fileOperationDialog.taskFinished();
        if(COPY_MOVE_DELETE == GaliyaraConst.DELETE_FILE)
            deleteTaskFinished();
        if(COPY_MOVE_DELETE == GaliyaraConst.COPY_FILE)
            copyTaskFinished();
        if(COPY_MOVE_DELETE == GaliyaraConst.MOVE_FILE)
            moveTaskFinished();
        deselectAllImages();
        updateTitleBar();
        fileCopyMoveDelete = null;
    }

    @Override
    public void taskProgress(int d) {
        if(fileOperationDialog != null)
            fileOperationDialog.taskProgress(d);
    }

    private void deleteTaskFinished() {
        taskFinished();
    }
    private void moveTaskFinished() {
        taskFinished();
    }
    private void copyTaskFinished() {
        Broadcaster.dataModified(this);
    }

    private void taskFinished() {
        adapter.updateAdapter(selectedImagePath);
        allImageList.removeAll(selectedImagePath);
        deselectAllImages();
        updateTitleBar();
        Broadcaster.dataModified(this);
    }

    private void selectAllImages() {
        selectedImagePath.clear();
        selectedImagePath.addAll(allImageList);
        adapter.notifyDataSetChanged();
    }
    private void deselectAllImages() {
        selectedImagePath.clear();
        adapter.notifyDataSetChanged();
    }
}
