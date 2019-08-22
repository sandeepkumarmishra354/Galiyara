package com.galiyara.sandy.galiyara.GDialogs;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

import com.galiyara.sandy.galiyara.GInterface.DialogActionListener;
import com.galiyara.sandy.galiyara.GFileUtil.GFileUtils;
import com.galiyara.sandy.galiyara.GFileUtil.MediaProvider;
import com.galiyara.sandy.galiyara.GConstants.GaliyaraConst;
import com.galiyara.sandy.galiyara.R;

public class ImageInfoDialog {
    private Dialog mainDialog;
    private Button infoCloseButton;
    private Context context;
    private String currentImagePath;
    private DialogActionListener dialogActionListener;
    private MediaProvider GMediaProvider;
    private TextView imgPath,imgSize,imgRes,imgDevice,imgDate,imgIso,imgType;

    public ImageInfoDialog(Context context,String currentImagePath,DialogActionListener dialogActionListener) {
        this.context = context;
        this.currentImagePath = currentImagePath;
        this.dialogActionListener = dialogActionListener;
    }

    private void initInfoDialog() {
        if (mainDialog == null) {
            mainDialog = new Dialog(context);
            mainDialog.setContentView(R.layout.dialog_info);
            infoCloseButton = mainDialog.findViewById(R.id.infoCloseButton);
            infoCloseButton.setOnClickListener(v -> {
                mainDialog.dismiss();
                if(dialogActionListener != null)
                    dialogActionListener.onActionConfirmed();
            });
            mainDialog.setOnDismissListener(dialog -> freeMem());
            mainDialog.setOnCancelListener(dialog -> {
                freeMem();
                if(dialogActionListener != null)
                    dialogActionListener.onActionConfirmed();
            });
            if(GMediaProvider == null)
                GMediaProvider = new MediaProvider();
            setImageInfo(mainDialog);
            GMediaProvider = null;
        }
    }

    private void setImageInfo(Dialog popupView) {
        imgPath = popupView.findViewById(R.id.imgPath);
        imgSize = popupView.findViewById(R.id.imgSize);
        imgRes = popupView.findViewById(R.id.imgRes);
        imgDevice = popupView.findViewById(R.id.imgDevice);
        imgDate = popupView.findViewById(R.id.imgDate);
        imgIso = popupView.findViewById(R.id.imgISO);
        imgType = popupView.findViewById(R.id.imgType);

        imgPath.setText(currentImagePath);
        imgSize.setText(GFileUtils.getFileSize(currentImagePath));
        imgType.setText(GMediaProvider.getMediaInfoTag(currentImagePath, GaliyaraConst.IMAGE_TYPE));
        imgIso.setText(GMediaProvider.getMediaInfoTag(currentImagePath, GaliyaraConst.IMAGE_ISO));
        imgRes.setText(GMediaProvider.getMediaInfoTag(currentImagePath, GaliyaraConst.IMAGE_FULL_RESOLUTION));
        imgDevice.setText(GMediaProvider.getMediaInfoTag(currentImagePath, GaliyaraConst.IMAGE_DEVICE_MODEL));
        imgDate.setText(GMediaProvider.getMediaInfoTag(currentImagePath, GaliyaraConst.IMAGE_DATETIME));
    }

    private void freeMem() {
        imgDate = null;
        imgDevice = null;
        imgIso = null;
        imgPath = null;
        imgRes = null;
        imgSize = null;
        imgType = null;
        infoCloseButton = null;
        mainDialog = null;
    }

    public void show() {
        initInfoDialog();
        mainDialog.show();
    }
}
