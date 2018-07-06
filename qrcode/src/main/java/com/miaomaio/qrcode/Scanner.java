package com.miaomaio.qrcode;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;

/**
 * Author : zhongwenpeng
 * Email : 1340751953@qq.com
 * Time :  2018/7/6
 * Description :
 */
public class Scanner {
    private final WeakReference<Activity> mContext;
    private final WeakReference<Fragment> mFragment;

    private Scanner(Activity activity) {
        this(activity, null);
    }

    private Scanner(Fragment fragment) {
        this(fragment.getActivity(), fragment);
    }

    private Scanner(Activity activity, Fragment fragment) {
        mContext = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }

    public static Scanner from(Activity activity) {
        return new Scanner(activity);
    }

    public static Scanner from(Fragment fragment) {
        return new Scanner(fragment);
    }

    public ScannerRequest request(int requestCode) {
        return new ScannerRequest(this, requestCode);
    }

    @Nullable
    Activity getActivity() {
        return mContext.get();
    }

    @Nullable
    Fragment getFragment() {
        return mFragment != null ? mFragment.get() : null;
    }

    public static String getCode(Intent intent) {
        return intent.getStringExtra("result");
    }

}
