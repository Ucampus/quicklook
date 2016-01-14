package cl.uchile.ing.adi.quicklooklib.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cl.uchile.ing.adi.quicklooklib.fragments.items.AbstractItem;
import cl.uchile.ing.adi.quicklooklib.fragments.items.ItemFactory;
import cl.uchile.ing.adi.quicklooklib.fragments.items.ZipItem;

/**
 * Abstract Fragment defines the basic structure of the fragments managing the files.
 */
public abstract class AbstractFragment extends Fragment {

    protected AbstractItem item;
    protected OnListFragmentInteractionListener mListener;

    public AbstractFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b!=null) {
            String path = b.getString(AbstractItem.ITEM_PATH);
            String type = b.getString(AbstractItem.ITEM_TYPE);
            item = ItemFactory.getInstance().createItem(path,type);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.onListFragmentCreation(item);
    }

    @Override
    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Defines the item related to the fragment.
     * @param item Item related to fragment.
     */
    public void setItem(AbstractItem item) {
        this.item = item;
    }

    /**
     * This interface manages the interaction of the fragments and it's implemented
     * by Main Activity of the library.
     */
    public interface OnListFragmentInteractionListener {

        /**
         * Manages the interaction between the fragments, i.e. the transition between
         * them when exploring folders/zips/etc.
         * @param item the item which is going to be displayed.
         */
        void onListFragmentInteraction(AbstractItem item);

        /**
         * Updates the navbar text with the location of the file in the filesystem.
         * @param item the item which is going to be displayed.
         */
        void onListFragmentCreation(AbstractItem item);

        /**
         * Extracts a item inside a Compressed folder and opens it.
         * @param item the item which is going to be displayed.
         */
        void onListFragmentExtraction(ZipItem item);

        void onListFragmentPermissions(Runnable run);
    }
}
