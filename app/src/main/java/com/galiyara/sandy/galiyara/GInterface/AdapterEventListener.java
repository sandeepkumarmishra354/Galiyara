package com.galiyara.sandy.galiyara.GInterface;

@FunctionalInterface
public interface AdapterEventListener {
    void itemLongClicked(String path);
    default void itemClicked(String path){}
    default void itemClicked(String path,String albumName){}
}
