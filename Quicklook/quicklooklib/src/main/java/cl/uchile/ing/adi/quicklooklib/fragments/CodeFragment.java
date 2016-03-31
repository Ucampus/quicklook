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
        //ws.setLoadWithOverviewMode(true);
        ws.setDisplayZoomControls(false);
        ws.setMinimumFontSize(8);

        String content = "";
        content += "<html><head>";
        content += "<link href=\"styles/default.css\" rel=\"stylesheet\" />";
        content += "<style type=\"text/css\">code { width:100%; background-color: #fff }</style>";
        content += "<script src=\"highlight.pack.js\"></script>";
        content += "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">";
        content += "</head><body>";

        try {
            InputStream is = new FileInputStream( item.getPath() );
            String c = IOUtils.toString(is, "UTF-8");
            is.close();
            content += "<pre><code>"+c.replace( "<", "&lt;" )+"</code></pre>";
        } catch (IOException ignored)  {}
        content += "<script>hljs.initHighlightingOnLoad();</script>";
        content += "</body></html>";

        web.loadDataWithBaseURL("file:///android_asset/", content, "text/html", "UTF-8", null);

        return v;
    }

}
