package com.github.eajon.upload;



import com.github.eajon.rxbus.RxBusRelay;
import com.github.eajon.rxbus.RxResponse;

import java.io.File;

public class UploadTask {

    private String tag;
    private File file;
    private long currentSize;
    private long totalSize;
    private int index;
    private int total;

    private State state = State.NONE;//上传状态

    /**
     * 枚举下载状态
     */
    public enum State {
        NONE,           //无状态
        LOADING,        //上传中
        ERROR,          //错误
        FINISH,         //完成
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }



    public UploadTask(String tag, File file, int index, int total) {
        this.tag = tag;
        this.file = file;
        this.index = index;
        this.total = total;
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
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
}
