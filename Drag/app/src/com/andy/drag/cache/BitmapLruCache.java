package com.andy.drag.cache;

import android.graphics.Bitmap;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.HONEYCOMB_MR1;

/*
 * A bitmap memory cache implementation.
 *
 * @author Shintaro Katafuchi
 */
public final class BitmapLruCache extends LruCache<String, Bitmap> {

    private static BitmapLruCache sBitmapLruCache;
    private static final int DEFAULT_CAPACITY = 10;

    private BitmapLruCache(int capacity) {
        super(capacity);
    }

    public static BitmapLruCache getInstance () {
        if (sBitmapLruCache == null) {
            synchronized (BitmapLruCache.class) {
                if (sBitmapLruCache == null) {
                    sBitmapLruCache = new BitmapLruCache (DEFAULT_CAPACITY);
                }
            }
        }
        return sBitmapLruCache;
    }


    /**
     * Returns the number of bytes in {@link Bitmap}.
     *
     * @param bitmap bitmap
     * @return the number of bytes.
     */
    private static int getBitmapSize(Bitmap bitmap) {
        int bytes = SDK_INT >= HONEYCOMB_MR1 ? bitmap.getByteCount() : getByteCount(bitmap);
        if (bytes < 0) {
            throw new IllegalStateException("Bitmap size is negative. Size=" + bitmap);
        }
        return bytes;
    }

    private static int getByteCount(Bitmap bitmap) {
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    @Override
    protected String getClassName() {
        return BitmapLruCache.class.getName();
    }

    @Override
    protected int getValueSize(Bitmap value) {
        return getBitmapSize(value);
    }

    public void stop (){
        clear();
        sBitmapLruCache = null;
    }

}
