package cl.uchile.ing.adi.quicklooklib.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.shockwave.pdfium.PdfView;
import com.shockwave.pdfium.listener.OnErrorOccurredListener;
import com.shockwave.pdfium.listener.OnPageChangedListener;

import java.io.File;

import cl.uchile.ing.adi.quicklooklib.R;

/**
 * Shows PDF files using PDFView library
 */
public class PdfFragment extends QuicklookFragment {

    PdfView pdfView;
    TextView pages;
    View v;

    public PdfFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=  inflater.inflate(R.layout.fragment_pdf, container, false);
        pages = (TextView) v.findViewById(R.id.pdf_pages);
        pdfView = (PdfView) v.findViewById(R.id.pdfview);
        ImageButton prevButton = (ImageButton) v.findViewById(R.id.back_button);
        ImageButton nextButton = (ImageButton) v.findViewById(R.id.forward_button);

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevPage(v);
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextPage(v);
            }
        });

        pdfView.fromUri(Uri.parse(item.getPath()))
                .onErrorOccured(new OnErrorOccurredListener() {
                    public void errorOccured() {
                        showError(getContext().getString(R.string.info_pdf_load_failed));
                    }
                }).onPageChanged(new OnPageChangedListener() {
                    @Override
                    public void pageChanged(int page, int pageCount) {
                        PdfFragment.this.updatePageCounter(page,pageCount);
                    }
                })
                .load();
        return v;
    }

    public void updatePageCounter(int page, int pageCount) {
        Log.d("Quicklook", "Page changed!");
        String actualPage = "" + page + "/" + pageCount;
        pages.setText(actualPage);
    }

    public void prevPage(View v) {
        pdfView.prevPage();
    }

    public void nextPage(View v) {
        pdfView.nextPage();
    }
}
