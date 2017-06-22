package cl.uchile.ing.adi.quicklooklib.fragments;


import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import cl.uchile.ing.adi.quicklooklib.R;

/**
 * Opens files when there is no fragment in charge to open them.
 */
public class DefaultFragment extends QuicklookFragment {

    /**
     * Shows basic data about the file, and an "Open with" button.
     */
    @Override
    public View createItemView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_default, container, false);
        ImageView fileimage = (ImageView) v.findViewById(R.id.icon);
        TextView filename = (TextView) v.findViewById(R.id.filename);
        TextView filetype = (TextView) v.findViewById(R.id.filetype);
        TextView filesize = (TextView) v.findViewById(R.id.filesize);
        filename.setText(item.getName());
        filetype.setText(item.getFormattedType());
        filesize.setText(item.getFormattedSize());
        fileimage.setImageResource(item.getImage());

        //Set listeners
        ImageButton openItem = (ImageButton) v.findViewById(R.id.open_item);
        ImageButton shareItem = (ImageButton) v.findViewById(R.id.share_item);
        ImageButton openDownloads = (ImageButton) v.findViewById(R.id.open_downloads);

        if( item.isOpenable() ) {
            openItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.openItem();
                }
            });
        } else {
            openItem.setVisibility(View.GONE);
        }
        shareItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.shareItem();
            }
        });

        openDownloads.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mListener.openDownloads();
            }

        });

        /*saveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void doClick(View v) {
                mListener.saveItem();
            }
        });
        */
        return v;
    }

}
