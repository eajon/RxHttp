package com.github.eajon.util;

import android.text.TextUtils;

import com.github.eajon.rxbus.RxBus;


public class RxBusUtils {


    //默认发射粘性消息只同类型保留最后一个
    public static void sendBus(String tag, Object object, boolean isStick) {
        if (isStick) {
            RxBus.getDefault().removeStickyEventType(object.getClass());
        }
        if (TextUtils.isEmpty(tag)) {
            RxBus.getDefault().post(object, isStick);
        } else {
            RxBus.getDefault().post(tag, object, isStick);
        }
    }
}
