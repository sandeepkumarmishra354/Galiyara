package com.galiyara.sandy.galiyara.GDialogs

import android.content.Context
import com.galiyara.sandy.galiyara.GInterface.FolderChooserListener

class FolderChooserDialog(var context: Context) {
    fun chooseFolder(listener: FolderChooserListener) {
        MaterialDialog(this).show {
            folderChooser { dialog, folder ->
                // Folder selected
            }
        }
    }
}