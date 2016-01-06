package cl.uchile.ing.adi.quicklooklib.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import cl.uchile.ing.adi.quicklooklib.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DefaultFragment extends Fragment {
    protected static final String ARG_PATH = "path";

    protected FileItem file;
    protected OnListFragmentInteractionListener mListener;

    public DefaultFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String path = getArguments().getString(ARG_PATH);
            file = new FileItem(new File(path));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.onListFragmentCreation(file);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_default, container, false);
        ImageView fileimage = (ImageView) v.findViewById(R.id.icon);
        TextView filename = (TextView) v.findViewById(R.id.filename);
        TextView filetype = (TextView) v.findViewById(R.id.filetype);
        TextView filesize = (TextView) v.findViewById(R.id.filesize);
        Button filebutton = (Button) v.findViewById(R.id.open_with);
        filename.setText(file.getName());
        filetype.setText(file.getType());
        filesize.setText("" + file.getFile().length() + " bytes");
        fileimage.setImageResource(file.getImage());
        filebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(file.getPath()));
                intent.setType(file.getType());
                startActivity(Intent.createChooser(intent, "Abrir con:"));
            }
        });
        return v;
    }

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
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(FileItem item);
        void onListFragmentCreation(FileItem item);
    }

}
