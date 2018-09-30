package com.github.eajon.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class NetUtils {


    /**
     * 获取活动网络信息
     *
     * @return NetworkInfo
     */
    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm;
        try {
            cm = (ConnectivityManager) context.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
        } catch (Exception e) {
            return null;
        }

        return cm.getActiveNetworkInfo();
    }


    /**
     * 网络是否可用
     *
     * @return
     */
    public static boolean isAvailable(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return info != null && info.isAvailable();
    }


}
