package cl.uchile.ing.adi.quicklooklib.fragments.items;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.QuicklookFragment;
import cl.uchile.ing.adi.quicklooklib.fragments.ListFragment;
import cl.uchile.ing.adi.quicklooklib.fragments.adapters.FolderRecyclerViewAdapter;

/**
 * Represents a folder in the filesystem.
 */
public class FolderItem extends FileItem implements ListItem {

    public FolderItem(String path, String mimetype, String name, long size) {
        super(path,mimetype,name,size);
        image = R.drawable.folder;
    }

    @Override
    protected void createFragment() {
        fragment =  new ListFragment();
    }

    /**
     * Returns a list of items inside a folder
     * @return a list of items inside a folder.
     */
    public ArrayList<AItem> getElements() {
        File[] elements = new File(path).listFiles();
        ArrayList<AItem> files = new ArrayList<> ();
        for (File elem : elements) {
            String path = elem.getAbsolutePath();
            String type = FileItem.loadFileMimeType(elem);
            long size = AItem.getSizeFromPath(path);
            String name = AItem.getNameFromPath(path);
            AItem newItem = createForList(path,type,name,size);
            files.add(newItem);
        }
        Log.d("getElements: ", files.toString());
        return files;
    }

    @Override
    public RecyclerView.Adapter getAdapter(QuicklookFragment.OnListFragmentInteractionListener mListener) {
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

    public static void onClick(QuicklookFragment.OnListFragmentInteractionListener mListener,AItem mItem) {
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
    public AItem createForList(String path, String type, String name, long size) {
        return ItemFactory.getInstance().createItem(path, type, name, size);
    }
}
