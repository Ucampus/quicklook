package cl.uchile.ing.adi.quicklooklib.fragments.items;

import java.io.File;
import java.util.ArrayList;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.AbstractFragment;
import cl.uchile.ing.adi.quicklooklib.fragments.FolderFragment;

/**
 * Represents a folder in the filesystem.
 */
public class FolderItem extends ListItem {

    public FolderItem(String path, String mimetype, String name, long size) {
        super(path,mimetype,name,size);
        image = R.drawable.folder;
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
            long size = AbstractItem.getSizeFromPath(path);
            String name = AbstractItem.getNameFromPath(path);
            files.add(ItemFactory.getInstance().createItem(path,mimetype,name,size));
        }
        return files;
    }

    @Override
    public String getSubTitle() {
        return this.getPath();
    }

    @Override
    public String getFormattedType() {
        return "Folder";
    }

    public static void onClick(AbstractFragment.OnListFragmentInteractionListener mListener,AbstractItem mItem) {
        mListener.onListFragmentInteraction(mItem);
    }
}
