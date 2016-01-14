package cl.uchile.ing.adi.quicklooklib.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import cl.uchile.ing.adi.quicklooklib.R;

/**
 * Created by dudu on 04-01-2016.
 */
public class WebFragment extends AbstractFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_web, container, false);
        WebView web = (WebView) v.findViewById(R.id.web_fragment);
        web.loadUrl("file://"+this.item.getPath());
        web.getSettings().setBuiltInZoomControls(true);
        return v;
    }
}