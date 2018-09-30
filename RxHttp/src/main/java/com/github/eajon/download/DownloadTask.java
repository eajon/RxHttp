package com.github.eajon.download;




import com.github.eajon.rxbus.RxBusRelay;
import com.github.eajon.rxbus.RxResponse;

import java.io.Serializable;


public class DownloadTask implements Serializable {


    private String tag;


    private String localUrl;//本地存储地址


    private String serverUrl;//下载地址


    private long totalSize;//文件大小


    private long currentSize;//当前大小


    private State state = State.NONE;//下载状态


    public DownloadTask(String tag, String localUrl, String serverUrl) {
        this.tag = tag;
        this.localUrl = localUrl;
        this.serverUrl = serverUrl;
    }


    /**
     * 枚举下载状态
     */
    public enum State {
        NONE,           //无状态
        WAITING,        //等待
        LOADING,        //下载中
        PAUSE,          //暂停
        ERROR,          //错误
        FINISH,         //完成
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }


    public String getLocalUrl() {
        return localUrl == null ? "" : localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public String getServerUrl() {
        return serverUrl == null ? "" : serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(long currentSize) {
        this.currentSize = currentSize;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
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
        RxResponse rxResponse = new RxResponse(this.tag);
        rxResponse.setData(this);
        RxBusRelay.get().post(rxResponse);
    }


}
