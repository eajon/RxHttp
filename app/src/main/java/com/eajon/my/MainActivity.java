package com.eajon.my;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.eajon.my.base.BaseActivity;
import com.eajon.my.model.BaseResponse;
import com.eajon.my.model.CommonResponse;
import com.eajon.my.util.PhotoUtils;
import com.eajon.my.util.Weather;
import com.eajon.my.util.ZhihuImagePicker;
import com.eajon.my.widget.CProgressDialog;
import com.github.eajon.RxHttp;
import com.github.eajon.exception.ApiException;
import com.github.eajon.observer.DownloadObserver;
import com.github.eajon.observer.HttpObserver;
import com.github.eajon.observer.UploadObserver;
import com.github.eajon.task.DownloadTask;
import com.github.eajon.task.MultiUploadTask;
import com.github.eajon.task.UploadTask;
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
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.RequestBody;


public class MainActivity extends BaseActivity {
    ArrayList <UploadTask> uploadTasks;
    DownloadTask downloadTask;
    Disposable downloadDisposable;
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
//        WeatherModule2 weatherModule2 = ViewModelProviders.of(this).get(WeatherModule2.class);
//        weatherModule2.getWeather();

    }

    @Override
    protected void initClick() {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        rxImagePicker = RxImagePicker.INSTANCE
                .create(ZhihuImagePicker.class);
        //默认下载地址为Download目录
        downloadTask = new DownloadTask("wechat.apk");

    }

    @Override
    protected void initLogic() {
        doRequest();
    }

    private void doRequest() {
        HashMap map = new HashMap();
        map.put("username", "admin");
        map.put("password", "12345678");
        new RxHttp.Builder()
                .post()
                .apiUrl("user/login")
                .addParameter(map)
                .entity(CommonResponse.class)
                .build()
                .request(new HttpObserver<CommonResponse>() {
                    @Override
                    public void onSuccess(CommonResponse o) {
                        HashMap<String,Object> header=new HashMap<>();
                        header.put("Authorization",o.getData());
                        RxHttp.getConfig().baseHeader(header);
                        content.setText(o.getData().toString());
                    }

                    @Override
                    public void onError(ApiException t) {

                    }
                });
    }

    private void doJsonRequest() {
        BaseResponse baseResponse=new BaseResponse();
        baseResponse.setCode(1);
        baseResponse.setMessage("HAHAH");
        RequestBody body=RequestBody.create(MediaType.parse("application/json"),new Gson().toJson(baseResponse));
        new RxHttp.Builder()
                .post()
                .apiUrl("test/json")
                .setRequestBody(body)
                .entity(CommonResponse.class)
                .build()
                .request(new HttpObserver<CommonResponse>() {
                    @Override
                    public void onSuccess(CommonResponse o) {
                        content.setText(o.getData().toString());
                    }

                    @Override
                    public void onError(ApiException t) {

                    }
                });
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
        LogUtils.d("download1", downloadTask.getState().toString() + downloadTask.getProgress() + "%"+downloadTask.getSpeedKB());
        download.setText(downloadTask.getState().toString() + downloadTask.getProgress() + "%"+downloadTask.getSpeedKB());
    }


    //上传监听
    @RxSubscribe(observeOnThread = EventThread.MAIN)
    @SuppressWarnings("unused")
    public void uploadProgress(UploadTask uploadTask) {
        upload.setText(uploadTask.getState().toString() + uploadTask.getProgress() + "%");
    }

    @RxSubscribe(observeOnThread = EventThread.MAIN, eventId = "upload")
    @SuppressWarnings("unused")
    public void uploadProgress(MultiUploadTask multiUploadTask) {
        content.setText("总进度：" + multiUploadTask.getProgress() + "%" + multiUploadTask.getState().toString()+ multiUploadTask.getSpeedKB());
        if (multiUploadTask.getUploadTasks().size() >= 3) {//假设选择3个
            content1.setText("第一个：" + multiUploadTask.getProgress(0) + "%" + multiUploadTask.getState(0).toString()+ multiUploadTask.getSpeedKB());
            content2.setText("第二个：" + multiUploadTask.getProgress(1) + "%" + multiUploadTask.getState(1).toString()+ multiUploadTask.getSpeedKB());
            content3.setText("第三个：" + multiUploadTask.getProgress(2) + "%" + multiUploadTask.getState(2).toString()+ multiUploadTask.getSpeedKB());
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


    private void upload(ArrayList <UploadTask> uploadTasks) {
        HashMap<String,Object> params=new HashMap<>();
        params.put("folder","gallery");
        params.put("host","gallery");
        params.put("folderId",52L);
        params.put("remark","androidTest");
        MultiUploadTask multiUploadTask = new MultiUploadTask(uploadTasks);
        new RxHttp.Builder()
                .apiUrl("image/upload")
                .task(multiUploadTask)
                .isStick(true)
                .eventId("upload")
                .addParameter(params)
                .lifecycle(MainActivity.this)
                .activityEvent(ActivityEvent.PAUSE)
                .entity(CommonResponse.class)
                .withDialog(new CProgressDialog(MainActivity.this, R.style.CustomDialog))
                .build()
                .upload(new UploadObserver<CommonResponse>() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onSuccess(CommonResponse o) {
                        content.setText(o.getData().toString());
                    }

                    @Override
                    public void onError(ApiException t) {
                        content.setText(t.getDisplayMessage());
                    }
                });

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
                                        downloadDisposable.dispose();
                                        download.setText(downloadTask.getState().toString() + downloadTask.getProgress() + "%");
                                    } else {
                                        downloadDisposable = new RxHttp.Builder()
                                                .baseUrl("http://imtt.dd.qq.com/")
                                                .apiUrl("16891/50CC095EFBE6059601C6FB652547D737.apk?fsname=com.tencent.mm_6.6.7_1321.apk&csr=1bbd")
                                                .lifecycle(MainActivity.this)
                                                .eventId("download")
                                                .withDialog(new CProgressDialog(MainActivity.this, R.style.CustomDialog))
                                                .activityEvent(ActivityEvent.PAUSE)
                                                .task(downloadTask)
                                                .build()
                                                .download(new DownloadObserver <DownloadTask>() {
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
                                                });


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
//               doRequest();
                doJsonRequest();
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
                                            .build())
                                    .compose(MainActivity.this.bindUntilEvent(ActivityEvent.DESTROY))
                                    .subscribe(new Observer <Result>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {
                                            uploadTasks = new ArrayList <>();
                                        }

                                        @Override
                                        public void onNext(Result result) {
                                            uploadTasks.add(new UploadTask("files",new File(PhotoUtils.getPath(MainActivity.this, result.getUri()))));
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
