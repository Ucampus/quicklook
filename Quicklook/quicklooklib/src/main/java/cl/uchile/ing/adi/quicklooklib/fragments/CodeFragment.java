package cl.uchile.ing.adi.quicklooklib.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
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

    private WebView web;
    private ProgressDialog pd;

    @Override
    public View createItemView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_web, container, false);
        web = (WebView) v.findViewById(R.id.web_fragment);
        WebSettings ws = web.getSettings();
        pd = ProgressDialog.show(getActivity(),
                getString(R.string.quicklook_loading_file),"Loading");
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
        content += "</head><body style=\"background: #f0f0f0\">";

        try {
            InputStream is = new FileInputStream( item.getPath() );
            String c = IOUtils.toString(is, "UTF-8");
            is.close();
            content += "<pre><code>"+c.replace( "<", "&lt;" )+"</code></pre>";
        } catch (IOException ignored)  {}
        content += "<script>(function() { hljs.initHighlighting();window.androidFunctions.dismissProgressDialog()})();</script>";
        content += "</body></html>";
        web.loadDataWithBaseURL("file:///android_asset/", content, "text/html", "UTF-8", null);
        //Add a JavaScriptInterface, so I can make calls from the web to Java methods
        web.addJavascriptInterface(new WebPageListener(), "androidFunctions");


        return v;
    }

    public class WebPageListener {
        @JavascriptInterface
        public void dismissProgressDialog(){
           getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pd.dismiss();
                }
            });
        }
    }

}
