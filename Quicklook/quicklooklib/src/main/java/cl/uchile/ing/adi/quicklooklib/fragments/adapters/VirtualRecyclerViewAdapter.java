package cl.uchile.ing.adi.quicklooklib.fragments.adapters;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import cl.uchile.ing.adi.quicklooklib.fragments.QuicklookFragment.OnListFragmentInteractionListener;
import cl.uchile.ing.adi.quicklooklib.fragments.items.AItem;
import cl.uchile.ing.adi.quicklooklib.fragments.items.FileItem;
import cl.uchile.ing.adi.quicklooklib.fragments.items.VirtualItem;

/**
 * {@link RecyclerView.Adapter} that can display a {@link FileItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class VirtualRecyclerViewAdapter extends FolderRecyclerViewAdapter {

    public VirtualRecyclerViewAdapter(List<AItem> items, OnListFragmentInteractionListener listener) {
        super(items, listener);
    }

    public void clickAction(ViewHolder holder) {
        VirtualItem.onClick(mListener, holder.mItem);
    }

}
