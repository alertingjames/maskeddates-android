package com.date.maskeddates.main;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.date.maskeddates.R;
import com.date.maskeddates.graphics.ImageViewTouch;
import com.date.maskeddates.graphics.ImageViewTouchBase;

import java.io.InputStream;
import java.net.URL;

public class ViewPhotoActivity extends AppCompatActivity {

    private static final String LOG_TAG = "imageTouch";

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);

        ImageViewTouch image = (ImageViewTouch) findViewById(R.id.photo);
        ((ImageView)findViewById(R.id.backButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        String photoUrl = getIntent().getStringExtra("photoUrl");

        // set the default image display type
        image.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        int size = (int) (Math.min(metrics.widthPixels, metrics.heightPixels) / 0.35);

        Bitmap bitmap=drawableToBitmap(LoadImageFromWebOperations(photoUrl));

        bitmap = BitmapUtilsS.resizeBitmap(bitmap,size,size);

        if (null != bitmap) {
            Log.d(LOG_TAG, "screen size: " + metrics.widthPixels + "x" + metrics.heightPixels);
            Log.d(LOG_TAG, "bitmap size: " + bitmap.getWidth() + "x" + bitmap.getHeight());

            image.setOnDrawableChangedListener(
                    new ImageViewTouchBase.OnDrawableChangeListener() {
                        @Override
                        public void onDrawableChanged(final Drawable drawable) {
                            Log.v(LOG_TAG, "image scale: " + image.getScale() + "/" + image.getMinScale());
                            Log.v(LOG_TAG, "scale type: " + image.getDisplayType() + "/" + image.getScaleType());

                        }
                    }
            );
            image.setImageBitmap(bitmap, null, 5, 5);

        } else {
            Toast.makeText(this, "We can not detect any internet connectivity. Please check your internet connection and try again.", Toast.LENGTH_LONG).show();
        }


        image.setSingleTapListener(
                new ImageViewTouch.OnImageViewTouchSingleTapListener() {

                    @Override
                    public void onSingleTapConfirmed() {
                        Log.d(LOG_TAG, "onSingleTapConfirmed");
                    }
                }
        );

        image.setDoubleTapListener(
                new ImageViewTouch.OnImageViewTouchDoubleTapListener() {

                    @Override
                    public void onDoubleTap() {
                        Log.d(LOG_TAG, "onDoubleTap");
                    }
                }
        );

        image.setOnDrawableChangedListener(
                new ImageViewTouchBase.OnDrawableChangeListener() {

                    @Override
                    public void onDrawableChanged(Drawable drawable) {
                        Log.i(LOG_TAG, "onBitmapChanged: " + drawable);
                    }
                }
        );
    }

    private Drawable LoadImageFromWebOperations(String url)
    {
        try
        {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        }catch (Exception e) {
            System.out.println("Exc="+e);
            return null;
        }
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static class BitmapUtilsS {

        /**
         * Resize a bitmap
         *
         * @param input
         * @param destWidth
         * @param destHeight
         * @return
         * @throws OutOfMemoryError
         */
        public static Bitmap resizeBitmap(final Bitmap input, int destWidth, int destHeight) throws OutOfMemoryError {
            return resizeBitmap( input, destWidth, destHeight, 0 );
        }

        /**
         * Resize a bitmap object to fit the passed width and height
         *
         * @param input
         *           The bitmap to be resized
         * @param destWidth
         *           Desired maximum width of the result bitmap
         * @param destHeight
         *           Desired maximum height of the result bitmap
         * @return A new resized bitmap
         * @throws OutOfMemoryError
         *            if the operation exceeds the available vm memory
         */
        public static Bitmap resizeBitmap( final Bitmap input, int destWidth, int destHeight, int rotation ) throws OutOfMemoryError {

            int dstWidth = destWidth;
            int dstHeight = destHeight;
            final int srcWidth = input.getWidth();
            final int srcHeight = input.getHeight();

            if ( rotation == 90 || rotation == 270 ) {
                dstWidth = destHeight;
                dstHeight = destWidth;
            }

            boolean needsResize = false;
            float p;
            if ( ( srcWidth > dstWidth ) || ( srcHeight > dstHeight ) ) {
                needsResize = true;
                if ( ( srcWidth > srcHeight ) && ( srcWidth > dstWidth ) ) {
                    p = (float) dstWidth / (float) srcWidth;
                    dstHeight = (int) ( srcHeight * p );
                } else {
                    p = (float) dstHeight / (float) srcHeight;
                    dstWidth = (int) ( srcWidth * p );
                }
            } else {
                dstWidth = srcWidth;
                dstHeight = srcHeight;
            }

            if ( needsResize || rotation != 0 ) {
                Bitmap output;

                if ( rotation == 0 ) {
                    output = Bitmap.createScaledBitmap( input, dstWidth, dstHeight, true );
                } else {
                    Matrix matrix = new Matrix();
                    matrix.postScale( (float) dstWidth / srcWidth, (float) dstHeight / srcHeight );
                    matrix.postRotate( rotation );
                    output = Bitmap.createBitmap( input, 0, 0, srcWidth, srcHeight, matrix, true );
                }
                return output;
            } else
                return input;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
