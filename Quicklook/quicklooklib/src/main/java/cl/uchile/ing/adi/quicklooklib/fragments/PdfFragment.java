package cl.uchile.ing.adi.quicklooklib.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnErrorOccurredListener;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import java.io.File;

import cl.uchile.ing.adi.quicklooklib.R;

/**
 * Shows PDF files using PDFView library
 */
public class PdfFragment extends QuicklookFragment {

    PDFView pdfView;

    public PdfFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v =  inflater.inflate(R.layout.fragment_pdf, container, false);
        pdfView = (PDFView) v.findViewById(R.id.pdfview);
            pdfView.fromFile(new File(item.getPath()))
                    .defaultPage(0)
                    .showMinimap(false)
                    .enableSwipe(true)
                    .onErrorOccured(new OnErrorOccurredListener() {
                        public void errorOccured() {
                            showError(getContext().getString(R.string.pdf_load_failed));
                        }
                    }).onPageChange(new OnPageChangeListener() {
                @Override
                public void onPageChanged(int page, int pageCount) {
                    TextView pages = (TextView) v.findViewById(R.id.pdf_pages);
                    String actualPage = "" + page + "/" + pageCount;
                    pages.setText(actualPage);
                }
            })
                    .swipeVertical(true)
                    .load();
        return v;
    }
}
