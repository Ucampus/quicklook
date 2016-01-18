package cl.uchile.ing.adi.quicklooklib.fragments.items;

import java.io.File;
import java.util.ArrayList;

import cl.uchile.ing.adi.quicklooklib.fragments.AbstractFragment;

/**
 * Created by dudu on 17-01-2016.
 */
public abstract class ListItem extends AbstractItem {

    public ListItem(String path, String mimetype, String name, long size) {
        super(path,mimetype,name,size);
    }

    public abstract ArrayList<AbstractItem> getElements();
    }
