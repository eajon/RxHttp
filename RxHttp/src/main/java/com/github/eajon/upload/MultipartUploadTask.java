package com.github.eajon.upload;

import com.github.eajon.rxbus.RxBusRelay;
import com.github.eajon.rxbus.RxResponse;

import java.util.ArrayList;

public class MultipartUploadTask {

    private String tag;
    private ArrayList <UploadTask> uploadTasks;

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



    public MultipartUploadTask(String tag, ArrayList <UploadTask> uploadTasks) {
        this.tag = tag;
        this.uploadTasks = uploadTasks;
    }

    public ArrayList <UploadTask> getUploadTasks() {
        return uploadTasks;
    }

    public int getProgress() {
        long totalSize = 0L;
        for (int i = 0; i < uploadTasks.size(); i++) {
            totalSize += uploadTasks.get(i).getTotalSize();
        }
        long currentSize = 0L;
        for (int i = 0; i < uploadTasks.size(); i++) {
            currentSize += uploadTasks.get(i).getCurrentSize();
        }
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
