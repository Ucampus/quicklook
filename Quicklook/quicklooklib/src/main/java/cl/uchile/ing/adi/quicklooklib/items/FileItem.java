package cl.uchile.ing.adi.quicklooklib.items;

import android.content.Context;
import android.os.Bundle;

import java.io.File;

import cl.uchile.ing.adi.quicklooklib.ItemFactory;
import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.DefaultFragment;

/**
 * FileItem is the fallback item. It represents every item is not covered by
 * the library.
 */
public class FileItem extends BaseItem {

    public FileItem(String path, String mimetype, long size, Bundle extra, Context context) {
        super(path,mimetype,size,extra, context);
        addBannedWord("__MACOSX");
        image =  R.drawable.document;
        formattedName = getContext().getString(R.string.items_file_formatted_name);
        fragment = new DefaultFragment();
    }

    public static String loadFileType(File f) {
        if (f.isDirectory()) {
            return ItemFactory.FOLDER_MIMETYPE;
        } else {
            String path = f.getPath();
            return loadType(path);
        }

    }

}