package com.github.eajon.upload;



import com.github.eajon.rxbus.RxBusRelay;
import com.github.eajon.rxbus.RxResponse;

import java.io.File;

public class UploadTask {

    private String tag;
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



    public UploadTask(String tag, File file) {
        this.tag = tag;
        this.file = file;
        this.totalSize=file.length();
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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


    public void sendBus() {
        RxResponse rxResponse = new RxResponse(tag);
        rxResponse.setData(this);
        RxBusRelay.get().post(rxResponse);
    }

    @Override
    public String toString() {
        return "UploadTask{" +
                "tag='" + tag + '\'' +
                ", file=" + file +
                ", currentSize=" + currentSize +
                ", totalSize=" + totalSize +
                ", state=" + state +
                '}';
    }
}
