package cl.uchile.ing.adi.quicklooklib.items;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cl.uchile.ing.adi.quicklooklib.fragments.QuicklookFragment;

/**
 * Interface for listable items.
 */
public interface IListItem {
    ArrayList<BaseItem> getElements();
    RecyclerView.Adapter getAdapter(QuicklookFragment.OnListFragmentInteractionListener mListener,ArrayList<BaseItem> elements);
    BaseItem doClick(QuicklookFragment.OnListFragmentInteractionListener mListener, BaseItem mItem);
}