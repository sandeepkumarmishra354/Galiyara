package com.galiyara.sandy.galiyara.GActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;

import com.galiyara.sandy.galiyara.GDialogs.FolderChooserDialog;
import com.google.android.material.navigation.NavigationView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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

import com.galiyara.sandy.galiyara.GAdapter.ImageListAdapter;
import com.galiyara.sandy.galiyara.GAlbumModel.Albums;
import com.galiyara.sandy.galiyara.GInterface.AdapterEventListener;
import com.galiyara.sandy.galiyara.GInterface.AsyncCallbackListener;
import com.galiyara.sandy.galiyara.GInterface.DialogActionListener;
import com.galiyara.sandy.galiyara.GDialogs.DeleteConfirmationDialog;
import com.galiyara.sandy.galiyara.GDialogs.FileOperationDialog;
import com.galiyara.sandy.galiyara.GEventBroadcaster.Broadcaster;
import com.galiyara.sandy.galiyara.GFileUtil.FileCopyMoveDelete;
import com.galiyara.sandy.galiyara.GFileUtil.GFileUtils;
import com.galiyara.sandy.galiyara.GFileUtil.MediaProvider;
import com.galiyara.sandy.galiyara.GHelper.GSettings;
import com.galiyara.sandy.galiyara.GHelper.GSorter;
import com.galiyara.sandy.galiyara.GHelper.GaliyaraHelper;
import com.galiyara.sandy.galiyara.GHelper.ThemeManager;
import com.galiyara.sandy.galiyara.GConstants.GaliyaraConst;
import com.galiyara.sandy.galiyara.R;

import java.util.ArrayList;
import java.util.Objects;

import io.multimoon.colorful.CAppCompatActivity;


public class MainActivity extends CAppCompatActivity implements AsyncCallbackListener {

    private boolean isInActionMode = false;
    private ArrayList<String> selectedAlbumName = new ArrayList<>();
    private ArrayList<String> selectedImagePath = new ArrayList<>();
    private Toolbar toolbar;
    private ImageListAdapter adapter;
    private Handler albumLoadHandler;
    private Handler mHandler;
    private ArrayList<Albums> imagePaths = new ArrayList<>();
    private int COPY_MOVE_DELETE;
    private DrawerLayout drawerLayout;
    private DeleteConfirmationDialog deleteDialog;
    private FileOperationDialog fileOperationDialog;
    private FolderChooserDialog folderChooserDialog;
    private FileCopyMoveDelete fileCopyMoveDelete;
    private MediaProvider GMediaProvider = new MediaProvider();
    private GaliyaraHelper galiyaraHelper;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BroadcastReceiver mDataModifiedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showAllAlbums();
        }
    };
    private Thread mThread;
    private AdapterEventListener adapterEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GaliyaraHelper.changeLanguage(this);
        setContentView(R.layout.activity_main);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        toolbar = findViewById(R.id.customToolbar);
        setSupportActionBar(toolbar);
        ThemeManager.setPopupTheme(toolbar);
        toolbar.setTitle(R.string.app_name);
        createDrawerNavigation();
        setNavListener();
        albumLoadHandler = new Handler();

        LocalBroadcastManager.getInstance(this).registerReceiver(mDataModifiedReceiver,
                new IntentFilter(GaliyaraConst.DATA_MODIFIED_FILTER));

        toolbar.setOnMenuItemClickListener(menuItem -> {
            final int mId = menuItem.getItemId();
            if(menuItem.getItemId() == R.id.sortMenuItem)
                ThemeManager.initSortOptions(toolbar);
            return menuItemClicked(mId);
        });
        swipeRefreshLayout.setOnRefreshListener(this::refreshAlbumList);
        mHandler = new Handler();
        GaliyaraHelper.setTrashedPath();
        GaliyaraHelper.setResizedPath();
        initListener();
        showAllAlbums();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isInActionMode() {return isInActionMode;}
    private void refreshAlbumList() {showAllAlbums();}

    private void freeMemOnDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDataModifiedReceiver);
        mDataModifiedReceiver = null;
        adapterEventListener = null;
        GMediaProvider = null;
        galiyaraHelper = null;
        fileCopyMoveDelete = null;
        imagePaths.clear();
        selectedAlbumName.clear();
        adapter = null;
        recyclerView = null;
        swipeRefreshLayout = null;
    }

    private void createDrawerNavigation() {
        drawerLayout = findViewById(R.id.drawer_layout);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
    }
    private void setNavListener() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            menuItem.setChecked(true);
            drawerLayout.closeDrawers();
            return menuItemClicked(menuItem.getItemId());
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if(menuItem.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        else
            return super.onOptionsItemSelected(menuItem);
    }
    @Override
    public void onDestroy() {
        freeMemOnDestroy();
        super.onDestroy();
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
            else if(fileOperationDialog == null && isInActionMode) {
                deselectAllAlbums();
                updateTitleBar();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu,menu);
        return true;
    }
    @Override
    public void onResume() {
        super.onResume();
        if(galiyaraHelper == null)
            galiyaraHelper = new GaliyaraHelper();
        if(GMediaProvider == null)
            GMediaProvider = new MediaProvider();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_album);
        if(recyclerView != null) {
            animateList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int rCode,@NonNull String[] permissions,@NonNull int[] grantResults) {
        super.onRequestPermissionsResult(rCode,permissions,grantResults);
        switch (rCode) {
            case GaliyaraConst.PERMISSION_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showAllAlbums();
                }
                else {
                    GaliyaraConst.showToast(getString(R.string.permission_denied));
                }
                break;
        }
    }

    private void initListener() {
        if(adapterEventListener == null) {
            adapterEventListener = new AdapterEventListener() {
                @Override
                public void itemClicked(String folderPath,String albumName) {
                    Intent intent = new Intent(MainActivity.this, AlbumsActivity.class);
                    intent.putExtra(GaliyaraConst.FOLDER_PATH,folderPath);
                    intent.putExtra(GaliyaraConst.ALBUM_NAME,albumName);
                    startActivity(intent);
                }
                @Override
                public void itemLongClicked(String path) {
                    isInActionMode = true;
                    selectedAlbumName.clear();
                    toolbar.getMenu().clear();
                    toolbar.inflateMenu(R.menu.image_selected_menu);
                    updateSelection(true,path);
                }
            };
        }
    }

    private void showAllAlbums() {
        if(Broadcaster.isReadExternalPermissionGranted(this)) {
            new Thread(() -> {
                imagePaths = GMediaProvider.loadAllAlbums(MainActivity.this);
                if(imagePaths.size() > 0) {
                    albumLoadHandler.post(this::selectFragment);
                    GSettings.setFirstTime(false);
                }
            }).start();
        }
    }

    private void selectFragment() {
        swipeRefreshLayout.setRefreshing(false);
        if(recyclerView == null) {
            recyclerView = findViewById(R.id.rcView);
            recyclerView.setLayoutManager(new GridLayoutManager(this,2));
            recyclerView.setHasFixedSize(true);
        }
        if(adapter == null) {
            RequestOptions options = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE).override(200,200).skipMemoryCache(true);
            final RequestManager glide = Glide.with(this);
            adapter = new ImageListAdapter(this,imagePaths,glide,options,adapterEventListener);
            recyclerView.setAdapter(adapter);
        }
        else
            adapter.refresh(imagePaths);
        animateList();
        GSettings.setFirstTime(false);
    }
    private void animateList() {
        recyclerView.scheduleLayoutAnimation();
    }

    public void updateSelection(boolean selected,String a) {
        if(selected)
            addToSelection(a);
        else
            removeFromSelection(a);
    }

    private void addToSelection(String str) {
        if(!selectedAlbumName.contains(str)) {
            selectedAlbumName.add(str);
            updateTitleBar();
        }
    }
    private void removeFromSelection(String str) {
        if(selectedAlbumName.remove(str))
            updateTitleBar();
    }

    public boolean isAlreadySelected(String img) {
        return selectedAlbumName.contains(img);
    }

    @SuppressLint("DefaultLocale")
    private void updateTitleBar() {
        if(selectedAlbumName.isEmpty()) {
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.sort_menu);
            isInActionMode = false;
            toolbar.setTitle(GaliyaraConst.APP_NAME);
        }
        else {
            String str = String.format("%d/%d",selectedAlbumName.size(),imagePaths.size());
            toolbar.setTitle(str);
        }
    }

    private boolean menuItemClicked(int mId) {
        boolean status = false;
        if(mId == R.id.deleteSelectedMenuItem) {
            if(!selectedAlbumName.isEmpty())
                COPY_MOVE_DELETE = GaliyaraConst.DELETE_FILE;
                deleteSelected();
            status = true;
        }
        if(mId == R.id.sAllSelectedMenuItem) {
            if(selectedAlbumName.size() == imagePaths.size())
                deselectAllAlbums();
            else
                selectAllAlbums();
            updateTitleBar();
            status = true;
        }
        if(mId == R.id.shareSelectedMenuItem) {
            if(!selectedAlbumName.isEmpty())
                shareSelectedImage();
            status = true;
        }
        if(mId == R.id.moveSelectedMenuItem) {
            if(isInActionMode && !selectedAlbumName.isEmpty()) {
                COPY_MOVE_DELETE = GaliyaraConst.MOVE_FILE;
                moveCopySelectedImage();
            }
            status = true;
        }
        if(mId == R.id.copySelectedMenuItem) {
            if(isInActionMode && !selectedAlbumName.isEmpty()) {
                COPY_MOVE_DELETE = GaliyaraConst.COPY_FILE;
                moveCopySelectedImage();
            }
            status = true;
        }
        if(mId == R.id.nav_about) {
            galiyaraHelper.showAboutActivity(this);
            status = true;
        }
        if(mId == R.id.nav_all_photo) {
            galiyaraHelper.showAllPhotosActivity(this);
            status = true;
        }
        if(mId == R.id.nav_settings) {
            galiyaraHelper.showSettingActivity(this);
            status = true;
        }
        if(mId == R.id.nav_trash) {
            galiyaraHelper.showTrashActivity(this);
            status = true;
        }
        if(mId == R.id.nav_policy) {
            galiyaraHelper.showPolicy(this);
            status = true;
        }
        if(mId == R.id.sortDate) {
            if(GSorter.getSortType() != GaliyaraConst.SORT_BY_DATE) {
                GSorter.saveSortOption(this, GaliyaraConst.SORT_BY_DATE);
                showAllAlbums();
            }
            status = true;
        }
        if(mId == R.id.sortName) {
            if(GSorter.getSortType() != GaliyaraConst.SORT_BY_NAME) {
                GSorter.saveSortOption(this, GaliyaraConst.SORT_BY_NAME);
                showAllAlbums();
            }
            status = true;
        }
        if(mId == R.id.sortSize) {
            if(GSorter.getSortType() != GaliyaraConst.SORT_BY_SIZE) {
                GSorter.saveSortOption(this, GaliyaraConst.SORT_BY_SIZE);
                showAllAlbums();
            }
            status = true;
        }
        if(mId == R.id.sortAsc) {
            GSorter.saveSortOption(this,!GSorter.isSortAscending());
            showAllAlbums();
            status = true;
        }
        return status;
    }

    @SuppressWarnings("unchecked")
    private void moveCopySelectedImage() {
        if(folderChooserDialog == null)
            folderChooserDialog = new FolderChooserDialog(this);
        folderChooserDialog.chooseFolder(folder -> {
            mThread = new Thread(() -> {
                selectedImagePath.clear();
                for(String a : selectedAlbumName)
                    selectedImagePath.addAll(GMediaProvider.getAllShownImagePath(MainActivity.this,a));
                albumLoadHandler.post(() -> {
                    fileCopyMoveDelete = null;
                    fileCopyMoveDelete = new FileCopyMoveDelete(MainActivity.this.getApplicationContext(),COPY_MOVE_DELETE
                            ,MainActivity.this,folder+"/");
                    fileCopyMoveDelete.execute(selectedImagePath);
                });
                mThread = null;
                folderChooserDialog = null;
            });
            mThread.start();
        });
    }

    @Override
    public void taskInitiated() {
        if(fileOperationDialog == null) {
            fileOperationDialog = new FileOperationDialog(this,selectedImagePath.size(),COPY_MOVE_DELETE);
            fileOperationDialog.setActionListener(new DialogActionListener() {
                @Override
                public void onActionConfirmed() { fileOperationDialog = null; }
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
            taskFinished();
        if(COPY_MOVE_DELETE == GaliyaraConst.MOVE_FILE)
            taskFinished();
    }
    @Override
    public void taskProgress(int d) {
        if(fileOperationDialog != null)
            fileOperationDialog.taskProgress(d);
    }

    private void deleteTaskFinished() {
        adapter.removeAlbum(selectedAlbumName);
        for(String a : selectedAlbumName) {
            for(Albums al : imagePaths) {
                String pth = GFileUtils.getFilePathOnly(al.getImagePath());
                if(pth.equals(a)) {
                    imagePaths.remove(al);
                    break;
                }
            }
        }
        deleteDialog = null;
        deselectAllAlbums();
        updateTitleBar();
    }

    private void taskFinished() {
        deselectAllAlbums();
        updateTitleBar();
        showAllAlbums();
    }

    private void deselectAllAlbums() {
        selectedAlbumName.clear();
        adapter.notifyDataSetChanged();
        GaliyaraConst.logData("DDDEESSSEEELLEECCTT "+selectedAlbumName.isEmpty());
    }
    private void selectAllAlbums() {
        selectedAlbumName.clear();
        for (Albums a : imagePaths)
            selectedAlbumName.add(GFileUtils.getFilePathOnly(a.getImagePath()));
        adapter.notifyDataSetChanged();
    }
    private void deleteSelected() {
        if(isInActionMode && !selectedAlbumName.isEmpty()) {
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
        mThread = new Thread(() -> {
            selectedImagePath.clear();
            for(String a : selectedAlbumName)
                selectedImagePath.addAll(GMediaProvider.getAllShownImagePath(MainActivity.this,a));
            albumLoadHandler.post(() -> {
                fileCopyMoveDelete = null;
                fileCopyMoveDelete = new FileCopyMoveDelete(MainActivity.this.getApplicationContext(),COPY_MOVE_DELETE,this);
                fileCopyMoveDelete.execute(selectedImagePath);
            });
            mThread = null;
        });
        mThread.start();
    }
    private void shareSelectedImage() {
        mThread = new Thread(() -> {
            ArrayList<String> allImages = new ArrayList<>();
            if(isInActionMode && !selectedAlbumName.isEmpty()) {
                for(String albumName : selectedAlbumName) {
                    allImages.addAll(GMediaProvider.getAllShownImagePath(MainActivity.this,albumName));
                }
            }
            mHandler.post(() -> {
                galiyaraHelper.shareSelectedImage(MainActivity.this,allImages,mHandler);
                deselectAllAlbums();
                updateTitleBar();
            });
            mThread = null;
        });
        mThread.start();
    }
}
