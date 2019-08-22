package com.galiyara.sandy.galiyara.GInterface;

import com.galiyara.sandy.galiyara.GDatabase.GeneralSettingEntity;
import com.galiyara.sandy.galiyara.GDatabase.SecuritySettingEntity;
import com.galiyara.sandy.galiyara.GDatabase.TrashEntity;

@SuppressWarnings("unused")
public interface DBOperationListener {
    default void dataReady(SecuritySettingEntity entity) {}
    default void dataReady(GeneralSettingEntity entity) {}
    default void dataReady(TrashEntity entity) {}
}
