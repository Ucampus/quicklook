package cl.uchile.ing.adi.quicklooklib.items;

import android.os.Bundle;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.DefaultFragment;

/**
 * Represents web content in the filesystem.
 */
public class ExcelItem extends FileItem {

    public ExcelItem(String path, String mimetype, long size, Bundle extra) {
        super(path,mimetype,size,extra);
        image = R.drawable.excel;
        formattedName = getContext().getString(R.string.items_excel_formatted_name);
    }

    @Override
    protected void createFragment() {
        fragment = new DefaultFragment();
    }

}
