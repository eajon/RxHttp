package com.github.eajon.util;

import android.text.TextUtils;

import com.threshold.rxbus2.RxBus;

public class RxBusUtils {


    //默认发射粘性消息只同类型保留最后一个
    public static void sendBus(String eventId, boolean isStick, Object object) {
        if (isStick) {
            RxBus.getDefault().removeStickyEventType(object.getClass());
            if (TextUtils.isEmpty(eventId)) {
                RxBus.getDefault().postSticky(object);
            } else {
                RxBus.getDefault().postSticky(eventId, object);
            }
        } else {
            if (TextUtils.isEmpty(eventId)) {
                RxBus.getDefault().post(object);
            } else {
                RxBus.getDefault().post(eventId, object);
            }
        }
    }
}
