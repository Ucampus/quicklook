package cl.uchile.ing.adi.quicklooklib.items;

import android.content.Context;
import android.os.Bundle;

import cl.uchile.ing.adi.quicklooklib.R;

/**
 * Represents a PDF file in the filesystem.
 */
public class PDFItem extends FileItem {

    public PDFItem(String path, String mimetype,  long size, Bundle extra, Context context) {
        super(path,mimetype,size,extra, context);
        image = R.drawable.pdf;
        formattedName = getContext().getString(R.string.items_pdf_formatted_name);
    }

}
