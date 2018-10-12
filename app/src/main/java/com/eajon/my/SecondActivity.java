package com.eajon.my;

import android.os.Bundle;
import android.widget.TextView;

import com.eajon.my.base.BaseActivity;
import com.github.eajon.download.DownloadTask;
import com.github.eajon.util.LogUtils;
import com.threshold.rxbus2.annotation.RxSubscribe;
import com.threshold.rxbus2.util.EventThread;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SecondActivity extends BaseActivity {


    @BindView(R.id.content)
    TextView content;

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

    @RxSubscribe(observeOnThread = EventThread.MAIN, isSticky = true)
    private void download(DownloadTask downloadTask) {
        LogUtils.d("download",downloadTask.getProgress());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                content.setText(downloadTask.getProgress() + "%");
            }
        });

    }
}
