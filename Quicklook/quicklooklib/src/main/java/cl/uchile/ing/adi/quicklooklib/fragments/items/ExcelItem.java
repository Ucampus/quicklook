package cl.uchile.ing.adi.quicklooklib.fragments.items;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.DefaultFragment;

/**
 * Represents web content in the filesystem.
 */
public class ExcelItem extends DefaultItem {

    public ExcelItem() {
    }

    public ExcelItem(String path, String mimetype) {
        super(path,mimetype);
    }

    public ExcelItem(String path, String mimetype, String name, long size) {
        super(path,mimetype,name,size);
    }

    @Override
    public int getImage() {
        return R.drawable.excel;
    }

    @Override
    public AbstractItem create(String path,String mimetype) {
        return new ExcelItem(path,mimetype);
    }

    @Override
    public AbstractItem create(String path, String mimetype, String name, long size) {
        return new ExcelItem(path,mimetype,name,size);
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
