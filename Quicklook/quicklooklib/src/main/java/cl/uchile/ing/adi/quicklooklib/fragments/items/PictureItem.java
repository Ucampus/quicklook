package cl.uchile.ing.adi.quicklooklib.fragments.items;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.WebFragment;

/**
 * Represents web content in the filesystem.
 */
public class PictureItem extends DefaultItem {

    public PictureItem() {
    }

    public PictureItem(String path, String mimetype) {
        super(path,mimetype);
    }

    public PictureItem(String path, String mimetype, String name, long size) {
        super(path,mimetype,name,size);
    }

    @Override
    public int getImage() {
        return R.drawable.image;
    }

    @Override
    public AbstractItem create(String path,String mimetype) {
        return new PictureItem(path,mimetype);
    }

    @Override
    public AbstractItem create(String path, String mimetype, String name, long size) {
        return new PictureItem(path,mimetype,name,size);
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