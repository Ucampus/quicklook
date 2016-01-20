package cl.uchile.ing.adi.quicklooklib.fragments.items;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.PdfFragment;

/**
 * Represents a PDF file in the filesystem.
 */
public class PDFItem extends FileItem {

    public PDFItem(String path, String mimetype, String id, long size) {
        super(path,mimetype,id,size);
        image = R.drawable.pdf;
    }

    @Override
    protected void createFragment() {
        fragment = new PdfFragment();
    }


    @Override
    public String getFormattedType() {
        return "PDF Document";
    }
}
