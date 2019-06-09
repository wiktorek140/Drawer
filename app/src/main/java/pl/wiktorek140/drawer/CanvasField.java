package pl.wiktorek140.drawer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CanvasField extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static Bitmap mBitmap = null;
    private static Canvas mCanvas = null;
    private SurfaceHolder mSurface; //Monitor powierzchni
    private Paint mPaint = new Paint(); //Farba w danym kolorze
    private boolean rotate = false;

    private float xEnd = -1;
    private float yEnd = -1;
    private float xStart = -1;
    private float yStart = -1;
    private float xEndOld = -1;
    private float yEndOld = -1;

    private Thread mPaintThread; //Aktualizator powierzchni
    private boolean isPainting = false; //Czy wątek rysowania pracuje
    private Object mBlokada = new Object(); //Obiekt do tworzenia sekcji krytycznych

    public CanvasField(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSurface = getHolder();
        mSurface.addCallback(this);
    }

    //Wznawianie rysownaia
    public void startDrawing() {
        mPaintThread = new Thread(this);
        isPainting = true;
        mPaintThread.start();
    }

    //Zatrzymaj mozliwośc rysowania
    public void stopDrawing() {
        isPainting = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();

        try {
            //Ustalamy co ile mają być zbierane punkty z ekranu
            Thread.sleep(30);
        } catch(InterruptedException e) {}

        //modyfikacja rysunku na wyłączność
        synchronized(mBlokada) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    stopDrawing();
                    startDrawing();
                    xStart = event.getX();
                    yStart = event.getY();
                    mPaint.setColor(MainActivity.color); //Ustawienie koloru
                    mPaint.setStrokeWidth(MainActivity.brushSize); //Ustawienie szerokości linii
                    mCanvas.drawCircle(xStart, yStart, MainActivity.brushSize + 3, mPaint); //Rysowanie kółka na końcu i początku linii
                    xEndOld = xStart;
                    yEndOld = yStart;
                    return true;

                case MotionEvent.ACTION_UP:
                    mCanvas.drawCircle(event.getX(),event.getY(), MainActivity.brushSize + 3, mPaint);
                    return true;

                case MotionEvent.ACTION_MOVE:
                    xEnd = event.getX();
                    yEnd = event.getY();
                    mCanvas.drawLine(xEndOld, yEndOld, xEnd, yEnd, mPaint);
                    xEndOld = xEnd;
                    yEndOld = yEnd;
                    return true;

                default:
                    break;
            }
        }
        return false;
    }

    @Override
    public void run() {
        while(isPainting) {
            Canvas canvas= null;
            try {
                synchronized(mSurface) {
                    if (!mSurface.getSurface().isValid()) //Sprawdza czy powierzchnia jest prawidłowa
                        continue;

                    canvas = mSurface.lockCanvas(null);

                    //Dostęp do rysowania ma jeden wątek na raz
                    synchronized(mBlokada) {
                        if(isPainting) {
                            if(MainActivity.clear) { //Instrukcja wykona się gdy przycisk o ID clear jest wciśnięty
                                mCanvas.drawARGB(255, 255, 255, 255); //Wyczyszczenie ekranu (zamalowanie kanwy na biało)
                                MainActivity.clear = false;
                            }
                            canvas.drawBitmap(mBitmap, 0, 0, null); //Wyświetlenie kanwy na ekranie
                        }
                    }
                }
            }
            finally { // zapewnie spójnosci obrazu
                if(canvas != null) {
                    mSurface.unlockCanvasAndPost(canvas); //Koniec edycji kanwy i wyświetlenie rysunku na ekranie
                }
            }
        }
    }

    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas c = null;
        try {
            c = holder.getSurface().lockCanvas(null);
            int w = (getWidth()>getHeight())?getWidth():getHeight();
            if(mBitmap == null) mBitmap = Bitmap.createBitmap(w, w, Bitmap.Config.ARGB_8888); //Tworzenie bitmapy

            if(mCanvas == null) {
                mCanvas = new Canvas(mBitmap); //Tworzenie z bitmapy
                mCanvas.drawARGB(255, 255, 255, 255); //Zamalowanie na biało
                c.drawARGB(255, 255, 255, 255); //Zamalowanie na biało
            } else {
                c.drawBitmap(mBitmap,0,0, mPaint);
            }
        } finally {
            holder.getSurface().unlockCanvasAndPost(c);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isPainting = false; //Zatrzymanie rysowania
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Canvas c = null;
        try {
            c = holder.lockCanvas(null);

            if(mBitmap == null) mBitmap = Bitmap.createBitmap(width, height - MainActivity.buttonHeight, Bitmap.Config.ARGB_8888); //Tworzenie bitmapy

            if(mCanvas != null) {

                c.drawBitmap(mBitmap,0,0, mPaint);
            }
        } finally {
            holder.unlockCanvasAndPost(c);
        }
    }
}