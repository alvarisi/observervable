package com.tokopedia.observerlearning;

import android.graphics.Bitmap;

/**
 * Created by Tokopedia01 on 5/17/2016.
 */
public class WidgetObject {
    private String text;
    private Bitmap bitmap;

    public WidgetObject(String text,Bitmap bitmap) {
        this.text = text;
        this.bitmap = bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
