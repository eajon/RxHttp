package com.github.eajon.task;

import android.os.Environment;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;


public class DownloadTask extends BaseTask implements Serializable {


    private String name;//存储在本地的文件名
    private String originalName;//存储在本地的文件名
    private String localDir;//本地存储目录
    private long totalSize;//文件大小
    private long currentSize;//当前大小
    private long rangeSize;//纪录上次下载位置

    public DownloadTask() {
        this.localDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    public DownloadTask(String name) {
        this.name = name;
        this.localDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    public DownloadTask(String name, String localDir) {
        this.name = name;
        this.localDir = localDir;
    }

    @Override
    public void setState(State state) {
        super.setState(state);
        if (state == State.PAUSE) {
            rangeSize = currentSize;
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getLocalDir() {
        return localDir == null ? "" : localDir;
    }

    public void setLocalDir(String localDir) {
        this.localDir = localDir;
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


    @Override
    public int getProgress() {
        if (totalSize != 0) {
            float progress = ( float ) currentSize / ( float ) totalSize;
            return ( int ) (progress * 100);
        } else {
            return 0;
        }
    }

    @Override
    public float getAverageSpeed() {
        return getAverageSpeed(currentSize - rangeSize);

    }

    @Override
    public String getAverageSpeedFormat() {
        return getAverageSpeedFormat(currentSize - rangeSize);
    }

    @Override
    public String getAverageSpeedFormat(TimeUnit timeUnit) {
        return getAverageSpeedFormat(currentSize - rangeSize, timeUnit);
    }


    @Override
    public String toString() {
        return "DownloadTask{" +
                "name='" + name + '\'' +
                ", originalName='" + originalName + '\'' +
                ", localDir='" + localDir + '\'' +
                ", totalSize=" + totalSize +
                ", currentSize=" + currentSize +
                ", rangeSize=" + rangeSize +
                '}';
    }
}
