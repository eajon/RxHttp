package com.eajon.my.glide;

import android.app.Activity;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestOptions;
import com.eajon.my.R;

import java.io.Serializable;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by Administrator on 2017/11/3.
 * glide的工具类
 */

public class GlideUtils implements Serializable {

    public static void loadImg(Activity activity, String url, ImageView img) {
        RequestOptions options = new RequestOptions();
        options.transform(new RoundedCornersTransformation(128, 0, RoundedCornersTransformation.CornerType.BOTTOM));
        options.placeholder(R.mipmap.ic_launcher);//加载中的占位图
        options.error(R.mipmap.ic_launcher);//错误时的占位图
        GlideApp.with(activity).load(url)
                .apply(options)
                .into(img);
    }

    public static void loadImg(Activity activity, Uri uri, ImageView img) {
        RequestOptions options = new RequestOptions();
        options.transform(new RoundedCornersTransformation(128, 0, RoundedCornersTransformation.CornerType.BOTTOM));
        options.placeholder(R.mipmap.ic_launcher);//加载中的占位图
        options.error(R.mipmap.ic_launcher);//错误时的占位图
        GlideApp.with(activity).load(uri)
                .apply(options)
                .into(img);
    }

}
