package com.github.eajon.task;

import android.text.TextUtils;

import com.github.eajon.util.LogUtils;
import com.threshold.rxbus2.RxBus;

import java.math.BigDecimal;

public class BaseTask {

    /**
     * 枚举状态
     */
    public enum State {
        NONE,           //无状态
        WAITING,        //等待
        LOADING,        //启动中
        ERROR,          //错误
        FINISH,         //完成
        PAUSE,          //暂停
        CANCEL,         //取消
    }

    public State state = State.NONE;//下载状态

    public long speed;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        if (state != State.LOADING) {
            this.speed = 0;
        }
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public double getSpeedMB() {
        return new BigDecimal((double) speed / (1024 * 1024)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

    }

    public double getSpeedKB() {
        return new BigDecimal((double) speed / 1024).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public boolean isFinish() {
        return state == State.FINISH;
    }

    public boolean isError() {
        return state == State.ERROR;
    }

    public void sendBus(String eventId, boolean isStick) {
        if (isStick) {
            RxBus.getDefault().removeStickyEventType(this.getClass());
            if (TextUtils.isEmpty(eventId)) {
                RxBus.getDefault().postSticky(this);
            } else {
                RxBus.getDefault().postSticky(eventId, this);
            }
        } else {
            if (TextUtils.isEmpty(eventId)) {
                RxBus.getDefault().post(this);
            } else {
                RxBus.getDefault().post(eventId, this);
            }
        }
    }
}
