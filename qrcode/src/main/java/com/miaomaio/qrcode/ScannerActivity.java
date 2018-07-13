package com.miaomaio.qrcode;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
    public static final int REQUEST_CODE_PERMISSION_CAMERA = 0x10;
    public static final int REQUEST_CODE_PERMISSION_CAMERA_WRITE_EXTERNAL_STORAGE = 0x11;
    //private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        initView();
        bindListener();
    }

    private void initView() {
        mIvBack = (ImageView) findViewById(R.id.mIvBack);
        mIvOpenLight = (ImageView) findViewById(R.id.mIvOpenLight);
        mIvToAlbum = (ImageView) findViewById(R.id.mIvToAlbum);

        setupScannerView();


    }

    public boolean checkPermission(String permission, int requestCode) {

        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, permission)) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            return false;
        }

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission(Manifest.permission.CAMERA, REQUEST_CODE_PERMISSION_CAMERA)) {
                return;
            }
        }

        mZXingScannerView.setResultHandler(this);
        mZXingScannerView.startCamera();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(this, 35);
                mCustomFinderView.postInvalidate();
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission(Manifest.permission.CAMERA, REQUEST_CODE_PERMISSION_CAMERA)) {
                return;
            }
        }
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_CODE_PERMISSION_CAMERA_WRITE_EXTERNAL_STORAGE)) {
                    return;
                }
            }

            toSelectQrCodemage();


        }
    }

    private void handleResult(String result) {
        final Intent intent = new Intent();
        intent.putExtra("result", result);
        setResult(Activity.RESULT_OK, intent);
        finish();
        //Toast.makeText(this, result, Toast.LENGTH_LONG).show();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //相机权限

        if (requestCode == REQUEST_CODE_PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                finish();
                Toast.makeText(this, "请同意相机权限", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_PERMISSION_CAMERA_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toSelectQrCodemage();
            } else {
                Toast.makeText(this, "请同意存储读取权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void toSelectQrCodemage() {
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
