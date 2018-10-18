package com.eajon.my;


import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.eajon.my.base.BaseActivity;
import com.eajon.my.glide.GlideUtils;
import com.eajon.my.util.PhotoUtils;
import com.eajon.my.util.Weather;
import com.eajon.my.util.ZhihuImagePicker;
import com.eajon.my.viewModel.WeatherModule;
import com.eajon.my.viewModel.WeatherModule2;
import com.github.eajon.RxHttp;
import com.github.eajon.download.DownloadTask;
import com.github.eajon.exception.ApiException;
import com.github.eajon.observer.DownloadObserver;
import com.github.eajon.observer.HttpObserver;
import com.github.eajon.upload.MultipartUploadTask;
import com.github.eajon.upload.UploadTask;
import com.github.eajon.util.LogUtils;
import com.google.gson.Gson;
import com.qingmei2.rximagepicker.core.RxImagePicker;
import com.qingmei2.rximagepicker.entity.Result;
import com.qingmei2.rximagepicker_extension.MimeType;
import com.qingmei2.rximagepicker_extension_zhihu.ZhihuConfigurationBuilder;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.threshold.rxbus2.RxBus;
import com.threshold.rxbus2.annotation.RxSubscribe;
import com.threshold.rxbus2.util.EventThread;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;


public class MainActivity extends BaseActivity {
    ArrayList <UploadTask> uploadTasks;

    File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "WEIXIN" + ".apk");
    String url1 = "http://imtt.dd.qq.com/16891/50CC095EFBE6059601C6FB652547D737.apk?fsname=com.tencent.mm_6.6.7_1321.apk&csr=1bbd";
    DownloadTask downloadTask;
    HttpObserver observer;
    @BindView(R.id.request)
    Button request;
    @BindView(R.id.download)
    Button download;
    @BindView(R.id.upload)
    Button upload;
    @BindView(R.id.stick)
    Button stick;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.content1)
    TextView content1;
    @BindView(R.id.content2)
    TextView content2;
    @BindView(R.id.content3)
    TextView content3;

    private ZhihuImagePicker rxImagePicker;


    @Override
    protected int setContentId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        //官方MVVM
//        WeatherModule weatherModule= ViewModelProviders.of(this).get(WeatherModule.class);
//        weatherModule.getWeather().observe(this, new android.arch.lifecycle.Observer <Weather>() {
//            @Override
//            public void onChanged(@Nullable Weather weather) {
//                content.setText(new Gson().toJson(weather));
//            }
//        });
        //RxHttp MVVM
        WeatherModule2 weatherModule2 = ViewModelProviders.of(this).get(WeatherModule2.class);
        weatherModule2.getWeather();

    }

    @Override
    protected void initClick() {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        rxImagePicker = RxImagePicker.INSTANCE
                .create(ZhihuImagePicker.class);
        downloadTask = new DownloadTask(file1.getName(), file1.getAbsolutePath(), url1);

    }

    @Override
    protected void initLogic() {
//        doRequest();
    }

    private void doRequest() {
        HashMap map = new HashMap();
        map.put("city", "常熟");
        new RxHttp.Builder()
                .get()
                .baseUrl("http://wthrcdn.etouch.cn/")
                .apiUrl("weather_mini")
                .addParameter(map)
                .entity(Weather.class)
                .eventId("weather")
                .isStick(true)
                .build()
                .request();
    }


    @RxSubscribe(observeOnThread = EventThread.IO, isSticky = true, eventId = "weather")
    public void weatherCallBack(Weather weather) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                content.setText(new Gson().toJson(weather));
            }
        });

    }

    @RxSubscribe(observeOnThread = EventThread.MAIN, isSticky = true, eventId = "weather")//异常捕获
    public void weatherCallBack(ApiException e) {
        content.setText(new Gson().toJson(e));
    }

    //下载监听
    @RxSubscribe(observeOnThread = EventThread.MAIN, eventId = "download")
    @SuppressWarnings("unused")
    public void downloadProgress(DownloadTask downloadTask) {
        download.setText(downloadTask.getState().toString() + downloadTask.getProgress() + "%");
    }


    //上传监听
    @RxSubscribe(observeOnThread = EventThread.MAIN)
    @SuppressWarnings("unused")
    public void uploadProgress(UploadTask uploadTask) {
        upload.setText(uploadTask.getState().toString() + uploadTask.getProgress() + "%");
    }

    @RxSubscribe(observeOnThread = EventThread.MAIN, eventId = "upload")
    @SuppressWarnings("unused")
    public void uploadProgress(MultipartUploadTask multipartUploadTask) {
        content.setText("总进度：" + multipartUploadTask.getProgress() + "%" + multipartUploadTask.getState().toString());
        if (multipartUploadTask.getUploadTasks().size() >= 3) {//假设选择3个
            content1.setText("第一个：" + multipartUploadTask.getProgress(0) + "%" + multipartUploadTask.getState(0).toString());
            content2.setText("第二个：" + multipartUploadTask.getProgress(1) + "%" + multipartUploadTask.getState(1).toString());
            content3.setText("第三个：" + multipartUploadTask.getProgress(2) + "%" + multipartUploadTask.getState(2).toString());
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


    private void upload(ArrayList <UploadTask> uploadTasks) {
        MultipartUploadTask multipartUploadTask = new MultipartUploadTask(uploadTasks);
        new RxHttp.Builder()
                .baseUrl("https://shop.cxwos.com/admin/File/")
                .apiUrl("UploadFile?tentantId=16")
                .multipartUploadTask(multipartUploadTask)
                .isStick(true)
                .eventId("upload")
                .build()
                .upload();

    }

    @OnClick({R.id.download, R.id.upload, R.id.request, R.id.stick})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.download:
                new RxPermissions(MainActivity.this)
                        .requestEachCombined(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .subscribe(new Consumer <Permission>() {
                            @Override
                            public void accept(Permission permission) throws Exception {
                                if (permission.granted) {
                                    if (downloadTask.getState() == DownloadTask.State.LOADING) {
                                        observer.dispose();
                                        download.setText(downloadTask.getState().toString() + downloadTask.getProgress() + "%");
                                    } else {
                                        RxHttp rxHttp = new RxHttp.Builder().isStick(true).eventId("download").withDialog(MainActivity.this).downloadTask(downloadTask).build();
                                        observer = new DownloadObserver <DownloadTask>() {

                                            @Override
                                            public void onPause(DownloadTask downloadTask) {
                                                LogUtils.d("onPause", downloadTask.getProgress());
                                            }

                                            @Override
                                            public void onSuccess(DownloadTask downloadTask) {
                                                LogUtils.e(RxHttp.getConfig().getLogTag(), downloadTask.getState());
                                            }

                                            @Override
                                            public void onError(ApiException t) {
                                                LogUtils.e("dialog", "onError");
                                                LogUtils.e(RxHttp.getConfig().getLogTag(), downloadTask.getState());
                                            }

                                        };
                                        rxHttp.download(observer);
                                    }
                                } else if (permission.shouldShowRequestPermissionRationale) {

                                    // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                                    Log.d("permission", permission.name + " is denied. More info should be provided.");
                                } else {

                                    // 用户拒绝了该权限，并且选中『不再询问』
                                    Log.d("permission", permission.name + " is denied.");
                                }
                            }
                        });
                break;
            case R.id.upload:
                requestGalleryPermissions();
                break;
            case R.id.request:

                HashMap map = new HashMap();
                map.put("city", "上海");
                new RxHttp.Builder()
                        .get()
                        .baseUrl("http://wthrcdn.etouch.cn/")
                        .apiUrl("weather_mini")
                        .addParameter(map)
                        .eventId("weather")
                        .withDialog(MainActivity.this)
                        .entity(Weather.class)
                        .isStick(true)
                        .build()
                        .request();
                break;
            case R.id.stick:
                intent = new Intent(this, SecondActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void requestCameraPermissions() {
        new RxPermissions(this)
                .requestEachCombined(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer <Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {

                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            Log.d("permission", permission.name + " is denied. More info should be provided.");
                        } else {

                            // 用户拒绝了该权限，并且选中『不再询问』
                            Log.d("permission", permission.name + " is denied.");
                        }
                    }
                });
    }

    private void requestGalleryPermissions() {
        new RxPermissions(this)
                .requestEachCombined(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer <Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            rxImagePicker.openGallery(MainActivity.this,
                                    new ZhihuConfigurationBuilder(MimeType.INSTANCE.ofImage(), false)
                                            .maxSelectable(9)
                                            .countable(true)
                                            .spanCount(3)
                                            .theme(R.style.Zhihu_Normal)
                                            .build()).compose(MainActivity.this.bindUntilEvent(ActivityEvent.DESTROY))
                                    .subscribe(new Observer <Result>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {
                                            uploadTasks = new ArrayList <>();
                                        }

                                        @Override
                                        public void onNext(Result result) {
                                            uploadTasks.add(new UploadTask(new File(PhotoUtils.getPath(MainActivity.this, result.getUri()))));
                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }

                                        @Override
                                        public void onComplete() {
                                            if (uploadTasks.size() > 0)
                                                upload(uploadTasks);
                                        }
                                    });
                        } else if (permission.shouldShowRequestPermissionRationale) {

                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            Log.d("permission", permission.name + " is denied. More info should be provided.");
                        } else {

                            // 用户拒绝了该权限，并且选中『不再询问』
                            Log.d("permission", permission.name + " is denied.");
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().reset();
    }
}
