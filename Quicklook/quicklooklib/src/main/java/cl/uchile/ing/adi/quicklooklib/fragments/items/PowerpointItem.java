package cl.uchile.ing.adi.quicklooklib.fragments.items;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.DefaultFragment;

/**
 * Represents web content in the filesystem.
 */
public class PowerpointItem extends FileItem {

    public PowerpointItem(String path, String mimetype, String name, long size) {
        super(path,mimetype,name,size);
        image = R.drawable.powerpoint;
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
