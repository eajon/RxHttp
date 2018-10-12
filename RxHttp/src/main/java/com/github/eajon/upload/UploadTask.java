package com.github.eajon.upload;



import android.text.TextUtils;

import com.threshold.rxbus2.RxBus;

import java.io.File;

public class UploadTask {

    private String fileName;
    private File file;
    private long currentSize;
    private long totalSize;

    private State state = State.NONE;//上传状态

    /**
     * 枚举下载状态
     */
    public enum State {
        NONE,           //无状态
        LOADING,        //上传中
        ERROR,          //错误
        FINISH,         //完成
        CANCEL,         //取消
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public UploadTask(File file) {
        this.file = file;
        this.fileName=file.getName();
        this.totalSize=file.length();
    }


    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public long getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(long currentSize) {
        this.currentSize = currentSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }





    public int getProgress() {
        if (totalSize != 0) {
            float progress = (float) currentSize / (float) totalSize;
            return (int) (progress * 100);
        } else {
            return 0;
        }
    }

    public boolean isFinish() {
        return currentSize == totalSize;
    }


    public void sendBus(String eventId,boolean isStick) {
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

    @Override
    public String toString() {
        return "UploadTask{" +
                "fileName='" + fileName + '\'' +
                ", file=" + file +
                ", currentSize=" + currentSize +
                ", totalSize=" + totalSize +
                ", state=" + state +
                '}';
    }
}
