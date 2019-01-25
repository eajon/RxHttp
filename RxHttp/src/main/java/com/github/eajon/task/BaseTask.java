package com.github.eajon.task;

import com.github.eajon.util.SpeedUtils;

import java.util.concurrent.TimeUnit;

public abstract class BaseTask {

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

    private long startTime;
    private long finishTime;


    private State state = State.NONE;//下载状态

    private long speed;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        if (state != State.LOADING) {
            this.speed = 0;
        } else {
            startTime = System.currentTimeMillis();
        }
        if (state == State.FINISH) {
            finishTime = System.currentTimeMillis();
        }
    }

    public long getDuration() {
        if (finishTime == 0L) {
            return System.currentTimeMillis() - startTime;
        }
        return finishTime - startTime;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public String getSpeedFormat() {
        return SpeedUtils.formatSpeedPerSecond(speed);

    }


    public String getSpeedFormat(TimeUnit timeUnit) {
        return SpeedUtils.formatSpeed(speed, timeUnit);
    }

    public float getAverageSpeed(long currentSize) {
        long dur = getDuration();
        if (currentSize != 0 && dur != 0) {
            float speed = 1000F * currentSize / dur;
            return speed;
        } else {
            return 0F;
        }

    }

    public String getAverageSpeedFormat(long currentSize) {
        return SpeedUtils.formatSpeedPerSecond(getAverageSpeed(currentSize));
    }

    public String getAverageSpeedFormat(long currentSize, TimeUnit timeUnit) {
        return SpeedUtils.formatSpeed(getAverageSpeed(currentSize), timeUnit);
    }

    public boolean isFinish() {
        return state == State.FINISH;
    }

    public boolean isError() {
        return state == State.ERROR;
    }


}
