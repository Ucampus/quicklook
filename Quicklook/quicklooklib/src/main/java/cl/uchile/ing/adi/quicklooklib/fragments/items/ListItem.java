package cl.uchile.ing.adi.quicklooklib.fragments.items;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import cl.uchile.ing.adi.quicklooklib.fragments.QuicklookFragment;

/**
 * Created by dudu on 17-01-2016.
 */
public interface ListItem {
    ArrayList<AItem> getElements();
    RecyclerView.Adapter getAdapter(QuicklookFragment.OnListFragmentInteractionListener mListener);
}