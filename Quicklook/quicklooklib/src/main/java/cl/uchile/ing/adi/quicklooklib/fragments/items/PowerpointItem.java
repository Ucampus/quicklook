package cl.uchile.ing.adi.quicklooklib.fragments.items;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.DefaultFragment;

/**
 * Represents web content in the filesystem.
 */
public class PowerpointItem extends DefaultItem {

    public PowerpointItem() {
    }

    public PowerpointItem(String path, String mimetype) {
        super(path,mimetype);
    }

    public PowerpointItem(String path, String mimetype, String name, long size) {
        super(path,mimetype,name,size);
    }

    @Override
    public int getImage() {
        return R.drawable.powerpoint;
    }

    @Override
    public AbstractItem create(String path,String mimetype) {
        return new PowerpointItem(path,mimetype);
    }

    @Override
    public AbstractItem create(String path, String mimetype, String name, long size) {
        return new PowerpointItem(path,mimetype,name,size);
    }

    @Override
    protected void createFragment() {
        fragment = new DefaultFragment();
    }

    @Override
    public String getFormattedType() {
        return "MS Powerpoint Document";
    }
}
