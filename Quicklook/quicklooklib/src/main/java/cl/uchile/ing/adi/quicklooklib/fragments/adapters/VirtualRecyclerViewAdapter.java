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

    public VirtualRecyclerViewAdapter(List<AbstractItem> items, OnListFragmentInteractionListener listener) {
        super(items,listener);
    }

    public void clickAction(ViewHolder holder) {
        VirtualItem.onClick(mListener, holder.mItem);
    }

}
