package cl.uchile.ing.adi.quicklooklib.fragments.items;

import android.os.Bundle;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.DefaultFragment;

/**
 * Represents web content in the filesystem.
 */
public class WordItem extends FileItem {

    public WordItem(String path, String mimetype, String name, long size, Bundle extra) {
        super(path,mimetype,name,size, extra);
        image = R.drawable.word;
    }

    @Override
    protected void createFragment() {
        fragment = new DefaultFragment();
    }

    @Override
    public String getFormattedType() {
        return "MS Word Document";
    }
}
