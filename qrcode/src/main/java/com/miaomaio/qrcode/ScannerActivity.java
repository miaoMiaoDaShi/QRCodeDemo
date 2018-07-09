package com.miaomaio.qrcode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.lang.ref.WeakReference;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, View.OnClickListener {

    private static final String TAG = "ScannerActivity";
    private ZXingScannerView mZXingScannerView;

    private Handler mHandler = new Handler();
    private CustomFinderView mCustomFinderView;

    private ImageView mIvBack;
    private ImageView mIvOpenLight;
    private ImageView mIvToAlbum;
    //private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        initView();
        bindListener();
    }

    private void initView() {
        setupScannerView();
        mIvBack = (ImageView) findViewById(R.id.mIvBack);
        mIvOpenLight = (ImageView) findViewById(R.id.mIvOpenLight);
        mIvToAlbum = (ImageView) findViewById(R.id.mIvToAlbum);


    }

    private void bindListener() {
        mIvBack.setOnClickListener(this);
        mIvOpenLight.setOnClickListener(this);
        mIvToAlbum.setOnClickListener(this);
    }

    private void setupScannerView() {
        mCustomFinderView = new CustomFinderView(getApplicationContext());
        mZXingScannerView = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return mCustomFinderView;
            }
        };
        //mZXingScannerView.setAspectTolerance(0.1f);
        mZXingScannerView.setAutoFocus(true);
        final FrameLayout finderContent = (FrameLayout) findViewById(R.id.mFinderContent);
        finderContent.addView(mZXingScannerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mZXingScannerView.setResultHandler(this);
        mZXingScannerView.startCamera();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(this, 20);
                mCustomFinderView.postInvalidate();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacksAndMessages(this);
        mZXingScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        handleResult(result.getText());

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.mIvBack) {
            finish();

        } else if (i == R.id.mIvOpenLight) {
            mZXingScannerView.toggleFlash();
            ((ImageView) view).setImageResource(mZXingScannerView.getFlash() ?
                    R.drawable.ic_turn_off_the_light : R.drawable.ic_turn_on_the_light);

        } else if (i == R.id.mIvToAlbum) {
            Matisse.from(this)
                    .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                    .countable(true)
                    .maxSelectable(1)
                    .gridExpectedSize(500)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .imageEngine(new GlideEngine())
                    .forResult(1);

        }
    }

    private void handleResult(String result) {
        final Intent intent = new Intent();
        intent.putExtra("result", result);
        setResult(Activity.RESULT_OK, intent);
        finish();
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }


    private void encodeImage(final String path) {

//        Observable.create(new ObservableOnSubscribe<String>() {
//            @Override
//            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
//                if (!TextUtils.isEmpty(path)) {
//                    final String code = QRCodeUtils.getStringFromQRCode(BitmapFactory.decodeFile(path));
//                    emitter.onNext(code);
//                } else {
//                    emitter.onError(new Throwable("无效图片地址"));
//                }
//
//                emitter.onComplete();
//            }
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<String>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        mDisposable = d;
//                    }
//
//                    @Override
//                    public void onNext(String s) {
//                        handleResult(s);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Toast.makeText(ScannerActivity.this, "没有识别到二维码", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == 1) {
            new DeCodeImageAsyncTask(new WeakReference<ScannerActivity>(this)).execute(Matisse.obtainPathResult(data).get(0));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mDisposable != null) {
//            mDisposable.dispose();
//        }
    }

    private static class DeCodeImageAsyncTask extends AsyncTask<String, Void, String> {


        private WeakReference<ScannerActivity> mActivityWeakReference;

        DeCodeImageAsyncTask(WeakReference<ScannerActivity> weakReference) {
            mActivityWeakReference = weakReference;
        }

        @Override
        protected String doInBackground(String... strings) {
            String path = strings[0];
            if (!TextUtils.isEmpty(path)) {
                return QRCodeUtils.getStringFromQRCode(BitmapFactory.decodeFile(path));
            } else {
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            final ScannerActivity activity = mActivityWeakReference.get();
            if (activity != null) {
                if (TextUtils.isEmpty(s)) {
                    Toast.makeText(activity.getApplicationContext(), "没有识别到二维码", Toast.LENGTH_SHORT).show();
                } else {
                    activity.handleResult(s);
                }
            }

        }
    }
}
