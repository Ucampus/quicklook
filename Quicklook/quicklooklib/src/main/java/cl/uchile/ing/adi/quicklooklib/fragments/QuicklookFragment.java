package cl.uchile.ing.adi.quicklooklib.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cl.uchile.ing.adi.quicklooklib.QuicklookActivity;
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        try {
            return createItemView(inflater,container,savedInstanceState);
        } catch (Exception e) {
            mListener.fragmentFallback(item);
            return null;
        }
    }

    public abstract View createItemView(LayoutInflater inflater, ViewGroup container,
                                        Bundle savedInstanceState);

    @Override
    public void onResume() {
        super.onResume();
        mListener.setCurrentFragment(item.getFragment());
        mListener.setCurrentItem(item);
        mListener.updateActionBar();
    }

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

    public BaseItem getItem() {
        return ((QuicklookActivity)getActivity()).getItem();
    }

    public void showError(String cause) {
        mListener.showInfo(cause);
        if(isAdded()) mListener.reportError(getItem(),this,cause);
    }

    /**
     * This interface manages the interaction of the fragments and it's implemented
     * by Main Activity of the library.
     */
    public interface OnListFragmentInteractionListener {
        /**
         * Updates the navbar text with the location of the file in the filesystem.
         */
        void updateActionBar();

        /**
         * Extracts a item inside a Compressed folder and opens it.
         * @param toRetrieve the item which is going to be displayed.
         * @param container item which contains toRetrieve.
         */
        BaseItem retrieveElement(BaseItem toRetrieve, VirtualItem container);

        Uri saveItem();
        void openItem();
        void shareItem();
        BaseItem getItem();

        /**
         * Retrieves the current fragment.
         */
        QuicklookFragment getFragment();

        void setCurrentFragment(QuicklookFragment fragment);

        void setCurrentItem(BaseItem item);

        void showInfo(String message);

        void reportError(BaseItem item, QuicklookFragment fragment, String description);

        void removeFromBackStack();

        void makeTransition(BaseItem mItem, boolean backstack);

        void fragmentFallback(BaseItem mItem);

        void changeFragment(BaseItem mItem, boolean backstack);
    }
}
