package com.shockwave.pdfium;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.shockwave.pdfium.listener.OnErrorOccurredListener;
import com.shockwave.pdfium.listener.OnLoadCompleteListener;
import com.shockwave.pdfium.listener.OnPageChangedListener;
import com.shockwave.pdfium.listener.OnZoomChangedListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cl.uchile.ing.adi.quicklooklib.R;

import static com.shockwave.pdfium.util.Constants.*;



public class PdfView extends SurfaceView {

    static int TOP_BORDER = 1;
    static int BOTTOM_BORDER = 2;
    static int BOTH_BORDERS = 3;
    static int NO_BORDER = 0;

    private Context c;

    private static final String TAG = PdfView.class.getName();

    private PdfiumCore mPdfCore;

    private PdfDocument mPdfDoc = null;
    private FileInputStream mDocFileStream = null;

    private DragPinchManager dragPinchManager;

    private int mCurrentPageIndex = 0;
    private int mPageCount = 0;

    private float zoom = MINIMUM_ZOOM;


    private SurfaceHolder mPdfSurfaceHolder;
    private boolean isSurfaceCreated = false;

    private final Rect mPageRect = new Rect();
    private final Rect mScreenRect = new Rect();
    private final Matrix mTransformMatrix = new Matrix();
    private boolean isZoomed = false;
    private boolean isRenderable = true;


    private AsyncTask loadingTask;


    private Runnable mRenderRunnable;

    private OnPageChangedListener onPageChangedListener;
    private OnZoomChangedListener onZoomChangedListener;
    private OnErrorOccurredListener onErrorOccurredListener;
    private OnLoadCompleteListener onLoadCompleteListener;

    ProgressDialog pd;


    public PdfView(final Context c,AttributeSet set) {
        super(c,set);
        this.c = c;
        mPdfCore = new PdfiumCore(c);
        dragPinchManager = new DragPinchManager(this);

        mRenderRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRenderable) {
                    loadPageIfNeed(mCurrentPageIndex);
                    if (PdfView.this.getZoom()<=1) {
                        resetPageFit();
                    }
                    /**mPreLoadPageWorker.submit(new Runnable() {
                        @Override
                        public void run() {
                            loadPageIfNeed(mCurrentPageIndex + 1);
                            loadPageIfNeed(mCurrentPageIndex - 1);
                            loadPageIfNeed(mCurrentPageIndex + 2);
                            loadPageIfNeed(mCurrentPageIndex - 2);
                        }
                    });**/
                }
            }
        };

        this.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (isRenderable) {
                    isSurfaceCreated = true;
                    updateSurface(holder);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (isRenderable) {
                    Log.w(TAG, "Surface Changed");
                    updateSurface(holder);
                    loadPage(mCurrentPageIndex,BOTH_BORDERS);
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.v(TAG, "Surface destroyed");
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isSurfaceCreated = false;
        recycle();
        Log.w(TAG, "Surface detached");
    }

    private void loadDocument(Uri fileUri) {
        try{
            mDocFileStream = new FileInputStream(fileUri.getPath());

            mPdfDoc = mPdfCore.newDocument(mDocFileStream.getFD());
            if (DEBUG_MODE) Log.d("Main", "Open Document");
            if (mPdfDoc.mNativeDocPtr==-1) {
                throw new IOException();
            }
            mPageCount = mPdfCore.getPageCount(mPdfDoc);
            if (DEBUG_MODE) Log.d(TAG, "Page Count: " + mPageCount);
            if (onLoadCompleteListener!=null) {
                onLoadCompleteListener.loadComplete(mPageCount);
            }
        }catch(IOException e) {
            e.printStackTrace();
            if (DEBUG_MODE) Log.e("Main", "Data uri: " + fileUri.toString());
            if (onErrorOccurredListener != null) {
                onErrorOccurredListener.errorOccured();
            }
            this.setBackgroundColor(Color.parseColor("#FFFFFF"));
            isRenderable = false;
            mDocFileStream = null;
            mPdfDoc = null;
        }
    }

    private void loadPageIfNeed(final int pageIndex){
        if( pageIndex >= 0 && pageIndex < mPageCount && !mPdfDoc.hasPage(pageIndex) ){
            if (DEBUG_MODE) Log.d(TAG, "Load page: " + pageIndex);
            mPdfCore.openPage(mPdfDoc, pageIndex);
        }
    }

    private void updateSurface(SurfaceHolder holder){
        mPdfSurfaceHolder = holder;
        mScreenRect.set(holder.getSurfaceFrame());
    }

    protected void setPagePosition(int top, int left) {
        int width = mPageRect.width();
        int height = mPageRect.height();
        mPageRect.top = top;
        mPageRect.bottom = top + height;
        mPageRect.left = left;
        mPageRect.right = left + width;
        render();
    }

    public void goToTop() {
        setPagePosition(0, mPageRect.left);
    }

    public void goToBottom() {
        int bottom = mPdfSurfaceHolder.getSurfaceFrame().height();
        setPagePosition(bottom - mPageRect.height(), mPageRect.left);
    }

    protected void resetPageFit(){
        int pageIndex = mCurrentPageIndex;
        float pageWidth = mPdfCore.getPageWidth(mPdfDoc, pageIndex);
        float pageHeight = mPdfCore.getPageHeight(mPdfDoc, pageIndex);
        float screenWidth = mPdfSurfaceHolder.getSurfaceFrame().width();
        float screenHeight = mPdfSurfaceHolder.getSurfaceFrame().height();

        /**Portrait**/
        if(screenWidth < screenHeight){
            if( (pageWidth / pageHeight) < (screenWidth / screenHeight) ){
                //Situation one: fit height
                pageWidth *= (screenHeight / pageHeight);
                pageHeight = screenHeight;

                mPageRect.top = 0;
                mPageRect.left = (int)(screenWidth - pageWidth) / 2;
                mPageRect.right = (int)(mPageRect.left + pageWidth);
                mPageRect.bottom = (int)pageHeight;
            }else{
                //Situation two: fit width
                pageHeight *= (screenWidth / pageWidth);
                pageWidth = screenWidth;

                mPageRect.left = 0;
                mPageRect.top = (int)(screenHeight - pageHeight) / 2;
                mPageRect.bottom = (int)(mPageRect.top + pageHeight);
                mPageRect.right = (int)pageWidth;
            }
        }else{
            if( pageWidth > pageHeight ){
                //Situation one: fit height
                pageWidth *= (screenHeight / pageHeight);
                pageHeight = screenHeight;

                mPageRect.top = 0;
                mPageRect.left = (int)(screenWidth - pageWidth) / 2;
                mPageRect.right = (int)(mPageRect.left + pageWidth);
                mPageRect.bottom = (int)pageHeight;
            }else{
                //Situation two: fit width
                pageHeight *= (screenWidth / pageWidth);
                pageWidth = screenWidth;

                mPageRect.left = 0;
                mPageRect.top = 0;
                mPageRect.bottom = (int)(mPageRect.top + pageHeight);
                mPageRect.right = (int)pageWidth;
            }
        }

        isZoomed = false;
        zoom = MINIMUM_ZOOM;
        render();
        if (onZoomChangedListener !=null) {
            onZoomChangedListener.zoomChanged(isZoomed, getZoom());
        }
    }

    private void rectF2Rect(RectF inRectF, Rect outRect){
        outRect.left = (int)inRectF.left;
        outRect.right = (int)inRectF.right;
        outRect.top = (int)inRectF.top;
        outRect.bottom = (int)inRectF.bottom;
    }

    protected int closerBorder() {
        if (mPageRect.top>=0 && mPageRect.bottom <= mPdfSurfaceHolder.getSurfaceFrame().height()) {
            return BOTH_BORDERS;
        } if (mPageRect.top>=0) {
            return TOP_BORDER;
        } if (mPageRect.bottom<=mPdfSurfaceHolder.getSurfaceFrame().height()) {
            return BOTTOM_BORDER;
        }
            return NO_BORDER;
    }

    public boolean isZoomed() {
        return isZoomed;
    }

    public void zoomTo(float zoom, PointF pivot) {
        //It allows to work with matrixes.
        RectF mPageRectF = new RectF();
        //Check zoom levels
        if (this.zoom*zoom < MINIMUM_ZOOM) {
            resetPageFit();
        } else {
            if (this.zoom*zoom > MAXIMUM_ZOOM) {
                zoom = MAXIMUM_ZOOM / this.zoom;
            }
            this.zoom *= zoom;
            float focusX = pivot.x;
            float focusY = pivot.y;
            mTransformMatrix.setScale(zoom, zoom,
                    focusX, focusY);
            mPageRectF.set(mPageRect);
            mTransformMatrix.mapRect(mPageRectF);
            if (DEBUG_MODE) Log.d("PdfView", "Zoom: " + this.zoom);
            rectF2Rect(mPageRectF, mPageRect);
            isZoomed = true;
            //Fix movement while zooming
            float moveX = 0f;
            float moveY = 0f;
            if (mPageRect.left > 0) {
                moveX -= mPageRect.left;
            }
            if (mPageRect.top > 0) {
                moveY -= mPageRect.top;
            }
            if (mPageRect.right < mScreenRect.width()) {
                moveX = mScreenRect.width() - mPageRect.right;
            }
            if (mPageRect.bottom < mScreenRect.height()) {
                moveY = mScreenRect.height() - mPageRect.bottom;
            }
            moveRelative(moveX,moveY,false);
        }
        if (onZoomChangedListener !=null) {
            onZoomChangedListener.zoomChanged(isZoomed, getZoom());
        }
    }

    public float getZoom() {
        return this.zoom;
    }

    protected Rect getScreenRect() {
        return mScreenRect;
    }

    public void moveTo(float distanceX, float distanceY,boolean render) {
        if(!isSurfaceCreated) return;
            if (DEBUG_MODE) Log.d(TAG, "DistanceX: " + distanceX);
            if (DEBUG_MODE) Log.d(TAG, "DistanceY: " + distanceY);
            int deltaX = mPageRect.width();
            int deltaY = mPageRect.height();
            mPageRect.left = (int) (distanceX);
            mPageRect.right = (mPageRect.left + deltaX);
            mPageRect.top = (int) (distanceY);
            mPageRect.bottom = (mPageRect.top + deltaY);
            if (render) {
                render();
            }
    }

    public void moveRelative(float distanceX, float distanceY) {
        moveRelative(distanceX,distanceY,true);
    }

    public void moveRelative(float distanceX, float distanceY,boolean render) {
            float newLeft = mPageRect.left + distanceX;
            float newRight = mPageRect.right + distanceX;
            float newTop = mPageRect.top + distanceY;
            float newBottom = mPageRect.bottom + distanceY;

            //Don't move more than needed each side.
            if (distanceX > 0 && newRight >= mScreenRect.right && newLeft >= mScreenRect.left) {
                distanceX = (mScreenRect.left - mPageRect.left);
            }
            if (distanceX < 0 && newLeft <= mScreenRect.left && newRight <= mScreenRect.right) {
                distanceX = (mScreenRect.right - mPageRect.right);
            }
            if (distanceY > 0 && newBottom >= mScreenRect.bottom && newTop >= mScreenRect.top) {
                distanceY = (mScreenRect.top - mPageRect.top);
            }
            if (distanceY < 0 && newTop <= mScreenRect.top && newBottom <= mScreenRect.bottom) {
                distanceY = (mScreenRect.bottom - mPageRect.bottom);
            }
            //If the height of the document is contained on entire view
            if (mScreenRect.height() > mPageRect.height()) {
                //Center vertical
                moveTo(mPageRect.left + distanceX, (mScreenRect.height() - mPageRect.height()) / 2,render);
            }
            //if width of document is contained on entire view:
            else if (mScreenRect.width() > mPageRect.width()) {
                //Center horizontal
                moveTo(((mScreenRect.width() - mPageRect.width()) / 2), mPageRect.top + distanceY,render);
            } else {
                moveTo(mPageRect.left + distanceX, mPageRect.top + distanceY,render);

            }
    }

    protected void recycle() {
        try{
            if(mPdfDoc != null && mDocFileStream != null){
                mPdfCore.closeDocument(mPdfDoc);
                if (DEBUG_MODE) Log.d("Main", "Close Document");
                mDocFileStream.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    public Configurator fromUri(Uri uri) {
        return new Configurator(uri);
    }

    public void goToPage(int index) {
        goToPage(index,BOTH_BORDERS);
    }

    public void goToPage(int index, int transitionType) {
        if(index >= 0 && index < mPageCount) {
            loadPage(index,transitionType);
        }
    }

    public void nextPage() {
        goToPage(mCurrentPageIndex+1);
    }

    public void prevPage() {
        goToPage(mCurrentPageIndex-1);
    }

    public void firstPage() {
        goToPage(1);
    }

    public void LastPage() {
        goToPage(mPageCount);
    }

    public int getPageCount() {
        return mPageCount;
    }

    public int getCurrentPage() {
        return mCurrentPageIndex+1;
    }

    private void setOnPageChangedListener(OnPageChangedListener onPageChangedListener) {
        this.onPageChangedListener = onPageChangedListener;
    }

    private void setOnZoomChangedListener(OnZoomChangedListener onZoomChangedListener) {
        this.onZoomChangedListener = onZoomChangedListener;
    }

    private void setOnErrorOccuredListener(OnErrorOccurredListener onErrorOccurredListener) {
        this.onErrorOccurredListener = onErrorOccurredListener;
    }

    private void setOnLoadCompleteListener(OnLoadCompleteListener onLoadCompleteListener) {
        this.onLoadCompleteListener = onLoadCompleteListener;
    }



    public class Configurator {

        private final Uri uri;

        private OnLoadCompleteListener onLoadCompleteListener;

        private OnPageChangedListener onPageChangedListener;

        private OnZoomChangedListener onZoomChangedListener;

        private OnErrorOccurredListener onErrorOccurredListener;

        private Configurator(Uri uri) {
            this.uri = uri;
        }

        public Configurator onLoad(OnLoadCompleteListener onLoadCompleteListener) {
            this.onLoadCompleteListener = onLoadCompleteListener;
            return this;
        }

        public Configurator onPageChanged(OnPageChangedListener onPageChangedListener) {
            this.onPageChangedListener = onPageChangedListener;
            return this;
        }

        public Configurator onErrorOccured(OnErrorOccurredListener onErrorOccurredListener) {
            this.onErrorOccurredListener = onErrorOccurredListener;
            return this;
        }

        public Configurator onZoomChanged(OnZoomChangedListener onZoomChangedListener) {
            this.onZoomChangedListener = onZoomChangedListener;
            return this;
        }

        public void load() {
            PdfView.this.setOnLoadCompleteListener(onLoadCompleteListener);
            PdfView.this.setOnPageChangedListener(onPageChangedListener);
            PdfView.this.setOnErrorOccuredListener(onErrorOccurredListener);
            PdfView.this.setOnZoomChangedListener(onZoomChangedListener);
            PdfView.this.loadDocument(uri);
        }
    }

    public void render() {
        if (isRenderable) {
            mPdfCore.renderPage(mPdfDoc, mPdfSurfaceHolder.getSurface(), mCurrentPageIndex,
                    mPageRect.left, mPageRect.top,
                    mPageRect.width(), mPageRect.height());
        }
    }

    public void loadPage(final int indexPage, final int transitionType) {
        if (!areTasksRunning()) {
            loadingTask = new AsyncTask<Object, Object, Object>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    pd = ProgressDialog.show(c, "Loading", "");
                    pd.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    pd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    pd.setContentView(R.layout.pd_layout);
                    pd.getWindow().setGravity(Gravity.LEFT | Gravity.BOTTOM);

                }

                @Override
                protected Object doInBackground(Object... params) {
                    if (mPdfDoc != null) {
                        mCurrentPageIndex = indexPage;
                        mRenderRunnable.run();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    if (transitionType == BOTH_BORDERS) {
                        resetPageFit();
                    } else if (transitionType == TOP_BORDER) {
                        goToBottom();
                    } else if (transitionType == BOTTOM_BORDER) {
                        goToTop();
                    }
                    if (onPageChangedListener != null) {
                        onPageChangedListener.pageChanged(mCurrentPageIndex + 1, mPageCount);
                    }
                    pd.dismiss();
                }
            };
            loadingTask.execute();
        }
    }

    public boolean areTasksRunning() {
        return (loadingTask != null && loadingTask.getStatus() == AsyncTask.Status.RUNNING);
    }


}
