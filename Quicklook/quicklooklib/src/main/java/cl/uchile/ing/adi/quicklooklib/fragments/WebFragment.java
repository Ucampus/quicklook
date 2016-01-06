package cl.uchile.ing.adi.quicklooklib.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import cl.uchile.ing.adi.quicklooklib.R;

/**
 * Created by dudu on 04-01-2016.
 */
public class WebFragment extends DefaultFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_web, container, false);
        WebView web = (WebView) v.findViewById(R.id.web_fragment);
        web.loadUrl("file://"+this.file.getPath());
        web.getSettings().setBuiltInZoomControls(true);
        return v;
    }
}
