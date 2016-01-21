package cl.uchile.ing.adi.quicklooklib.fragments.items;

import java.io.File;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.DefaultFragment;

/**
 * FileItem is the fallback item. It represents every item is not covered by
 * the library.
 */
public class FileItem extends AItem {

    public FileItem(String path, String mimetype, String id, long size) {
        super(path,mimetype,id,size);
        addBannedWord("__MACOSX");
        image =  R.drawable.document;
    }

    @Override
    protected void createFragment() {
        fragment = new DefaultFragment();
    }

    @Override
    public String getFormattedType() {
        return "File";
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