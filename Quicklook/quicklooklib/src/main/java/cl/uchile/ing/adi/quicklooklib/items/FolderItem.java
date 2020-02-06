package cl.uchile.ing.adi.quicklooklib.items;

import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import cl.uchile.ing.adi.quicklooklib.ItemFactory;
import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.QuicklookFragment;
import cl.uchile.ing.adi.quicklooklib.fragments.ListFragment;
import cl.uchile.ing.adi.quicklooklib.adapters.RecyclerViewAdapter;

/**
 * Represents a folder in the filesystem.
 */
public class FolderItem extends BaseItem implements IListItem {


    public FolderItem(String path, String mimetype, long size, Bundle extra, Context context) {
        super(path, mimetype, size, extra, context);
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
        return new RecyclerViewAdapter(elements, mListener);

    }

    @Override
    public String getSubTitle() {
        return this.getPath();
    }

    @Override
    public BaseItem doClick(QuicklookFragment.OnListFragmentInteractionListener mListener, BaseItem mItem) {
        return mItem;
    }

    /**
     * Creates an item for the list of items.
     * @param path Path of the item
     * @param type Type of the item
     * @param size Size of the item
     * @return item
     */
    public BaseItem createForList(String path, String type, long size, Bundle extra) {
        return ItemFactory.getInstance().createItem(path, type, size,extra, getContext());
    }

    @Override
    public boolean willShowOptionsMenu() {
        return false;
    }

    @Override
    public boolean openAsDefault() {
        return true;
    }
}
