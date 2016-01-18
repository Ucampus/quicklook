package cl.uchile.ing.adi.quicklooklib.fragments.items;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.DefaultFragment;
import cl.uchile.ing.adi.quicklooklib.fragments.WebFragment;

/**
 * Represents web content in the filesystem.
 */
public class WordItem extends DefaultItem {

    public WordItem() {
    }

    public WordItem(String path, String mimetype) {
        super(path,mimetype);
    }

    public WordItem(String path, String mimetype, String name, long size) {
        super(path,mimetype,name,size);
    }

    @Override
    public int getImage() {
        return R.drawable.word;
    }

    @Override
    public AbstractItem create(String path,String mimetype) {
        return new WordItem(path,mimetype);
    }

    @Override
    public AbstractItem create(String path, String mimetype, String name, long size) {
        return new WordItem(path,mimetype,name,size);
    }

    @Override
    protected void createFragment() {
        fragment = new DefaultFragment();
    }

    @Override
    public String getFormattedType() {
        return "MS Word Document";
    }
}
