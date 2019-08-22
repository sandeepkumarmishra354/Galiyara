package com.galiyara.sandy.galiyara.GInterface;

import androidx.annotation.NonNull;

public interface DialogActionListener {
    default void onActionConfirmed(){}
    default void onActionConfirmed(String str){}
    default void onActionConfirmed(String str,boolean flag){}
    default void onActionDenied(){}
    default void onActionDismissed(){}
    default void onActionDelete(){}
    default void onActionRestore(){}
    default void onCustomResize(int w,int h){}
    default void onFormResize(){}
    default void onLangChanged(@NonNull String lang){}
}
