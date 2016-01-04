package cl.uchile.ing.adi.quicklooklib.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cl.uchile.ing.adi.quicklooklib.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DefaultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DefaultFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;


    public DefaultFragment() {
    }

    public static DefaultFragment newInstance(String param1, String param2) {
        DefaultFragment fragment = new DefaultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_default, container, false);
    }

}
