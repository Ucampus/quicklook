package cl.uchile.ing.adi.quicklooklib.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
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
        final Handler h = new Handler();
        final Runnable hideCounter = new Runnable(){
            @Override
            public void run() {
                AlphaAnimation fadeOut = new AlphaAnimation( 1.0f , 0.0f ) ;
                pages.setAnimation(fadeOut);
                pages.startAnimation(fadeOut);
                pages.setVisibility(View.GONE);
            }
        };
        pdfView.fromUri(Uri.parse(item.getPath()))
                .onErrorOccured(new OnErrorOccurredListener() {
                    public void errorOccured() {
                        showError(getContext().getString(R.string.info_pdf_load_failed));
                    }
                }).onPageChanged(new OnPageChangedListener() {
            @Override
            public void pageChanged(int page, int pageCount) {
                h.removeCallbacks(hideCounter);
                sb.setProgress(page - 1);
                pages.setVisibility(View.VISIBLE);
                int progress = sb.getProgress();
                showPageCounter(sb,progress);
                h.postDelayed(hideCounter,2000);

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
        Log.d("SeekBar", "" + sb.getProgress() + " | " + sb.getMax());

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                showPageCounter(seekBar,seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                h.removeCallbacks(hideCounter);
                showPageCounter(seekBar,seekBar.getProgress());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                PdfFragment.this.goToPage(seekBar.getProgress());
            }
        });
        sb.setProgress(pdfView.getCurrentPage() - 1);

        if (pdfView.getPageCount()==1) sb.setVisibility(View.GONE);
        return v;
    }

    public void goToPage(int page) {
        pdfView.goToPage(page);
    }

    public void showPageCounter(SeekBar seekBar, int progress) {
        pages.setVisibility(View.VISIBLE);
        int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
        pages.setText("" + (progress + 1));
        if (pdfView.getZoom()==1) {
            pages.setY(val);
            pages.setX(pdfView.getWidth() - 6 * seekBar.getThumbOffset());
        } else {
            pages.setY(40);
            pages.setX(pdfView.getWidth() - 5 * seekBar.getThumbOffset());
        }
    }


}
