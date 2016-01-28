package cl.uchile.ing.adi.quicklooklib.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cl.uchile.ing.adi.quicklooklib.items.BaseItem;
import cl.uchile.ing.adi.quicklooklib.ItemFactory;
import cl.uchile.ing.adi.quicklooklib.items.VirtualItem;

/**
 * Abstract Fragment defines the basic structure of the fragments managing the files.
 */
public abstract class QuicklookFragment extends Fragment {

    protected BaseItem item;
    protected OnListFragmentInteractionListener mListener;

    public QuicklookFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b!=null) {
            String path = b.getString(BaseItem.ITEM_PATH);
            String type = b.getString(BaseItem.ITEM_TYPE);
            long size = BaseItem.getSizeFromPath(path);
            Bundle extra = b.getBundle(BaseItem.ITEM_EXTRA);
            item = ItemFactory.getInstance().createItem(path,type,size,extra);
        }
        setHasOptionsMenu(true);
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
            throw new RuntimeException(context
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
    public void setItem(BaseItem item) {
        this.item = item;
    }

    public BaseItem getItem() {
        return this.item;
    }

    public void showError(String cause) {
        mListener.onListFragmentInfo(cause);
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
        void onListFragmentInteraction(BaseItem item);

        /**
         * Updates the navbar text with the location of the file in the filesystem.
         * @param item the item which is going to be displayed.
         */
        void onListFragmentCreation(BaseItem item);

        /**
         * Extracts a item inside a Compressed folder and opens it.
         * @param toRetrieve the item which is going to be displayed.
         * @param container item which contains toRetrieve.
         */
        void onListFragmentRetrieval(BaseItem toRetrieve, VirtualItem container);

        Uri saveItem();
        void openItem();
        void shareItem();

        /**
         * Retrieves the current fragment.
         */
        QuicklookFragment getFragment();

        void setFragment(QuicklookFragment fragment);

        void onListFragmentInfo(String message);

        void removeFromBackStack(QuicklookFragment frag);
    }
}
