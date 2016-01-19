package cl.uchile.ing.adi.quicklooklib.fragments.adapters;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import cl.uchile.ing.adi.quicklooklib.fragments.AbstractFragment.OnListFragmentInteractionListener;
import cl.uchile.ing.adi.quicklooklib.fragments.items.AbstractItem;
import cl.uchile.ing.adi.quicklooklib.fragments.items.FolderItem;
import cl.uchile.ing.adi.quicklooklib.fragments.items.VirtualItem;
import cl.uchile.ing.adi.quicklooklib.fragments.items.DefaultItem;
import cl.uchile.ing.adi.quicklooklib.fragments.items.ItemFactory;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DefaultItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class VirtualRecyclerViewAdapter extends FolderRecyclerViewAdapter {

    public VirtualRecyclerViewAdapter(List<String[]> items, OnListFragmentInteractionListener listener) {
        super(items,listener);
    }

    public void clickAction(ViewHolder holder) {
        VirtualItem.onClick(mListener, holder.mItem);
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
        AbstractItem preItem = ItemFactory.getInstance().createItem(path, type, name, size);
        String anotherSep = preItem instanceof VirtualItem ? VirtualItem.SEP : "";
        return ItemFactory.getInstance().createItem(path + anotherSep, type, name, size);
    }

}
