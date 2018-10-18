package com.eajon.my;

import android.os.Bundle;
import android.widget.TextView;

import com.eajon.my.base.BaseActivity;
import com.eajon.my.util.Weather;
import com.github.eajon.download.DownloadTask;
import com.github.eajon.upload.MultipartUploadTask;
import com.github.eajon.util.LogUtils;
import com.google.gson.Gson;
import com.threshold.rxbus2.annotation.RxSubscribe;
import com.threshold.rxbus2.util.EventThread;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SecondActivity extends BaseActivity {


    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.content1)
    TextView content1;
    @BindView(R.id.content2)
    TextView content2;

    @Override
    protected int setContentId() {
        return R.layout.activity_second;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initClick() {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initLogic() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @RxSubscribe(observeOnThread = EventThread.MAIN, isSticky = true, eventId = "download")
    private void download(DownloadTask downloadTask) {
        LogUtils.d("download", downloadTask.getProgress());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                content.setText("下载进度：" + downloadTask.getProgress() + "%" + downloadTask.getState().toString());
            }
        });

    }


    @RxSubscribe(observeOnThread = EventThread.MAIN, isSticky = true, eventId = "upload")
    @SuppressWarnings("unused")
    public void uploadProgress(MultipartUploadTask multipartUploadTask) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                content1.setText("上传进度：" + multipartUploadTask.getProgress() + "%" + multipartUploadTask.getState().toString());
            }
        });
    }


    @RxSubscribe(observeOnThread = EventThread.MAIN, isSticky = true, eventId = "weather")
    public void weatherCallBack(Weather weather) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                content2.setText("请求返回" + new Gson().toJson(weather));
            }
        });
    }
}
