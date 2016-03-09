package cl.uchile.ing.adi.quicklooklib.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.shockwave.pdfium.PdfView;
import com.shockwave.pdfium.listener.OnErrorOccurredListener;
import com.shockwave.pdfium.listener.OnPageChangedListener;
import com.shockwave.pdfium.listener.OnZoomChangedListener;

import java.io.File;

import cl.uchile.ing.adi.quicklooklib.R;

/**
 * Shows PDF files using PDFView library
 */
public class PdfFragment extends QuicklookFragment {

    PdfView pdfView;
    TextView pages;
    View v;
    VerticalSeekBar sb;

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

        pdfView.fromUri(Uri.parse(item.getPath()))
                .onErrorOccured(new OnErrorOccurredListener() {
                    public void errorOccured() {
                        showError(getContext().getString(R.string.info_pdf_load_failed));
                    }
                }).onPageChanged(new OnPageChangedListener() {
            @Override
            public void pageChanged(int page, int pageCount) {
                if (sb.getVisibility()==View.VISIBLE)
                    sb.setProgress(page - 1);
            }
        }).onZoomChanged(new OnZoomChangedListener() {
            @Override
            public void zoomChanged(boolean isZoomed, float zoomLevel) {
                if (PdfFragment.this.pdfView.getPageCount() > 1) {
                    if (isZoomed) {
                        sb.setVisibility(View.GONE);
                    } else {
                        sb.setVisibility(View.VISIBLE);
                    }
                }
            }
        })
                .load();

        sb = (VerticalSeekBar) v.findViewById(R.id.seek_bar);
        sb.setMax(pdfView.getPageCount() - 1);
        sb.setProgress(pdfView.getCurrentPage() - 1);
        Log.d("SeekBar", "" + sb.getProgress() + " | " + sb.getMax());

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (b) {
                    pages.setVisibility(View.VISIBLE);
                }
                int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                pages.setText("" + (progress + 1));
                pages.setY(val);
                pages.setX(pdfView.getWidth() - 6 * seekBar.getThumbOffset());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("SB", "" + sb.getX() + " - " + sb.getY());
                Log.d("pages", "" + pages.getX() + " - " + pages.getY());

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pages.setVisibility(View.GONE);
                Log.d("pages", "" + pages.getX() + " - " + pages.getY());
                PdfFragment.this.goToPage(seekBar.getProgress());
            }
        });

        if (pdfView.getPageCount()==1) sb.setVisibility(View.GONE);
        return v;
    }

    public void goToPage(int page) {
        pdfView.goToPage(page);
    }



}
