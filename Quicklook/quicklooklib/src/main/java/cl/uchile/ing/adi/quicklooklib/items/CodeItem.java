package cl.uchile.ing.adi.quicklooklib.items;

import android.os.Bundle;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.CodeFragment;

/**
 * Represents web content in the filesystem.
 */
public class CodeItem extends FileItem {

    public CodeItem(String path, String mimetype, long size, Bundle extra) {
        super(path,mimetype,size,extra);
        image = R.drawable.txt;
        formattedName = getContext().getString(R.string.items_code_formatted_name);
        fragment = new CodeFragment();
    }
}

