package cl.uchile.ing.adi.quicklooklib.fragments.items;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.PdfFragment;

/**
 * Represents a PDF file in the filesystem.
 */
public class PDFItem extends DefaultItem {

    public PDFItem() {
    }

    public PDFItem(String path, String mimetype) {
        super(path,mimetype);
    }

    public PDFItem(String path, String mimetype, String name, long size) {
        super(path,mimetype,name,size);
    }

    @Override
    public AbstractItem create(String path,String mimetype) {
        return new PDFItem(path,mimetype);
    }

    @Override
    public AbstractItem create(String path, String mimetype, String name, long size) {
        return new PDFItem(path,mimetype,name,size);
    }

    @Override
    public int getImage() {
        return R.drawable.pdf;
    }

    @Override
    protected void createFragment() {
        fragment = new PdfFragment();
    }


}
