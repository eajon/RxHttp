package com.github.eajon.task;

import com.github.eajon.util.SpeedUtils;

import java.util.concurrent.TimeUnit;

public abstract class BaseTask {


    /**
     * 枚举状态
     */
    public enum State {
        /*无状态*/
        NONE,
        /*等待*/
        WAITING,
        /*启动中*/
        LOADING,
        /*错误*/
        ERROR,
        /*完成*/
        FINISH,
        /*暂停*/
        PAUSE,
        /*取消*/
        CANCEL,
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

    public abstract int getProgress();

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


    public abstract float getAverageSpeed();

    public abstract String getAverageSpeedFormat();

    public abstract String getAverageSpeedFormat(TimeUnit timeUnit);

    protected float getAverageSpeed(long currentSize) {
        long dur = getDuration();
        if (currentSize != 0 && dur != 0) {
            float speed = 1000F * currentSize / dur;
            return speed;
        } else {
            return 0F;
        }

    }

    protected String getAverageSpeedFormat(long currentSize) {
        return SpeedUtils.formatSpeedPerSecond(getAverageSpeed(currentSize));
    }

    protected String getAverageSpeedFormat(long currentSize, TimeUnit timeUnit) {
        return SpeedUtils.formatSpeed(getAverageSpeed(currentSize), timeUnit);
    }

    public boolean isFinish() {
        return state == State.FINISH;
    }

    public boolean isError() {
        return state == State.ERROR;
    }


}
