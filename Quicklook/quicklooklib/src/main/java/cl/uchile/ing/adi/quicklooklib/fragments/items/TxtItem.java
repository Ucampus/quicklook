package cl.uchile.ing.adi.quicklooklib.fragments.items;

import android.os.Bundle;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.WebFragment;

/**
 * Represents web content in the filesystem.
 */
public class TxtItem extends FileItem {

    public TxtItem(String path, String mimetype, String id, long size,Bundle extra) {
        super(path,mimetype,id,size,extra);
        image = R.drawable.txt;
    }

    @Override
    protected void createFragment() {
        fragment = new WebFragment();
    }

    @Override
    public String getFormattedType() {
        return "Text file";
    }
}
