/**
 * Copyright 2014 Joan Zapata
 *
 * This file is part of Android-pdfView.
 *
 * Android-pdfView is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Android-pdfView is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Android-pdfView.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.shockwave.pdfium;

import android.graphics.PointF;
import android.util.Log;

import com.shockwave.pdfium.util.DragPinchListener;
import com.shockwave.pdfium.util.DragPinchListener.OnDoubleTapListener;
import com.shockwave.pdfium.util.DragPinchListener.OnDragListener;
import com.shockwave.pdfium.util.DragPinchListener.OnPinchListener;

import static com.shockwave.pdfium.util.Constants.*;

/**
 * @author Joan Zapata
 *         This Manager takes care of moving the pdfView,
 *         set its zoom track user actions.
 */
class DragPinchManager implements OnDragListener, OnPinchListener, OnDoubleTapListener {

    private PdfView pdfView;

    private DragPinchListener dragPinchListener;

    private long startDragTime;

    private float startDragX;
    private float startDragY;

    public DragPinchManager(PdfView pdfView) {
        this.pdfView = pdfView;
        dragPinchListener = new DragPinchListener();
        dragPinchListener.setOnDragListener(this);
        dragPinchListener.setOnPinchListener(this);
        dragPinchListener.setOnDoubleTapListener(this);
        pdfView.setOnTouchListener(dragPinchListener);
    }


    @Override
    public void onPinch(float dr, PointF pivot) {
        if (DEBUG_MODE) Log.d("DragPinchManager", "Pinching");
        float wantedZoom = pdfView.getZoom() * dr;
        pdfView.zoomTo(dr, pivot);
    }
    @Override
    public void startDrag(float x, float y) {
        if (DEBUG_MODE) Log.d("DragPinchManager", "StartDrag");
        startDragTime = System.currentTimeMillis();
        startDragX = x;
        startDragY = y;
    }

    @Override
    public void onDrag(float dx, float dy) {
        if (DEBUG_MODE) Log.d("DragPinchManager", "Dragging: dx = "+dx+" | dy = "+dy);
        pdfView.moveRelative(dx, dy);
    }

    @Override
    public void endDrag(float x, float y) {
        if (DEBUG_MODE) Log.d("DragPinchManager", "End Drag");
        if (!isZoomed()) {
            if (DEBUG_MODE) Log.d("DragPinchManager","Changing page by flicking...");
            float distance;
            distance = x - startDragX;
            long time = System.currentTimeMillis() - startDragTime;
            int diff = distance > 0 ? -1 : +1;
            if (isQuickMove(distance, time) || isPageChange(distance)) {
                pdfView.goToPage((pdfView.getCurrentPage() - 1) + diff);
            }
        }
    }

    public boolean isZoomed() {
        return pdfView.isZoomed();
    }

    private boolean isPageChange(float distance) {
        return Math.abs(distance) > Math.abs((pdfView.getScreenRect().width()) / 2);
    }

    private boolean isQuickMove(float dx, long dt) {
        return Math.abs(dx) >= QUICK_MOVE_THRESHOLD_DISTANCE && //
                dt <= QUICK_MOVE_THRESHOLD_TIME;
    }

    @Override
    public void onDoubleTap(float x, float y) {
        if (DEBUG_MODE) Log.d("DragPinchManager", "DoubleTapping");
        if (isZoomed()) {
            pdfView.resetPageFit();
        }
    }
}
