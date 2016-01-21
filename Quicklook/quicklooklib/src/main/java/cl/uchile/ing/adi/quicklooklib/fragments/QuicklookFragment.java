package cl.uchile.ing.adi.quicklooklib.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cl.uchile.ing.adi.quicklooklib.fragments.items.AItem;
import cl.uchile.ing.adi.quicklooklib.fragments.items.ItemFactory;
import cl.uchile.ing.adi.quicklooklib.fragments.items.VirtualItem;

/**
 * Abstract Fragment defines the basic structure of the fragments managing the files.
 */
public abstract class QuicklookFragment extends Fragment {

    protected AItem item;
    protected OnListFragmentInteractionListener mListener;

    public QuicklookFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b!=null) {
            String path = b.getString(AItem.ITEM_PATH);
            String type = b.getString(AItem.ITEM_TYPE);
            long size = AItem.getSizeFromPath(path);
            String name = AItem.getNameFromPath(path);
            item = ItemFactory.getInstance().createItem(path,type,name,size);
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
    public void setItem(AItem item) {
        this.item = item;
    }

    public AItem getItem() {
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
        void onListFragmentInteraction(AItem item);

        /**
         * Updates the navbar text with the location of the file in the filesystem.
         * @param item the item which is going to be displayed.
         */
        void onListFragmentCreation(AItem item);

        /**
         * Extracts a item inside a Compressed folder and opens it.
         * @param toRetrieve the item which is going to be displayed.
         * @param container item which contains toRetrieve.
         */
        void onListFragmentRetrieval(AItem toRetrieve, VirtualItem container);

        /**
         * Retrieves the current fragment.
         */
        QuicklookFragment getFragment();

        void setFragment(QuicklookFragment fragment);

        void onListFragmentInfo(String message);
    }
}
