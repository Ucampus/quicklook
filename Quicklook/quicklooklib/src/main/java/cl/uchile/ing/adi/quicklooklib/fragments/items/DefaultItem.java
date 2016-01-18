package cl.uchile.ing.adi.quicklooklib.fragments.items;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.DefaultFragment;

/**
 * DefaultItem is the fallback item. It represents every item is not covered by
 * the library.
 */
public class DefaultItem extends AbstractItem {

    public DefaultItem() {
    }

    public DefaultItem(String path, String mimetype) {
        super(path,mimetype);
    }

    public DefaultItem(String path, String mimetype, String name, long size) {
        super(path,mimetype,name,size);
    }

    @Override
    public AbstractItem create(String path,String mimetype) {
        return new DefaultItem(path,mimetype);
    }

    @Override
    public AbstractItem create(String path, String mimetype, String name, long size) {
        return new DefaultItem(path,mimetype,name,size);
    }

    @Override
    public int getImage() {
        return R.drawable.document;
    }

    @Override
    protected void createFragment() {
        fragment = new DefaultFragment();
    }

    @Override
    public String getFormattedType() {
        return "File";
    }
}