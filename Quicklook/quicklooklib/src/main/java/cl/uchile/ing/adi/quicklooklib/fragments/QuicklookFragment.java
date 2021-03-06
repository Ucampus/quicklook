package cl.uchile.ing.adi.quicklooklib.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import cl.uchile.ing.adi.quicklooklib.QuicklookActivity;
import cl.uchile.ing.adi.quicklooklib.R;
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
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        if(!isAdded()) return null;
        Bundle b = getArguments();
        if (b!=null) {
            String path = b.getString(BaseItem.ITEM_PATH);
            String type = b.getString(BaseItem.ITEM_TYPE);
            long size = BaseItem.getSizeFromPath(path);
            Bundle extra = b.getBundle(BaseItem.ITEM_EXTRA);
            if(TextUtils.isEmpty(path) || TextUtils.isEmpty(type) || size<0 || extra==null){
                Toast.makeText(getContext(), R.string.quicklook_error_opening, Toast.LENGTH_SHORT).show();
                getActivity().finish();
                return null;
            }
            item = ItemFactory.getInstance().createItem(path,type,size,extra, getActivity());
        }

        try {
            return createItemView(inflater,container,savedInstanceState);
        } catch (Exception e) {
            if(mListener!=null) mListener.fragmentFallback(item);
            return null;
        }
    }

    public abstract View createItemView(LayoutInflater inflater, ViewGroup container,
                                        Bundle savedInstanceState);

    @Override
    public void onResume() {
        super.onResume();
        if(mListener!=null && item!=null) {
            mListener.setCurrentFragment(item.getFragment());
            mListener.setCurrentItem(item);
            mListener.updateActionBar();
        }
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

        void openDownloads();

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
