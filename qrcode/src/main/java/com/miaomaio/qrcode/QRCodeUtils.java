package com.miaomaio.qrcode;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.Hashtable;

/**
 * Author : zhongwenpeng
 * Email : 1340751953@qq.com
 * Time :  2018/7/6
 * Description :
 */

public class QRCodeUtils {

    public static String getStringFromQRCode(Drawable drawable) {
        String httpString = null;

        Bitmap bmp = ImageUtils.drawableToBitmap(drawable);
        byte[] data = ImageUtils.getYUV420sp(bmp.getWidth(), bmp.getHeight(), bmp);
        // 处理
        try {
            Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
//            hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);
            PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data,
                    bmp.getWidth(),
                    bmp.getHeight(),
                    0, 0,
                    bmp.getWidth(),
                    bmp.getHeight(),
                    false);
            BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
            QRCodeReader reader2 = new QRCodeReader();
            Result result = reader2.decode(bitmap1, hints);

            httpString = result.getText();
        } catch (Exception e) {
            e.printStackTrace();
        }

        bmp.recycle();
        bmp = null;

        return httpString;
    }

    public static String getStringFromQRCode(Bitmap bitmap) {
        String httpString = null;

        Bitmap bmp = bitmap;
        byte[] data = ImageUtils.getYUV420sp(bmp.getWidth(), bmp.getHeight(), bmp);
        // 处理
        try {
            Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
//            hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);
            PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data,
                    bmp.getWidth(),
                    bmp.getHeight(),
                    0, 0,
                    bmp.getWidth(),
                    bmp.getHeight(),
                    false);
            BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
            QRCodeReader reader2 = new QRCodeReader();
            Result result = reader2.decode(bitmap1, hints);

            httpString = result.getText();
        } catch (Exception e) {
            e.printStackTrace();
        }

        bmp.recycle();
        bmp = null;

        return httpString;
    }
}