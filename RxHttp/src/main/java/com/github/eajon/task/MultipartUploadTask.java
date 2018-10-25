package com.github.eajon.task;

import java.io.Serializable;
import java.util.ArrayList;

public class MultipartUploadTask extends BaseTask implements Serializable {

    private ArrayList <UploadTask> uploadTasks;

    public State getState(int position) {
        if (uploadTasks.size() > position) {
            return this.getUploadTasks().get(position).getState();
        } else {
            return State.NONE;
        }
    }

    public MultipartUploadTask(ArrayList <UploadTask> uploadTasks) {

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



    public int getProgress(int position) {
        if (uploadTasks.size() > position) {
            return this.getUploadTasks().get(position).getProgress();
        } else {
            return 0;
        }
    }




    @Override
    public String toString() {
        return "MultipartUploadTask{" +
                "uploadTasks=" + uploadTasks +
                ", state=" + state +
                '}';
    }
}
