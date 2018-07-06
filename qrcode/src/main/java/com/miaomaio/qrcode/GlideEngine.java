package com.miaomaio.qrcode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zhihu.matisse.engine.ImageEngine;


/**
 * Author : zhongwenpeng
 * Email : 1340751953@qq.com
 * Time :  2018/7/6
 * Description :
 */

public class GlideEngine implements ImageEngine {


    @SuppressLint("CheckResult")
    @Override
    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {


        Glide.with(context)
                .load(uri)
                .apply(new RequestOptions()
                        .placeholder(placeholder)
                        .override(resize, resize)
                        .centerCrop()
                )
                .into(imageView);
    }

    @Override
    public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {

        Glide.with(context)
                .asGif()
                .load(uri)
                .apply(new RequestOptions()
                        .placeholder(placeholder)
                        .override(resize, resize)
                        .centerCrop()
                )
                .into(imageView);
    }

    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {

        Glide.with(context).load(uri)
                .apply(new RequestOptions()
                        .override(resizeX, resizeY)
                        .centerCrop()
                )
                .into(imageView);
    }

    @Override
    public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {

        Glide.with(context)
                .asGif()
                .load(uri)
                .apply(new RequestOptions()
                        .centerCrop()
                        .override(resizeX, resizeY)
                )
                .into(imageView);
    }

    @Override
    public boolean supportAnimatedGif() {
        return false;
    }
}
