package com.galiyara.sandy.galiyara.GActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import com.galiyara.sandy.galiyara.GAdapter.ImageFullViewAdapter;
import com.galiyara.sandy.galiyara.GDialogs.FolderChooserDialog;
import com.galiyara.sandy.galiyara.GInterface.DialogActionListener;
import com.galiyara.sandy.galiyara.GDatabase.GDBCrud;
import com.galiyara.sandy.galiyara.GDialogs.DeleteConfirmationDialog;
import com.galiyara.sandy.galiyara.GDialogs.ImageInfoDialog;
import com.galiyara.sandy.galiyara.GDialogs.RenameDialog;
import com.galiyara.sandy.galiyara.GDialogs.ResizeDialog;
import com.galiyara.sandy.galiyara.GEventBroadcaster.Broadcaster;
import com.galiyara.sandy.galiyara.GFileUtil.GFileUtils;
import com.galiyara.sandy.galiyara.GHelper.GSettings;
import com.galiyara.sandy.galiyara.GHelper.GaliyaraHelper;
import com.galiyara.sandy.galiyara.GHelper.ThemeManager;
import com.galiyara.sandy.galiyara.GConstants.GaliyaraConst;
import com.galiyara.sandy.galiyara.GImageCropper.CropImage;
import com.galiyara.sandy.galiyara.GImageCropper.CropImageView;
import com.galiyara.sandy.galiyara.R;
import com.github.chrisbanes.photoview.PhotoView;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import io.multimoon.colorful.CAppCompatActivity;

public class ImageViewActivity extends CAppCompatActivity {

    public ArrayList<String> imagePathList;
    private ViewPager viewPager;
    private String currentImagePath;
    private ImageFullViewAdapter imageFullViewAdapter;
    private Toolbar toolbar;
    private int COPY_MOVE;
    private DeleteConfirmationDialog deleteDialog;
    private RenameDialog renameDialog;
    private ImageInfoDialog imageInfoDialog;
    private FolderChooserDialog folderChooserDialog;
    private ResizeDialog resizeDialog;
    private GaliyaraHelper galiyaraHelper;
    private Handler handler = new Handler();
    private Timer slideShowTimer;
    private boolean createMenu = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GaliyaraHelper.changeLanguage(this);
        setContentView(R.layout.activity_image_view);
        toolbar = findViewById(R.id.customToolbar);
        setSupportActionBar(toolbar);
        ThemeManager.setPopupTheme(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        viewPager = findViewById(R.id.viewPager);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String mimeType = intent.getType();
        Uri data = intent.getData();
        String action = intent.getAction();

        if(action != null) {
            if (action.equals(Intent.ACTION_VIEW)) {
                if (mimeType != null) {
                    if (mimeType.contains("image")) {
                        if (data != null) {
                            imagePathList = new ArrayList<>();
                            currentImagePath = data.toString();
                            imagePathList.add(currentImagePath);
                            createMenu = false;
                        }
                    }
                }
            }
        } else if (bundle != null) {
            currentImagePath = bundle.getString(GaliyaraConst.IMAGE_PATH);
            imagePathList = bundle.getStringArrayList(GaliyaraConst.IMAGE_LIST);
        }
        int cPos=0;
        if (imagePathList != null && imagePathList.contains(currentImagePath)) {
            try {
                cPos = imagePathList.indexOf(currentImagePath);
            } catch (Exception e) {
                e.printStackTrace();
                cPos = 0;
            }
        }
        updateImageCount(cPos);
        imageFullViewAdapter = new ImageFullViewAdapter(this, imagePathList);
        viewPager.setAdapter(imageFullViewAdapter);
        viewPager.setCurrentItem(cPos);
        Animation animation = new ScaleAnimation(0.5f, 1f, 0.5f, 1f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setFillAfter(true);
        animation.setDuration(200);
        registerEventListener();
    }

    private void freeMemOnDestroy() {
        if(slideShowTimer != null) {
            slideShowTimer.cancel();
            slideShowTimer = null;
        }
        galiyaraHelper = null;
        imageFullViewAdapter = null;
        handler = null;
        viewPager = null;
        imagePathList.clear();
    }

    @Override
    public void onDestroy() {
        freeMemOnDestroy();
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(createMenu)
            getMenuInflater().inflate(R.menu.image_viewer_menu,menu);
        return true;
    }

    //@RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        final int mId = menuItem.getItemId();
        return menuItemClicked(mId);
    }

    @Override
    public void onBackPressed() {
        freeMemOnDestroy();
        super.onBackPressed();
        finish();
    }

    private void registerEventListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {
                updateImageCount(i);
            }

            @Override
            public void onPageScrolled(int a, float b, int c) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void updateImageCount(int i) {
        int currentImagePosition = i + 1;
        currentImagePath = imagePathList.get(i);
        Objects.requireNonNull(getSupportActionBar()).setTitle(String.format("%d/%d", currentImagePosition,
                imagePathList.size()));
    }

    private boolean menuItemClicked(int mId) {
        boolean status = false;
        if (mId == R.id.shareMenuItem) {
            shareImage();
            status = true;
        } else if (mId == R.id.deleteMenuItem) {
            deleteImage();
            status = true;
        } else if (mId == R.id.renameMenuItem) {
            renameImage();
            status = true;
        } else if (mId == R.id.moveMenuItem) {
            COPY_MOVE = GaliyaraConst.MOVE_FILE;
            moveCopySelectedImage();
            status = true;
        } else if (mId == R.id.copyMenuItem) {
            COPY_MOVE = GaliyaraConst.COPY_FILE;
            moveCopySelectedImage();
            status = true;
        } else if (mId == R.id.cropMenuItem) {
            cropImage();
            status = true;
        } else if (mId == R.id.infoMenuItem) {
            viewImageInfo();
            status = true;
        } else if (mId == R.id.rotateLMenuItem) {
            rotateImage(GaliyaraConst.ROTATE_LEFT);
            status = true;
        } else if (mId == R.id.rotateRMenuItem) {
            rotateImage(GaliyaraConst.ROTATE_RIGHT);
            status = true;
        } else if (mId == R.id.setAsMenuItem) {
            if(galiyaraHelper == null)
                galiyaraHelper = new GaliyaraHelper();
            galiyaraHelper.setImageAs(this,currentImagePath);
            status = true;
        }
        else if (mId == R.id.slideShowMenuItem) {
            startSlideShow();
            status = true;
        }
        else if (mId == R.id.stopSlideShowMenuItem) {
            stopSlideShow();
            status = true;
        }
        else if(mId == android.R.id.home) {
            onBackPressed();
            status = true;
        }
        else if (mId == R.id.resizeOption) {
            resizeImage();
            status = true;
        }
        return status;
    }

    private void resizeImage() {
        if(resizeDialog == null) {
            resizeDialog = new ResizeDialog(this, currentImagePath,
                    new DialogActionListener() {
                        @Override
                        public void onActionDismissed() {resizeDialog = null;}
                        @Override
                        public void onCustomResize(int w,int h) {
                            if(galiyaraHelper == null)
                                galiyaraHelper = new GaliyaraHelper();
                            galiyaraHelper.resizeImage(ImageViewActivity.this.getApplicationContext(),
                                    currentImagePath,w,h);
                        }
                        @Override
                        public void onFormResize() {
                            if(galiyaraHelper == null)
                                galiyaraHelper = new GaliyaraHelper();
                            galiyaraHelper.resizeImageToPassportSize(ImageViewActivity.this.getApplicationContext(),
                                    currentImagePath);
                        }
            });
        }
        resizeDialog.show();
    }

    private void cropImage() {
        CropImage.activity(Uri.fromFile(new File(currentImagePath)))
                .setActivityTitle("Crop Image")
                .setCropMenuCropButtonIcon(R.drawable.ic_done)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    private void rotateImage(int rotateDirection) {
        View itemView = viewPager.findViewWithTag(viewPager.getCurrentItem());
        PhotoView photoView = itemView.findViewById(R.id.fullScreenImageView);
        if (rotateDirection == GaliyaraConst.ROTATE_LEFT)
            photoView.animate().rotationBy(-90f).start();
        if (rotateDirection == GaliyaraConst.ROTATE_RIGHT)
            photoView.animate().rotationBy(90f).start();
    }

    private void renameImage() {
        if(renameDialog == null) {
            renameDialog = new RenameDialog(this,currentImagePath);
            renameDialog.setActionListener(new DialogActionListener() {
                @Override
                public void onActionConfirmed(String newName) {
                    renameConfirm(newName);
                    renameDialog = null;
                }
                @Override
                public void onActionDenied() { renameDialog = null; }
            });
        }
        renameDialog.show();
    }

    private void renameConfirm(String newName) {
        String oldName = GFileUtils.getNameWithoutExtension(currentImagePath);
        if (!newName.equals(oldName)) {
            String extension = "." + GFileUtils.getFileExtension(currentImagePath);
            String fileName = newName + extension;
            File originalFile = new File(currentImagePath);
            File newFile = new File(originalFile.getParent() + File.separator + fileName);
            if (!newFile.exists()) {
                if (originalFile.renameTo(newFile)) {
                    updateRenamedMedia(currentImagePath, newFile.getPath());
                    GaliyaraConst.showToast(this.getString(R.string.rename_success));
                } else
                    GaliyaraConst.showToast(this.getString(R.string.rename_fail));
            } else
                GaliyaraConst.showToast(this.getString(R.string.rename_fail));
        }
    }

    private void deleteImage() {
        if(deleteDialog == null) {
            deleteDialog = new DeleteConfirmationDialog(this);
            deleteDialog.setActionListener(new DialogActionListener() {
                @Override
                public void onActionConfirmed() {
                    boolean moveToTrash = GSettings.getGeneralSetting().getMoveTrash();
                    if(moveToTrash)
                        moveFileToTrash(currentImagePath);
                    else
                        deleteFilePermanently(currentImagePath);
                    deleteDialog = null;
                }
                @Override
                public void onActionDenied() { }
            });
        }
        deleteDialog.show();
    }

    private void moveFileToTrash(String imgPath) {
        String trashPath = GaliyaraConst.getTrashPath()+File.separator;
        File srcFile,destFile;
        srcFile = new File(imgPath);
        destFile = new File(trashPath+GFileUtils.getNameWithoutExtension(imgPath));
        if(srcFile.renameTo(destFile)) {
            updateDeletedMedia(imgPath);
            new GDBCrud(this).moveToTrash(imgPath);
            if(imagePathList.size() <= 0)
                finish();
            else
                imageFullViewAdapter.removeImageFromAdapter(imgPath);
        }
    }
    private void deleteFilePermanently(String img) {
        updateDeletedMedia(img);
        if(imagePathList.size() <= 0)
            finish();
        else
            imageFullViewAdapter.removeAndUpdateAdapter(img);
    }

    private void shareImage() {
        File file = new File(currentImagePath);
        Uri uri = FileProvider.getUriForFile(this, GaliyaraConst.APP_AUTHORITY, file);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        this.startActivity(shareIntent);
        /*TextExtractor.extractText(this, currentImagePath, new TextExtractListener() {
            @Override
            public void onExtracted(String text) {
                GaliyaraConst.logData(text);
            }
            @Override
            public void onExtractFail() {}
        });*/
    }

    private void moveCopySelectedImage() {
        if(folderChooserDialog == null)
            folderChooserDialog = new FolderChooserDialog(this);
        folderChooserDialog.chooseFolder(folder -> {
            if (COPY_MOVE == GaliyaraConst.MOVE_FILE)
                moveImage(folder + "/");
            if (COPY_MOVE == GaliyaraConst.COPY_FILE)
                copyImage(folder + "/");
            folderChooserDialog = null;
        });
    }

    @Override
    public void onActivityResult(int rqc, int rsc, Intent data) {
        if (rqc == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (rsc == RESULT_OK) {
                if(galiyaraHelper == null)
                    galiyaraHelper = new GaliyaraHelper();
                galiyaraHelper.saveCroppedImage(this,result,currentImagePath,handler);
            } else if (rsc == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                error.printStackTrace();
            }
        }
    }

    private void moveImage(String dest) {
        File originalFile = new File(currentImagePath);
        File destinationFile = new File(dest + originalFile.getName());
        if (!destinationFile.exists()) {
            if (originalFile.renameTo(destinationFile)) {
                imageFullViewAdapter.removeImageFromAdapter(originalFile.getPath());
                addNewFileToMediaScanner(destinationFile.getPath());
                updateDeletedMedia(originalFile.getPath());
                GaliyaraConst.showToast(this.getString(R.string.move_success));
            } else
                GaliyaraConst.showToast(this.getString(R.string.move_fail));
        } else
            GaliyaraConst.showToast(this.getString(R.string.move_fail));
    }

    private void copyImage(String dest) {
        File originalFile = new File(currentImagePath);
        File destinationFile = new File(dest + originalFile.getName());
        if (!destinationFile.exists()) {
            try {
                GFileUtils.copy(originalFile,destinationFile);
                addNewFileToMediaScanner(destinationFile.getPath());
                GaliyaraConst.showToast(this.getString(R.string.copy_success));
            } catch (Exception e) {
                GaliyaraConst.showToast(this.getString(R.string.copy_fail));
            }
        }
    }

    private void viewImageInfo() {
        if(imageInfoDialog == null) {
            imageInfoDialog = new ImageInfoDialog(this,currentImagePath,
                    new DialogActionListener() {
                        @Override
                        public void onActionConfirmed() { imageInfoDialog = null; }
                    });
        }
        imageInfoDialog.show();
    }

    private void startSlideShow() {
        if(slideShowTimer == null) {
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.slide_show_menu);
            slideShowTimer = new Timer();
            slideShowTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(handler != null)
                        handler.post(() -> {
                            int cr = viewPager.getCurrentItem();
                           if(cr == imagePathList.size()-1)
                               viewPager.setCurrentItem(0,true);
                           else
                               viewPager.setCurrentItem(++cr,true);
                        });
                }
            },3000,3000);
        }
    }
    private void stopSlideShow() {
        if(slideShowTimer != null) {
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.image_viewer_menu);
            slideShowTimer.cancel();
            slideShowTimer = null;
        }
    }

    public void imageDeleted() {
        int i = viewPager.getCurrentItem();
        if(i <= 0)
            i = 0;
        else
            i = i-1;
        updateImageCount(i);
    }

    private void updateRenamedMedia(String oldImg, String newImg) {
        //removes old media from provider
        Broadcaster.updateDeletedMedia(oldImg,this);
        //updates provider with new media
        Broadcaster.addNewFileToMediaScanner(newImg,this);
        Broadcaster.broadcastRenameEvent(this,oldImg, newImg);
        Broadcaster.dataModified(this);
        updateActivityAfterRename(oldImg, newImg);
    }

    public void updateDeletedMedia(String filePath) {
        Broadcaster.updateDeletedMedia(filePath, this);
        imagePathList.remove(filePath);
        if(imagePathList.size() > 0)
            imageDeleted();
        Broadcaster.broadcastDeleteEvent(this,filePath,GaliyaraConst.DELETE_FROM_IMAGE_VIEW);
        Broadcaster.dataModified(this);
    }

    private void addNewFileToMediaScanner(String filePath) {
        Broadcaster.addNewFileToMediaScanner(filePath, this);
        Broadcaster.broadcastDeleteEvent(this,filePath,GaliyaraConst.DELETE_FROM_IMAGE_VIEW);
        Broadcaster.dataModified(this);
    }

    private void updateActivityAfterRename(String oldImg, String newImg) {
        if (imagePathList.contains(oldImg)) {
            int pos = imagePathList.indexOf(oldImg);
            imagePathList.set(pos, newImg);
            currentImagePath = newImg;
            imageFullViewAdapter.updateAdapterAfterRename(oldImg, newImg);
        }
    }
}
