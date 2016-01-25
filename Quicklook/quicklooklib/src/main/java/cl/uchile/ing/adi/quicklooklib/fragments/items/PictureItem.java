package cl.uchile.ing.adi.quicklooklib.fragments.items;

import android.os.Bundle;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.WebFragment;

/**
 * Represents web content in the filesystem.
 */
public class PictureItem extends FileItem {

    public PictureItem(String path, String mimetype, long size, Bundle extra) {
        super(path,mimetype,size,extra);
        image = R.drawable.image;
    }

    @Override
    protected void createFragment() {
        fragment = new WebFragment();
    }

    @Override
    public String getFormattedType() {
        return "Picture";
    }
}
