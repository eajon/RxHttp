package com.eajon.my;

import android.os.Bundle;
import android.widget.TextView;

import com.eajon.my.base.BaseActivity;
import com.eajon.my.model.Weather;
import com.github.eajon.task.DownloadTask;
import com.github.eajon.task.MultiUploadTask;
import com.github.eajon.util.LoggerUtils;
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

    @RxSubscribe(observeOnThread = EventThread.MAIN, isSticky = true, tag = "download")
    private void download(DownloadTask downloadTask) {
        LoggerUtils.info("download", downloadTask.getProgress());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                content.setText("下载进度：" + downloadTask.getProgress() + "%" + downloadTask.getState().toString());
            }
        });

    }


    @RxSubscribe(observeOnThread = EventThread.MAIN, isSticky = true, tag = "upload")
    @SuppressWarnings("unused")
    public void uploadProgress(MultiUploadTask multiUploadTask) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                content1.setText("上传进度：" + multiUploadTask.getProgress() + "%" + multiUploadTask.getState().toString());
            }
        });
    }


    @RxSubscribe(observeOnThread = EventThread.MAIN, isSticky = true, tag = "weather")
    public void weatherCallBack(Weather weather) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                content2.setText("请求返回" + new Gson().toJson(weather));
            }
        });
    }

    @RxSubscribe(observeOnThread = EventThread.MAIN, isSticky = true, tag = "weather")
    public void weatherCallBack(String weather) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                content2.setText("请求返回" + weather);
            }
        });
    }
}
