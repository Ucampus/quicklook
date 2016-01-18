package cl.uchile.ing.adi.quicklooklib.fragments.items;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.PdfFragment;

/**
 * Represents a PDF file in the filesystem.
 */
public class PdfItem extends DefaultItem {

    public PdfItem() {
    }

    public PdfItem(String path,String mimetype) {
        super(path,mimetype);
    }

    public PdfItem(String path, String mimetype, String name, long size) {
        super(path,mimetype,name,size);
    }

    @Override
    public AbstractItem create(String path,String mimetype) {
        return new PdfItem(path,mimetype);
    }

    @Override
    public AbstractItem create(String path, String mimetype, String name, long size) {
        return new PdfItem(path,mimetype,name,size);
    }

    @Override
    public int getImage() {
        return R.drawable.pdf;
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
