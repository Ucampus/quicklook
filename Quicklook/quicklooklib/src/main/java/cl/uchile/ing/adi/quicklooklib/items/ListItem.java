package cl.uchile.ing.adi.quicklooklib.items;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import cl.uchile.ing.adi.quicklooklib.fragments.QuicklookFragment;

/**
 * Interface for listable items.
 */
public interface ListItem {
    ArrayList<BaseItem> getElements();
    RecyclerView.Adapter getAdapter(QuicklookFragment.OnListFragmentInteractionListener mListener);
}