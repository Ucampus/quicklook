package cl.uchile.ing.adi.quicklook;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

public class DemoAssetFragment extends Fragment {
    private OnDemoAssetFragmentListener mListener;

    public DemoAssetFragment() {}

    @SuppressWarnings("unused")
    public static DemoAssetFragment newInstance() {
        DemoAssetFragment fragment = new DemoAssetFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demoasset_list, container, false);
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
        if (context instanceof OnDemoAssetFragmentListener) mListener = (OnDemoAssetFragmentListener) context;
        else throw new RuntimeException(context.toString() + " must implement OnDemoAssetFragmentListener");
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
