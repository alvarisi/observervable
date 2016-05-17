package com.tokopedia.observerlearning;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    Subscription subscription;
    ImageView ivImage;
    TextView tvText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        tvText = (TextView) findViewById(R.id.tvText);

        Observable<String> textObservable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String textContent = getResources().getString(R.string.lorem);
                Log.v("OBSERVERLEARNING999","running text get");
                subscriber.onNext(textContent);
            }
        });
        Observable<Bitmap> bitmapObservable = Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                Bitmap b = drawableToBitmap(getResources().getDrawable(R.drawable.building));
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.PNG, 60, out);
                Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                subscriber.onNext(decoded);
            }
        });
       Observable<WidgetObject> test =  Observable.zip(textObservable, bitmapObservable, new Func2<String, Bitmap, WidgetObject>() {
            @Override
            public WidgetObject call(String s, Bitmap bitmap) {
                return new WidgetObject(s,bitmap);
            }
        });

        subscription = test.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<WidgetObject>() {
            @Override
            public void onCompleted() {
                Snackbar.make(findViewById(android.R.id.content), "Had a snack at Snackbar", Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.RED)
                        .show();
            }

            @Override
            public void onError(Throwable e) {
                Log.v("OBSERVERLEARNING999", "ERROR");
                e.printStackTrace();
            }

            @Override
            public void onNext(WidgetObject o) {
                Log.v("OBSERVERLEARNING999", "get" + o.getText());
                ivImage.setImageBitmap(o.getBitmap());
                tvText.setText(o.getText());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed()){
            subscription.unsubscribe();
        }
    }

    public rx.Observable<String> getSomething(){
        return rx.Observable.defer(new Func0<rx.Observable<String>>() {
            @Override
            public rx.Observable<String> call() {

                return null;
            }
        });
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
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}

