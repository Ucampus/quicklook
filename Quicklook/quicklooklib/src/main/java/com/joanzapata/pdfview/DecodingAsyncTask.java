/**
 * Copyright 2014 Joan Zapata
 *
 * This file is part of Android-pdfview.
 *
 * Android-pdfview is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Android-pdfview is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Android-pdfview.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.joanzapata.pdfview;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.joanzapata.pdfview.PDFView;

import org.vudroid.core.DecodeService;
import org.vudroid.core.DecodeServiceBase;
import org.vudroid.pdfdroid.codec.PdfContext;

class DecodingAsyncTask extends AsyncTask<Void, Void, Boolean> {

    /** The decode service used for decoding the PDF */
    private DecodeService decodeService;

    private boolean cancelled;

    private Uri uri;

    private PDFView pdfView;

    private Context context;

    public DecodingAsyncTask(Uri uri, PDFView pdfView) {
        this.cancelled = false;
        this.pdfView = pdfView;
        this.uri = uri;
        this.context = pdfView.getContext();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        decodeService = new DecodeServiceBase(new PdfContext());
        decodeService.setContentResolver(context.getContentResolver());

        try {
            decodeService.open(uri);
        } catch (Exception e) {
            return true;
        }

        return false;
    }

    protected void onPostExecute(Boolean isErrorOccured) {

        if (isErrorOccured) {
            pdfView.errorOccurred();
        } else if (!isErrorOccured && !cancelled) {
            pdfView.loadComplete(decodeService);
        }
    }

    protected void onCancelled() {
        cancelled = true;
    }
}
