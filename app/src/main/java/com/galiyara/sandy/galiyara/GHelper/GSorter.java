package com.galiyara.sandy.galiyara.GHelper;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import com.galiyara.sandy.galiyara.GAlbumModel.Albums;
import com.galiyara.sandy.galiyara.GConstants.GaliyaraConst;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.comparator.SizeFileComparator;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class GSorter {

    private static int sortType = GaliyaraConst.SORT_DEFAULT;
    private static boolean sortAscending = GaliyaraConst.SORT_ASCENDING;

    /**
     * sorts the given Album according to the type.
     * @param albums album to be sorted.
     **/
    public static void sortAlbums(@NonNull ArrayList<Albums> albums) {
        switch (sortType) {
            case GaliyaraConst.SORT_BY_DATE:
                sortAlbumsByDate(albums);
                break;
            case GaliyaraConst.SORT_BY_NAME:
                sortAlbumsByName(albums);
                break;
            case GaliyaraConst.SORT_BY_SIZE:
                sortAlbumsBySize(albums);
                break;
        }
    }
    public static void sortImages(ArrayList<String> images) {
        switch (sortType) {
            case GaliyaraConst.SORT_BY_DATE:
                sortImagesByDate(images);
                break;
            case GaliyaraConst.SORT_BY_NAME:
                sortImagesByName(images);
                break;
            case GaliyaraConst.SORT_BY_SIZE:
                sortImagesBySize(images);
                break;
        }
    }

    private static void sortAlbumsByDate(ArrayList<Albums> albums) {
        if(sortAscending)
            Collections.sort(albums,(a,b) -> {
                File f1 = new File(a.getImagePath());
                File f2 = new File(b.getImagePath());
                return LastModifiedFileComparator.LASTMODIFIED_COMPARATOR.compare(f1,f2);
            });
        else
            Collections.sort(albums,(a,b) -> {
                File f1 = new File(a.getImagePath());
                File f2 = new File(b.getImagePath());
                return LastModifiedFileComparator.LASTMODIFIED_REVERSE.compare(f1,f2);
            });
    }
    private static void sortImagesByDate(ArrayList<String> images) {
        if(sortAscending)
            Collections.sort(images,(a,b) -> {
                File f1 = new File(a);
                File f2 = new File(b);
                return LastModifiedFileComparator.LASTMODIFIED_COMPARATOR.compare(f1,f2);
        });
        else
            Collections.sort(images,(a,b) -> {
                File f1 = new File(a);
                File f2 = new File(b);
                return LastModifiedFileComparator.LASTMODIFIED_REVERSE.compare(f1,f2);
            });
    }
    private static void sortAlbumsByName(ArrayList<Albums> albums) {
        if(sortAscending)
            Collections.sort(albums,(a,b) -> Collections.reverseOrder()
                    .compare(b.getImageFolder(),
                            a.getImageFolder()));
        else
            Collections.sort(albums,(a,b) -> Collections.reverseOrder()
                    .compare(a.getImageFolder(),
                            b.getImageFolder()));
    }
    private static void sortImagesByName(ArrayList<String> images) {
        if(sortAscending)
            Collections.sort(images,String::compareToIgnoreCase);
        else
            Collections.sort(images,Collections.reverseOrder());
    }
    private static void sortAlbumsBySize(ArrayList<Albums> albums) {
        if(sortAscending)
            Collections.sort(albums,(a,b)-> a.getCount() - b.getCount());
        else
            Collections.sort(albums,(a,b)-> b.getCount() - a.getCount());
    }
    private static void sortImagesBySize(ArrayList<String> images) {
        if(sortAscending)
            Collections.sort(images,(a,b)-> {
                File f1 = new File(a);
                File f2 = new File(b);
                return SizeFileComparator.SIZE_COMPARATOR.compare(f1,f2);
        });
        else
            Collections.sort(images,(a,b)-> {
                File f1 = new File(a);
                File f2 = new File(b);
                return SizeFileComparator.SIZE_REVERSE.compare(f1,f2);
            });
    }

    public static int getSortType() {return sortType;}
    public static void setSortType(int st) {sortType = st;}
    public static boolean isSortAscending() {return sortAscending;}
    public static void setSortAscending(boolean sa) {sortAscending = sa;}

    public static void initSortOptions(@NonNull Context ctx) {
        SharedPreferences preferences = ctx.getSharedPreferences(GaliyaraConst.MY_PREFS,Context.MODE_PRIVATE);
        sortType = preferences.getInt(GaliyaraConst.SORT_TYPE_KEY,GaliyaraConst.SORT_DEFAULT);
        sortAscending = preferences.getBoolean(GaliyaraConst.ASCENDING_KEY,GaliyaraConst.SORT_ASCENDING);
    }
    public static void saveSortOption(@NonNull Context ctx,int type) {
        SharedPreferences preferences = ctx.getSharedPreferences(GaliyaraConst.MY_PREFS,Context.MODE_PRIVATE);
        preferences.edit()
                .putInt(GaliyaraConst.SORT_TYPE_KEY,type)
                .apply();
        setSortType(type);
    }
    public static void saveSortOption(@NonNull Context ctx,boolean asc) {
        SharedPreferences preferences = ctx.getSharedPreferences(GaliyaraConst.MY_PREFS,Context.MODE_PRIVATE);
        preferences.edit()
                .putBoolean(GaliyaraConst.ASCENDING_KEY,asc)
                .apply();
        setSortAscending(asc);
    }
}
