package cl.uchile.ing.adi.quicklooklib.fragments;


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

import cl.uchile.ing.adi.quicklooklib.R;

/**
 * Opens files when there is no fragment in charge to open them.
 */
public class DefaultFragment extends AbstractFragment {


    /**
     * Shows basic data about the file, and an "Open with" button.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_default, container, false);
        ImageView fileimage = (ImageView) v.findViewById(R.id.icon);
        TextView filename = (TextView) v.findViewById(R.id.filename);
        TextView filetype = (TextView) v.findViewById(R.id.filetype);
        TextView filesize = (TextView) v.findViewById(R.id.filesize);
        filename.setText(item.getName());
        filetype.setText(item.getType());
        filesize.setText(item.getFormattedSize());
        fileimage.setImageResource(item.getImage());
        return v;
    }

}
