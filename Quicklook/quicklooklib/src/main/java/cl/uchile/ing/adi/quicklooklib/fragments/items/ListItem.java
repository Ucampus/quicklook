package cl.uchile.ing.adi.quicklooklib.fragments.items;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import cl.uchile.ing.adi.quicklooklib.fragments.AbstractFragment;

/**
 * Created by dudu on 17-01-2016.
 */
public interface ListItem {
    ArrayList<AbstractItem> getElements();
    RecyclerView.Adapter getAdapter(AbstractFragment.OnListFragmentInteractionListener mListener);
}