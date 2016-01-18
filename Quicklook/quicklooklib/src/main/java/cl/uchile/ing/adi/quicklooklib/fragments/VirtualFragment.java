package cl.uchile.ing.adi.quicklooklib.fragments;

import android.support.v7.widget.RecyclerView;

import cl.uchile.ing.adi.quicklooklib.fragments.adapters.VirtualRecyclerViewAdapter;
import cl.uchile.ing.adi.quicklooklib.fragments.items.VirtualItem;

/**
 * Shows the content of a zip file and allows to navigate inside it.
 */
public class VirtualFragment extends FolderFragment {

    public VirtualFragment() {
    }

    /**
     * Overriden getElements Method with zip adapter.
     * @return
     */
    @Override
    public RecyclerView.Adapter getElements() {
        return (new VirtualRecyclerViewAdapter(((VirtualItem) item).getElements(), mListener));
    }

}
