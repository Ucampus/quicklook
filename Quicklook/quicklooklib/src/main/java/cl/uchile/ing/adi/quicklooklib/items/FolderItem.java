package cl.uchile.ing.adi.quicklooklib.items;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.QuicklookFragment;
import cl.uchile.ing.adi.quicklooklib.fragments.ListFragment;
import cl.uchile.ing.adi.quicklooklib.adapters.FolderRecyclerViewAdapter;

/**
 * Represents a folder in the filesystem.
 */
public class FolderItem extends BaseItem implements ListItem {


    public FolderItem(String path, String mimetype, long size, Bundle extra) {
        super(path, mimetype, size, extra);
        image = R.drawable.folder;
        formattedName = getContext().getString(R.string.items_folder_formatted_name);
    }



    @Override
    protected void createFragment() {
        fragment =  new ListFragment();
    }

    /**
     * Returns a list of items inside a folder
     * @return a list of items inside a folder.
     */
    public ArrayList<BaseItem> getElements() {
        File[] elements = new File(path).listFiles();
        ArrayList<BaseItem> files = new ArrayList<> ();
        for (File elem : elements) {
            if (!isBannedWord(elem.getAbsolutePath())) {
                String path = elem.getAbsolutePath();
                String type = FileItem.loadFileType(elem);
                long size = BaseItem.getSizeFromPath(path);
                Bundle extra = this.getExtra();
                BaseItem newItem = createForList(path, type, size, extra);
                files.add(newItem);
            }
        }
        Log.d("getElements: ", files.toString());
        return files;
    }

    public RecyclerView.Adapter getAdapter(QuicklookFragment.OnListFragmentInteractionListener mListener) {
        return new FolderRecyclerViewAdapter((this).getElements(), mListener);

    }

    @Override
    public String getSubTitle() {
        return this.getPath();
    }

    public static void onClick(QuicklookFragment.OnListFragmentInteractionListener mListener,BaseItem mItem) {
        mListener.onListFragmentInteraction(mItem);
    }

    /**
     * Creates an item for the list of items.
     * @param path Path of the item
     * @param type Type of the item
     * @param size Size of the item
     * @return item
     */
    public BaseItem createForList(String path, String type, long size, Bundle extra) {
        return ItemFactory.getInstance().createItem(path, type, size,extra);
    }
}
