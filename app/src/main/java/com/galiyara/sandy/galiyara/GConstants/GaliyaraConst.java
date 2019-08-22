package com.galiyara.sandy.galiyara.GConstants;

import androidx.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;
import com.galiyara.sandy.galiyara.BuildConfig;
import com.galiyara.sandy.galiyara.GApp;

@SuppressWarnings("ALL")
public class GaliyaraConst {

    public static final int PASSPORT_WIDTH = 200;
    public static final int PASSPORT_HEIGHT = 230;
    public static final int DIALOG_SUCCESS = 999;
    public static final int COPY_FILE = 666;
    public static final int MOVE_FILE = 777;
    public static final int DELETE_FILE = 888;
    public static final int DELETE_TRASH = 783;
    public static final int RESTORE_TRASH = 214;
    public static final int RESTORE_FILE = 253;
    public static final int ROTATE_LEFT = 835;
    public static final int ROTATE_RIGHT = 673;
    public static final int RENAME_SUCCESS = 917;
    public static final int RENAME_FAIL = 343;
    public static final int PERMISSION_CODE = 3;
    public static final int IMAGE_ISO = 0;
    public static final int IMAGE_WIDTH = 1;
    public static final int IMAGE_HEIGHT = 2;
    public static final int IMAGE_DEVICE_MODEL = 3;
    public static final int IMAGE_COPYRIGHT = 4;
    public static final int IMAGE_DATETIME = 5;
    public static final int IMAGE_PIXEL_X = 6;
    public static final int IMAGE_PIXEL_Y = 7;
    public static final int IMAGE_CAPTURE_SOFTWARE = 8;
    public static final int IMAGE_TYPE = 9;
    public static final int IMAGE_NAME = 10;
    public static final int IMAGE_FULL_NAME = 11;
    public static final int IMAGE_PARENT_PATH = 12;
    public static final int IMAGE_FULL_PATH = 13;
    public static final int IMAGE_FULL_RESOLUTION = 14;
    public static final int ENABLE_HIDDEN_OPTION = 15;
    public static final int ENABLE_HQ_OPTION = 16;
    public static final int ENABLE_TRASH_OPTION = 17;
    public static final int ENABLE_LOCK_OPTION = 18;
    public static final int ENABLE_BIOMETRIC_OPTION = 91;
    public static final int ENABLE_IND_LOCK_OPTION = 19;
    public static final int ENABLE_DARK_THEME = 65;
    public static final int SET_PASSWORD = 89;
    public static final int LOCK_TYPE_BIOMETRIC = 53;
    public static final int LOCK_TYPE_PIN = 56;

    public static final boolean SORT_ASCENDING = false;
    public static final int SORT_BY_DATE = 22;
    public static final int SORT_BY_SIZE = 23;
    public static final int SORT_BY_NAME = 24;
    public static final int SORT_DEFAULT = SORT_BY_DATE;

    public static final int SET_PRIMARY_COLOR = 63;
    public static final int SET_ACCENT_COLOR = 67;
    public static final int APP_THEME_NOT_LOADED = -1;
    public static final int COLOR_RED = 1;
    public static final int COLOR_PINK = 2;
    public static final int COLOR_PURPLE = 3;
    public static final int COLOR_DEEP_PURPLE = 4;
    public static final int COLOR_INDIGO = 5;
    public static final int COLOR_BLUE = 6;
    public static final int COLOR_LIGHT_BLUE = 7;
    public static final int COLOR_CYAN = 8;
    public static final int COLOR_TEAL = 9;
    public static final int COLOR_GREEN = 10;
    public static final int COLOR_LIGHT_GREEN = 11;
    public static final int COLOR_LIME = 12;
    public static final int COLOR_YELLOW = 13;
    public static final int COLOR_AMBER = 14;
    public static final int COLOR_ORANGE = 15;
    public static final int COLOR_DEEP_ORANGE = 16;
    public static final int COLOR_BROWN = 17;
    public static final int COLOR_GREY = 18;
    public static final int COLOR_BLUE_GREY = 19;
    public static final int COLOR_WHITE = 20;
    public static final int COLOR_BLACK = 21;

    public static final String BUCKET_GROUP_BY = "1) GROUP BY 1,(2";
    public final static String BUCKET_ORDER_BY = "MAX(datetaken) DESC";
    public static final String ALL_IMAGES = "Show-All-Photos";
    public static final String TRASH_DIR_NAME = ".trash";
    public static final String RESIZE_DIR_NAME = "GResized";
    public static final String THEME_CHANGED = "ThemeChanged";
    public static final String FOLDER_PATH = "folder_path";
    public static final String ALBUM_NAME = "album_name";
    public static final String IMAGE_PATH = "image_path";
    public static final String IMAGE_LIST = "image_path_list";
    public static final String HIDDEN_OP_KEY = "Hidden-Key";
    public static final String HQ_OP_KEY = "Hq-Key";
    public static final String TRASH_OP_KEY = "Trash-Key";
    public static final String DARK_OP_KEY = "Dark-Key";
    public static final String MY_PREFS = "MyPrefs";
    public static final String PRIMARY_COLOR_KEY = "PrimaryColor-Key";
    public static final String ACCENT_COLOR_KEY = "AccentColor-Key";
    public static final String DARK_THEME_KEY = "DarkTheme-Key";
    public static final String SORT_TYPE_KEY = "Sort-Key";
    public static final String ASCENDING_KEY = "Ascending-Key";
    public static final String DATA_MODIFIED_FILTER = "DataModified";
    public static final String DELETE_FROM_IMAGE_VIEW = "PhotoDeletedFromFullView";
    public static final String PHOTO_RENAMED = "PhotoRenamed";
    public static final String NEW_PHOTO_ADDED = "NewPhotoAdded";
    public static final String MIME_TYPE = "mimeType";
    public static final String MIME_TYPE_IMG = "image/*";
    public static final String APP_NAME = "Galiyara";
    public static final String TAG = APP_NAME;
    public static final String LANG_ENGLISH = "en";
    public static final String LANG_HINDI = "hi";
    public static final String LANG_DEFAULT = LANG_ENGLISH;
    public static final String LANG_KEY = "Language-Key";
    public static final String GITHUB_LINK = "https://github.com/sandeepkumarmishra354/Galiyara";
    public static final String PRIVACY_POLICY_LINK = "https://sandeepkumarmishra4.wixsite.com/galiyara";
    public static final String APP_VERSION = "v"+ BuildConfig.VERSION_NAME;
    public static final String APP_AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";
    private static String trashPath = "";
    private static String resizedPath = "";

    public static void showToast(@NonNull String msg) {
        Toast toast = Toast.makeText(GApp.getApplication(),msg,Toast.LENGTH_LONG);
        toast.show();
    }
    public static void logData(@NonNull String msg) {
        if(BuildConfig.DEBUG)
            Log.d(TAG,msg);
    }

    public static void setTrashPath(String p) {trashPath = p;}
    public static void setResizedPath(String p) {resizedPath = p;}

    public static String getTrashPath() {return trashPath;}
    public static String getResizedPath() {return resizedPath;}
}
