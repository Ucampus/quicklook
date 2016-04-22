package cl.uchile.ing.adi.quicklooklib.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import cl.uchile.ing.adi.quicklooklib.R;
import cl.uchile.ing.adi.quicklooklib.items.PictureItem;

/**
 * Renders typical web resources using WebView.
 */
public class WebFragment extends QuicklookFragment {

    @Override
    public View createItemView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_web, container, false);
        WebView web = (WebView) v.findViewById(R.id.web_fragment);
        web.loadUrl("file://" + this.item.getPath());
        web.getSettings().setBuiltInZoomControls(true);
        if (item instanceof PictureItem) {
            web.getSettings().setSupportZoom(true);
            web.getSettings().setUseWideViewPort(true);
            web.getSettings().setLoadWithOverviewMode(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            web.getSettings().setDisplayZoomControls(false);
        }
        web.getSettings().setMinimumFontSize(13);
        return v;
    }

}
