package com.galiyara.sandy.galiyara.GDialogs;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.galiyara.sandy.galiyara.GInterface.DialogActionListener;
import com.galiyara.sandy.galiyara.GFileUtil.MediaProvider;
import com.galiyara.sandy.galiyara.GConstants.GaliyaraConst;
import com.galiyara.sandy.galiyara.R;

public class ResizeDialog {

    private Context context;
    private String imageToResize;
    private DialogActionListener listener;
    private Dialog dialog;
    private Button okButton,cancelButton;
    private EditText widthEdit,heightEdit;
    private LinearLayout inputLayout;
    private AppCompatSpinner spinner;
    private TextInputLayout widthLayout,heightLayout;
    private MediaProvider mediaProvider;
    private boolean flag = false;
    private final int FORM_TYPE = 0;
    private final int CUSTOM_TYPE = 1;
    private int type = FORM_TYPE;

    public ResizeDialog(@NonNull Context ctx,@NonNull String img,DialogActionListener listener) {
        this.context = ctx;
        this.imageToResize = img;
        this.listener = listener;
        this.mediaProvider = new MediaProvider();
    }

    private void initDialog() {
        if(dialog == null) {
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_resize);
            dialog.setOnCancelListener(dialog -> {
                freeMem();
                if(listener != null)
                    listener.onActionDismissed();
            });
            dialog.setOnDismissListener(dialog -> {
                freeMem();
                if(listener != null)
                    listener.onActionDismissed();
            });
            initVars();
            flag = true;
        }
    }

    private void freeMem() {
        okButton = null;
        cancelButton = null;
        widthEdit = null;
        heightEdit = null;
        inputLayout = null;
        spinner = null;
        widthLayout = null;
        heightLayout = null;
        dialog = null;
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void initVars() {
        inputLayout = dialog.findViewById(R.id.inputLayout);
        okButton = dialog.findViewById(R.id.resizeOkBtn);
        cancelButton = dialog.findViewById(R.id.resizeCancelBtn);
        widthEdit = dialog.findViewById(R.id.chooseWidth);
        heightEdit = dialog.findViewById(R.id.chooseHeight);
        spinner = dialog.findViewById(R.id.resizeSpinner);
        widthLayout = dialog.findViewById(R.id.widthLayout);
        heightLayout = dialog.findViewById(R.id.heightLayout);

        okButton.setOnClickListener(v -> {
            if(type == FORM_TYPE) {
                if (listener != null)
                    listener.onFormResize();
                dialog.dismiss();
            }
            if(type == CUSTOM_TYPE) {
                if(isAllCorrect()) {
                    if(listener != null)
                        listener.onCustomResize(getWidth(),getHeight());
                    dialog.dismiss();
                }
            }
        });
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(flag) {
                    if(position == 0) {
                        inputLayout.setVisibility(View.GONE);
                        type = FORM_TYPE;
                    }
                    if(position == 1) {
                        inputLayout.setVisibility(View.VISIBLE);
                        type = CUSTOM_TYPE;
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        int WIDTH;
        int HEIGHT;
        int DEFAULT_WIDTH = 480;
        int DEFAULT_HEIGHT = 640;

        try {
            WIDTH = Integer.parseInt(mediaProvider.getMediaInfoTag(imageToResize, GaliyaraConst.IMAGE_WIDTH));
            HEIGHT = Integer.parseInt(mediaProvider.getMediaInfoTag(imageToResize, GaliyaraConst.IMAGE_HEIGHT));
            if(WIDTH == 0)
                WIDTH = DEFAULT_WIDTH;
            if(HEIGHT == 0)
                HEIGHT = DEFAULT_HEIGHT;
        } catch (Exception e) {
            e.printStackTrace();
            WIDTH = DEFAULT_WIDTH;
            HEIGHT = DEFAULT_HEIGHT;
        }
        mediaProvider = null;

        widthEdit.setText(String.valueOf(WIDTH));
        heightEdit.setText(String.valueOf(HEIGHT));
    }

    private boolean isAllCorrect() {

        if(widthEdit.getText().toString().isEmpty()) {
            widthLayout.setErrorEnabled(true);
            widthLayout.setError(context.getString(R.string.width_empty_error));
            return false;
        }
        if(heightEdit.getText().toString().isEmpty()) {
            heightLayout.setErrorEnabled(true);
            heightLayout.setError(context.getString(R.string.height_empty_error));
            return false;
        }

        if(Integer.parseInt(widthEdit.getText().toString()) <= 0) {
            widthLayout.setErrorEnabled(true);
            widthLayout.setError(context.getString(R.string.width_zero_error));
            return false;
        }
        if(Integer.parseInt(heightEdit.getText().toString()) <= 0) {
            heightLayout.setErrorEnabled(true);
            heightLayout.setError(context.getString(R.string.height_zero_error));
            return false;
        }

        return true;
    }
    private int getWidth() {
        return Integer.parseInt(widthEdit.getText().toString());
    }
    private int getHeight() {
        return Integer.parseInt(heightEdit.getText().toString());
    }

    public void show() {
        initDialog();
        dialog.show();
    }
}
