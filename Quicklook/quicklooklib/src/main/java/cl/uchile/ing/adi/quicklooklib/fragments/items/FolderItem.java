package cl.uchile.ing.adi.quicklooklib.fragments.items;

import java.io.File;
import java.util.ArrayList;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.FolderFragment;

/**
 * Represents a folder in the filesystem.
 */
public class FolderItem extends ListItem {

    public FolderItem() {
    }

    public FolderItem(String path,String mimetype) {
        super(path,mimetype);
    }

    public FolderItem(String path, String mimetype, String name, long size) {
        super(path,mimetype,name,size);
    }

    @Override
    public AbstractItem create(String path, String mimetype) {
        return new FolderItem(path,mimetype);
    }

    @Override
    public AbstractItem create(String path, String mimetype, String name, long size) {
        return new FolderItem(path,mimetype,name,size);
    }

    @Override
    public int getImage() {
        return R.drawable.folder;
    }

    @Override
    protected void createFragment() {
        fragment =  new FolderFragment();
    }

    @Override
    public boolean isFolder() {
        return true;
    }

    /**
     * Returns a list of items inside a folder
     * @return a list of items inside a folder.
     */
    public ArrayList<AbstractItem> getElements() {
        File[] elements = new File(path).listFiles();
        ArrayList<AbstractItem> files = new ArrayList<> ();
        for (File elem : elements) {
            String path = elem.getAbsolutePath();
            String mimetype = loadMimeType(path);
            files.add(ItemFactory.getInstance().createItem(path,mimetype));
        }
        return files;
    }

    public String getSubTitle() {
        return this.getPath();
    }

    @Override
    public String getFormattedType() {
        return "Folder";
    }
}
