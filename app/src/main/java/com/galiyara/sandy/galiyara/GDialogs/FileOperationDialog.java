package com.galiyara.sandy.galiyara.GDialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.galiyara.sandy.galiyara.GInterface.DialogActionListener;
import com.galiyara.sandy.galiyara.GConstants.GaliyaraConst;
import com.galiyara.sandy.galiyara.R;

public class FileOperationDialog {
    private Dialog popupWindow;
    private ProgressBar popupProgressBar;
    private TextView popupStatusTextView;
    private TextView headingView;
    private Button popupDoneButton;
    private Context context;
    private int totalSize;
    private String actionString;
    private String headingString;
    private DialogActionListener dialogActionListener;

    public FileOperationDialog(Context context,int totalSize,int opCode) {
        this.context = context;
        this.totalSize = totalSize;
        if(opCode == GaliyaraConst.COPY_FILE) {
            actionString = context.getString(R.string.copied);
            headingString = context.getString(R.string.menu_copy);
        }
        if(opCode == GaliyaraConst.MOVE_FILE) {
            actionString = context.getString(R.string.moved);
            headingString = context.getString(R.string.menu_move);
        }
        if(opCode == GaliyaraConst.DELETE_FILE || opCode == GaliyaraConst.DELETE_TRASH) {
            actionString = context.getString(R.string.deleted);
            headingString = context.getString(R.string.menu_delete);
        }
        if(opCode == GaliyaraConst.RESTORE_TRASH) {
            actionString = context.getString(R.string.restored);
            headingString = context.getString(R.string.restore);
        }
    }

    private void initPopupWindow() {
        if(popupWindow == null) {
            popupWindow = new Dialog(context);
            popupWindow.setContentView(R.layout.dialog_file_operation);
            popupWindow.setCancelable(false);
            popupDoneButton = popupWindow.findViewById(R.id.actionButton);
            popupDoneButton.setOnClickListener(v -> popupWindow.dismiss());
            popupWindow.setOnCancelListener(dialog -> {
                freeMem();
                if(dialogActionListener != null)
                    dialogActionListener.onActionConfirmed();
            });
            popupWindow.setOnDismissListener(dialog -> {
                freeMem();
                if(dialogActionListener != null)
                    dialogActionListener.onActionConfirmed();
            });
            popupStatusTextView = popupWindow.findViewById(R.id.statusText);
            headingView = popupWindow.findViewById(R.id.heading);
            popupProgressBar = popupWindow.findViewById(R.id.progressBar);
            popupProgressBar.setMax(totalSize);
            headingView.setText(headingString);
        }
    }
    public void show() {
        initPopupWindow();
        popupWindow.show();
    }
    private void freeMem() {
            popupStatusTextView = null;
            popupProgressBar = null;
            popupDoneButton = null;
            headingView = null;
            popupWindow = null;
    }
    public void setActionListener(DialogActionListener dialogActionListener) {
        this.dialogActionListener = dialogActionListener;
    }
    public void taskProgress(int d) {
        if(d <= totalSize) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                popupProgressBar.setProgress(d,true);
            else
                popupProgressBar.setProgress(d);
            @SuppressLint("DefaultLocale")
            String str = String.format("%s %d/%d",actionString,d,totalSize);
            popupStatusTextView.setText(str);
        }
    }
    public void taskFinished() {
        if(popupWindow != null)
            popupDoneButton.setVisibility(View.VISIBLE);
    }
    public void dismissDialog() {
        if(popupWindow != null)
            popupDoneButton.callOnClick();
    }
}
