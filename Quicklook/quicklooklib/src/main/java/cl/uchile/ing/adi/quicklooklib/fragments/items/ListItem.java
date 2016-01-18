package cl.uchile.ing.adi.quicklooklib.fragments.items;

import java.io.File;
import java.util.ArrayList;

import cl.uchile.ing.adi.quicklooklib.fragments.AbstractFragment;

/**
 * Created by dudu on 17-01-2016.
 */
public abstract class ListItem extends AbstractItem {

    public ListItem() {
    }

    public ListItem(String path,String mimetype) {
        super(path,mimetype);
    }

    public ListItem(String path, String mimetype, String name, long size) {
        super(path,mimetype,name,size);
    }

    /**
     * It doesn't calculate folders size.
     */
    @Override
    protected void getDataFromFile() {
        File file = new File(this.path);
        this.name = file.getName();
        this.size = -1;
    }

    public abstract ArrayList<AbstractItem> getElements();
    public abstract String getTitle();
    public abstract String getSubTitle();
    }
