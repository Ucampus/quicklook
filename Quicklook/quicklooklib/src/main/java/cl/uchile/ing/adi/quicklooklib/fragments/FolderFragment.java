package cl.uchile.ing.adi.quicklooklib.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.adapters.FolderRecyclerViewAdapter;
import cl.uchile.ing.adi.quicklooklib.fragments.items.FolderItem;

/**
 * Opens folders and lists the items inside them. There are extensions of this
 * class for showing elements inside compressed files.
 */
public class FolderFragment extends AbstractFragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    public FolderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    /**
     * Defines the list view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fileitem_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(getElements());
        }
        return view;
    }

    /**
     * Returns the adapter with the elements to show.
     * @return an adapter with elements.
     */
    public RecyclerView.Adapter getElements() {
        return new FolderRecyclerViewAdapter(((FolderItem)this.item).getElements(), mListener);
    }
}
