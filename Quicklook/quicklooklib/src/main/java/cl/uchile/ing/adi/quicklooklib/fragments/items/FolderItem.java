package cl.uchile.ing.adi.quicklooklib.fragments.items;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.AbstractFragment;
import cl.uchile.ing.adi.quicklooklib.fragments.ListFragment;
import cl.uchile.ing.adi.quicklooklib.fragments.adapters.FolderRecyclerViewAdapter;

/**
 * Represents a folder in the filesystem.
 */
public class FolderItem extends AbstractItem implements ListItem {

    public FolderItem(String path, String mimetype, String name, long size) {
        super(path,mimetype,name,size);
        image = R.drawable.folder;
    }

    @Override
    protected void createFragment() {
        fragment =  new ListFragment();
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
            String type = AbstractItem.loadMimeType(path);
            long size = AbstractItem.getSizeFromPath(path);
            String name = AbstractItem.getNameFromPath(path);
            AbstractItem newItem = createForList(path,type,name,size);
            files.add(newItem);
        }
        return files;
    }

    @Override
    public RecyclerView.Adapter getAdapter(AbstractFragment.OnListFragmentInteractionListener mListener) {
        return new FolderRecyclerViewAdapter((this).getElements(), mListener);

    }

    @Override
    public AbstractItem retrieve(Context context) {
        return this;
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

    /**
     * Creates an item for the list of items.
     * @param path Path of the item
     * @param type Type of the item
     * @param name Name of the item
     * @param size Size of the item
     * @return item
     */
    public AbstractItem createForList(String path, String type, String name, long size) {
        return ItemFactory.getInstance().createItem(path, type, name, size);
    }
}
