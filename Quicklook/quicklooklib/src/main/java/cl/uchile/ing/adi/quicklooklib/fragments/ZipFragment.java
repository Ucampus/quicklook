package cl.uchile.ing.adi.quicklooklib.fragments;

import android.support.v7.widget.RecyclerView;

import cl.uchile.ing.adi.quicklooklib.fragments.adapters.ZipRecyclerViewAdapter;
import cl.uchile.ing.adi.quicklooklib.fragments.items.ZipItem;

/**
 * Shows the content of a zip file and allows to navigate inside it.
 */
public class ZipFragment extends FolderFragment {

    public ZipFragment() {
    }

    /**
     * Overriden getElements Method with zip adapter.
     * @return
     */
    @Override
    public RecyclerView.Adapter getElements() {
        return new ZipRecyclerViewAdapter(((ZipItem) this.item).getElements(), mListener);
    }

}
