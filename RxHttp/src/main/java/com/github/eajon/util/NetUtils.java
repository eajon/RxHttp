package com.github.eajon.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


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

    /**
     * 获取 encode 后 Header 值
     * 备注: OkHttp Header 中的 value 不支持 null, \n 和 中文 等特殊字符
     * 后台解析中文 Header 值需要decode（这个后台处理，前端不用理会）
     *
     * @param value
     * @return
     */
    public static Object getHeaderValueEncoded(Object value) {
        if (value == null) return "null";
        if (value instanceof String) {
            String strValue = ((String) value).replace("\n", "");//换行符
            for (int i = 0, length = strValue.length(); i < length; i++) {
                char c = strValue.charAt(i);
                if (c <= '\u001f' || c >= '\u007f') {
                    try {
                        return URLEncoder.encode(strValue, "UTF-8");//中文处理
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        return "";
                    }
                }
            }
            return strValue;
        } else {
            return value;
        }
    }

}
