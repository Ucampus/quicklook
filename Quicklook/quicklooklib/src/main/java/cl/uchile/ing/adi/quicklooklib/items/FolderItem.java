package cl.uchile.ing.adi.quicklooklib.items;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import cl.uchile.ing.adi.quicklooklib.ItemFactory;
import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.QuicklookFragment;
import cl.uchile.ing.adi.quicklooklib.fragments.ListFragment;
import cl.uchile.ing.adi.quicklooklib.adapters.FolderRecyclerViewAdapter;

/**
 * Represents a folder in the filesystem.
 */
public class FolderItem extends BaseItem implements IListItem {


    public FolderItem(String path, String mimetype, long size, Bundle extra) {
        super(path, mimetype, size, extra);
        image = R.drawable.folder;
        formattedName = getContext().getString(R.string.items_folder_formatted_name);
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
        return files;
    }

    public RecyclerView.Adapter getAdapter(QuicklookFragment.OnListFragmentInteractionListener mListener, ArrayList<BaseItem> elements) {
        return new FolderRecyclerViewAdapter(elements, mListener);

    }

    @Override
    public String getSubTitle() {
        return this.getPath();
    }

    public static void onClick(QuicklookFragment.OnListFragmentInteractionListener mListener,BaseItem mItem) {
        mListener.onListFragmentInteraction(mItem);
    }

    public void onVirtualClick(QuicklookFragment.OnListFragmentInteractionListener mListener,BaseItem mItem) {
        FolderItem.onClick(mListener, mItem);
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
