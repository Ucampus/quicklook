package cl.uchile.ing.adi.quicklooklib.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import cl.uchile.ing.adi.quicklooklib.R;

/**
 * Renders typical web resources using WebView.
 */
public class CodeFragment extends QuicklookFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_web, container, false);
        WebView web = (WebView) v.findViewById(R.id.web_fragment);
        WebSettings ws = web.getSettings();

        ws.setBuiltInZoomControls(true);
        ws.setJavaScriptEnabled(true);
        ws.setSupportZoom(true);
        ws.setDisplayZoomControls(false);
        ws.setLoadWithOverviewMode(true);
        ws.setMinimumFontSize(8);

        String type = item.getType();
        if( type.equals( "js" ) ) type = "javascript";

        String content = "";
        content += "<html><head>";
        content += "<link href=\"prism.css\" rel=\"stylesheet\" />";
        content += "</head><body>";
        content += "<style type=\"text/css\">code { background-color: #fff }</style>";
        content += "<script src=\"prism.js\"></script>";

        try {
            InputStream is = new FileInputStream( item.getPath() );
            String c = IOUtils.toString(is, "UTF-8");
            is.close();
            content += "<pre><code class=\"language-"+type+"\">"+c.replace( "<", "&lt;" )+"</code></pre>";
        } catch (IOException ignored)  {}
        content += "</body></html>";

        web.loadDataWithBaseURL("file:///android_asset/", content, "text/html", "UTF-8", null);

        return v;
    }

}
