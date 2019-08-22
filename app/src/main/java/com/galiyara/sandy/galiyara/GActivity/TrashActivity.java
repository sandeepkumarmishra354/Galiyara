package com.galiyara.sandy.galiyara.GActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.galiyara.sandy.galiyara.GAdapter.TrashAdapter;
import com.galiyara.sandy.galiyara.GInterface.AdapterEventListener;
import com.galiyara.sandy.galiyara.GInterface.AsyncCallbackListener;
import com.galiyara.sandy.galiyara.GInterface.DialogActionListener;
import com.galiyara.sandy.galiyara.GDialogs.FileOperationDialog;
import com.galiyara.sandy.galiyara.GDialogs.TrashDialog;
import com.galiyara.sandy.galiyara.GEventBroadcaster.Broadcaster;
import com.galiyara.sandy.galiyara.GFileUtil.FileCopyMoveDelete;
import com.galiyara.sandy.galiyara.GFileUtil.MediaProvider;
import com.galiyara.sandy.galiyara.GHelper.GaliyaraHelper;
import com.galiyara.sandy.galiyara.GHelper.ThemeManager;
import com.galiyara.sandy.galiyara.GConstants.GaliyaraConst;
import com.galiyara.sandy.galiyara.R;

import java.util.ArrayList;

import io.multimoon.colorful.CAppCompatActivity;

public class TrashActivity extends CAppCompatActivity implements AsyncCallbackListener {

    private RecyclerView recyclerView;
    private TrashAdapter adapter;
    private TrashDialog dialog;
    private DialogActionListener listener;
    private AdapterEventListener adapterEventListener;
    private ActionBar actionBar;
    private Toolbar toolbar;
    private FileOperationDialog fileOperationDialog;
    private ArrayList<String> imgList = new ArrayList<>();
    private ArrayList<String> selectedList = new ArrayList<>();
    private FileCopyMoveDelete fileCopyMoveDelete;
    private boolean actionMode = false;
    private String currentClickedImg = "";
    private int RESTORE_DELETE;
    private MediaProvider mediaProvider;
    private Handler handler = new Handler();
    private Thread mThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GaliyaraHelper.changeLanguage(this);
        setContentView(R.layout.activity_trash);
        toolbar = findViewById(R.id.customToolbar);
        setSupportActionBar(toolbar);
        ThemeManager.setPopupTheme(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.trash_activity_title);
        }
        recyclerView = findViewById(R.id.trashRcView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        initMenuActionListener();
        setListener();
        loadTrashImages();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onDestroy() {
        freeMem();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else
            return super.onOptionsItemSelected(menuItem);
    }

    private void freeMem() {
        recyclerView = null;
        adapter = null;
        listener = null;
        adapterEventListener = null;
        dialog = null;
        actionBar = null;
        toolbar = null;
        fileOperationDialog = null;
        fileCopyMoveDelete = null;
        imgList.clear();
        selectedList.clear();
        imgList = null;
        selectedList = null;
    }

    private void loadTrashImages() {
        mThread = new Thread(() -> {
            if (mediaProvider == null)
                mediaProvider = new MediaProvider();
            ArrayList<String> tmp = mediaProvider.getTrashedImages();
            mediaProvider = null;
            if (tmp != null) {
                imgList.clear();
                imgList.addAll(tmp);
                tmp.clear();
            }
            handler.post(() -> {
                adapter = new TrashAdapter(TrashActivity.this, imgList, adapterEventListener);
                recyclerView.setAdapter(adapter);
            });
            mThread = null;
            handler = null;
        });
        mThread.start();
    }

    private void setListener() {
        listener = new DialogActionListener() {
            @Override
            public void onActionDismissed() {
                dialog = null;
            }

            @Override
            public void onActionDelete() {
                selectedList.clear();
                selectedList.add(currentClickedImg);
                deleteImage();
            }

            @Override
            public void onActionRestore() {
                selectedList.clear();
                selectedList.add(currentClickedImg);
                restoreImage();
            }
        };

        adapterEventListener = new AdapterEventListener() {
            @Override
            public void itemClicked(String path) {
                if (dialog == null)
                    dialog = new TrashDialog(TrashActivity.this, listener);
                dialog.show();
                currentClickedImg = path;
            }

            @Override
            public void itemLongClicked(String path) {
                actionMode = true;
                addToSelected(path);
                inflateActionMenu();
                currentClickedImg = "";
            }
        };
    }

    private void initMenuActionListener() {
        toolbar.setOnMenuItemClickListener(menuItem -> {
            boolean status = false;
            if (menuItem.getItemId() == R.id.sAllSelectedMenuItem) {
                selectDeselectAll();
                status = true;
            }
            if (menuItem.getItemId() == R.id.restoreMenuItem) {
                restoreImage();
                status = true;
            }
            if (menuItem.getItemId() == R.id.deleteSelectedMenuItem) {
                deleteImage();
                status = true;
            }
            return status;
        });
    }

    public void updateSelection(boolean status, String path) {
        if (status)
            addToSelected(path);
        else
            removeFromSelected(path);
    }

    private void inflateActionMenu() {
        toolbar.inflateMenu(R.menu.trash_selected_menu);
    }

    private void addToSelected(String path) {
        if (!selectedList.contains(path)) {
            selectedList.add(path);
            updateActionBar();
        }
    }

    private void removeFromSelected(String path) {
        selectedList.remove(path);
        updateActionBar();
    }

    @SuppressLint("DefaultLocale")
    private void updateActionBar() {
        if (actionBar != null) {
            if (selectedList.isEmpty()) {
                toolbar.getMenu().clear();
                actionBar.setTitle(R.string.trash_activity_title);
                actionMode = false;
            } else {
                String str = String.format("%d/%d", selectedList.size(), imgList.size());
                actionBar.setTitle(str);
            }
        }
    }

    private void selectDeselectAll() {
        if (selectedList.size() == imgList.size()) {
            deSelectAll();
        } else if (selectedList.size() < imgList.size()) {
            selectAll();
        }
        updateActionBar();
        adapter.notifyDataSetChanged();
    }

    @SuppressWarnings("unchecked")
    private void deleteImage() {
        RESTORE_DELETE = GaliyaraConst.DELETE_TRASH;
        if (fileCopyMoveDelete == null)
            fileCopyMoveDelete = new FileCopyMoveDelete(this.getApplicationContext(), RESTORE_DELETE, this);
        fileCopyMoveDelete.execute(selectedList);
    }

    @SuppressWarnings("unchecked")
    private void restoreImage() {
        RESTORE_DELETE = GaliyaraConst.RESTORE_TRASH;
        if (fileCopyMoveDelete == null)
            fileCopyMoveDelete = new FileCopyMoveDelete(this.getApplicationContext(), RESTORE_DELETE, this);
        fileCopyMoveDelete.execute(selectedList);
    }

    private void selectAll() {
        selectedList.clear();
        selectedList.addAll(imgList);
    }

    private void deSelectAll() {
        selectedList.clear();
        actionMode = false;
    }

    public boolean isSelected(String path) {
        return selectedList.contains(path);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean getActionMode() {
        return actionMode;
    }

    @Override
    public void taskInitiated() {
        if (fileOperationDialog == null) {
            fileOperationDialog = new FileOperationDialog(this,selectedList.size(),RESTORE_DELETE);
            fileOperationDialog.setActionListener(new DialogActionListener() {
                @Override
                public void onActionConfirmed() {
                    fileOperationDialog = null;
                }

                @Override
                public void onActionDenied() {
                    fileOperationDialog = null;
                }
            });
        }
        fileOperationDialog.show();
    }

    @Override
    public void taskProgress(int d) {
        if (fileOperationDialog != null)
            fileOperationDialog.taskProgress(d);
    }

    @Override
    public void taskCompleted(int d) {
        if (fileOperationDialog != null)
            fileOperationDialog.taskFinished();
        imgList.removeAll(selectedList);
        adapter.removeFromAdapter(selectedList);
        selectedList.clear();
        updateActionBar();
        fileCopyMoveDelete = null;
        Broadcaster.dataModified(this);
    }
}
