package cl.uchile.ing.adi.quicklooklib.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.items.BaseItem;
import cl.uchile.ing.adi.quicklooklib.items.FolderItem;
import cl.uchile.ing.adi.quicklooklib.items.IListItem;

/**
 * Opens folders and lists the items inside them. There are extensions of this
 * class for showing elements inside compressed files.
 */
public class ListFragment extends QuicklookFragment {

    boolean visited = false;
    public ListFragment() {
    }

    /**
     * Defines the list view.
     */
    @Override
    public View createItemView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fileitem_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
                IListItem item = (IListItem) this.item;
                if (item!=null) {
                    Context context = view.getContext();
                    RecyclerView recyclerView = (RecyclerView) view;
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    ArrayList<BaseItem> elements = item.getElements();
                    //If there is only one folder, enter to it automatically.
                    if (elements.size() == 1 && (elements.get(0) instanceof FolderItem)) {
                        if (!visited) {
                            BaseItem nextItem = elements.get(0);
                            visited = true;
                            mListener.makeTransition(nextItem,true);
                            return view;
                        }
                    }
                    RecyclerView.Adapter adapter = item.getAdapter(mListener, elements);
                    recyclerView.setAdapter(adapter);
                }
        }
        return view;
    }
}
