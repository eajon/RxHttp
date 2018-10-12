package com.github.eajon.upload;

import android.text.TextUtils;

import com.threshold.rxbus2.RxBus;

import java.util.ArrayList;

public class MultipartUploadTask {

    private ArrayList <UploadTask> uploadTasks;

    private UploadTask.State state = UploadTask.State.NONE;//上传状态


    public UploadTask.State getState() {
        return state;
    }

    public UploadTask.State getState(int position) {
        if (uploadTasks.size() > position) {
            return this.getUploadTasks().get(position).getState();
        } else {
            return UploadTask.State.NONE;
        }
    }

    public void setState(UploadTask.State state) {
        this.state = state;
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

    public boolean isFinish() {
        return getProgress() == 100;
    }

    public int getProgress(int position) {
        if (uploadTasks.size() > position) {
            return this.getUploadTasks().get(position).getProgress();
        } else {
            return 0;
        }
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
        return "MultipartUploadTask{" +
                "uploadTasks=" + uploadTasks +
                ", state=" + state +
                '}';
    }
}
