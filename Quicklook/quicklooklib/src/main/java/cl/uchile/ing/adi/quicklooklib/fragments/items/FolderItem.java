package cl.uchile.ing.adi.quicklooklib.fragments.items;

import android.support.v7.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.AbstractFragment;
import cl.uchile.ing.adi.quicklooklib.fragments.FolderFragment;
import cl.uchile.ing.adi.quicklooklib.fragments.adapters.FolderRecyclerViewAdapter;

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
    public ArrayList<String[]> getElements() {
        File[] elements = new File(path).listFiles();
        ArrayList<String[]> files = new ArrayList<> ();
        for (File elem : elements) {
            String path = elem.getAbsolutePath();
            String type = AbstractItem.loadMimeType(path);
            long size = AbstractItem.getSizeFromPath(path);
            String name = AbstractItem.getNameFromPath(path);
            String[] newItem = {path,type,name,Long.toString(size)};
            //AbstractItem newItem = ItemFactory.getInstance().createItem(path, mimetype, name, size);
            files.add(newItem);
        }
        return files;
    }

    @Override
    public RecyclerView.Adapter getAdapter(AbstractFragment.OnListFragmentInteractionListener mListener) {
        return new FolderRecyclerViewAdapter((this).getElements(), mListener);

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
