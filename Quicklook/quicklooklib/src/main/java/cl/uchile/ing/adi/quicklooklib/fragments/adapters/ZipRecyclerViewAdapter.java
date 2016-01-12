package cl.uchile.ing.adi.quicklooklib.fragments.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.fragments.AbstractFragment.OnListFragmentInteractionListener;
import cl.uchile.ing.adi.quicklooklib.fragments.items.AbstractItem;
import cl.uchile.ing.adi.quicklooklib.fragments.items.DefaultItem;
import cl.uchile.ing.adi.quicklooklib.fragments.items.ItemFactory;
import cl.uchile.ing.adi.quicklooklib.fragments.items.ZipItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DefaultItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class ZipRecyclerViewAdapter extends FolderRecyclerViewAdapter {

    public ZipRecyclerViewAdapter(List<AbstractItem> items, OnListFragmentInteractionListener listener) {
        super(items,listener);
    }

    public void clickAction(ViewHolder holder) {
        AbstractItem item = holder.mItem;
        String name = item.getName();
        String path = item.getPath();
        long size = item.getSize();
        String type = "application/zip";
        ZipItem newItem = (ZipItem)ItemFactory.getInstance().createItem(path, type, name, size);
        if (item.isFolder()) {
            mListener.onListFragmentInteraction(newItem);
        } else {
            mListener.onListFragmentExtraction(newItem);
        }
    }

}
