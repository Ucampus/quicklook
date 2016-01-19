package cl.uchile.ing.adi.quicklooklib.fragments.items;

import android.content.Context;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.DefaultFragment;

/**
 * FileItem is the fallback item. It represents every item is not covered by
 * the library.
 */
public class FileItem extends AItem {

    public FileItem(String path, String mimetype, String name, long size) {
        super(path,mimetype,name,size);
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

}