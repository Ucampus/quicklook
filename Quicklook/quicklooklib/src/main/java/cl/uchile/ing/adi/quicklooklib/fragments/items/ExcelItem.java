package cl.uchile.ing.adi.quicklooklib.fragments.items;

import android.os.Bundle;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.DefaultFragment;

/**
 * Represents web content in the filesystem.
 */
public class ExcelItem extends FileItem {

    public ExcelItem(String path, String mimetype, String id, long size, Bundle extra) {
        super(path,mimetype,id,size,extra);
        image = R.drawable.excel;
    }

    @Override
    protected void createFragment() {
        fragment = new DefaultFragment();
    }

    @Override
    public String getFormattedType() {
        return "MS Excel Document";
    }
}
