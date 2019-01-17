package com.github.eajon.task;

import android.os.Environment;

import java.io.Serializable;


public class DownloadTask extends BaseTask implements Serializable {


    private String name;//存储在本地的文件名
    private String originalName;//存储在本地的文件名
    private String localDir;//本地存储目录
    private long totalSize;//文件大小
    private long currentSize;//当前大小

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


    public int getProgress() {
        if (totalSize != 0) {
            float progress = (float) currentSize / (float) totalSize;
            return (int) (progress * 100);
        } else {
            return 0;
        }
    }


    @Override
    public String toString() {
        return "DownloadTask{" +
                "name='" + name + '\'' +
                ", localDir='" + localDir + '\'' +
                ", totalSize=" + totalSize +
                ", currentSize=" + currentSize +
                '}';
    }
}
