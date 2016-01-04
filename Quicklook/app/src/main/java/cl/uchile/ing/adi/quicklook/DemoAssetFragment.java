package cl.uchile.ing.adi.quicklook;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cl.uchile.ing.adi.quicklook.dummy.DummyContent;
import cl.uchile.ing.adi.quicklook.dummy.DummyContent.DummyItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DemoAssetFragment extends Fragment {

    private int mColumnCount = 1;
    private OnDemoAssetFragmentListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DemoAssetFragment() {
    }

    @SuppressWarnings("unused")
    public static DemoAssetFragment newInstance() {
        DemoAssetFragment fragment = new DemoAssetFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demoasset_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            try {
                recyclerView.setAdapter(new DemoAssetRecyclerViewAdapter(getActivity().getAssets().list("files") , mListener));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDemoAssetFragmentListener) {
            mListener = (OnDemoAssetFragmentListener) context;
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

    public interface OnDemoAssetFragmentListener {
        void onAssetSelected(String item);
    }
}
