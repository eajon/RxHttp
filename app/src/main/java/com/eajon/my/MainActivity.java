package com.eajon.my;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eajon.my.base.BaseActivity;
import com.eajon.my.model.BaseResponse;
import com.eajon.my.model.CommonResponse;
import com.eajon.my.model.SysLoginModel;
import com.eajon.my.model.Token2;
import com.eajon.my.util.PhotoUtils;
import com.eajon.my.util.ZhihuImagePicker;
import com.eajon.my.widget.CProgressDialog;
import com.github.eajon.RxHttp;
import com.github.eajon.exception.ApiException;
import com.github.eajon.observer.DownloadObserver;
import com.github.eajon.observer.HttpObserver;
import com.github.eajon.observer.UploadObserver;
import com.github.eajon.task.BaseTask;
import com.github.eajon.task.DownloadTask;
import com.github.eajon.task.MultiUploadTask;
import com.github.eajon.task.UploadTask;
import com.github.eajon.util.LoggerUtils;
import com.github.eajon.util.RxUtils;
import com.qingmei2.rximagepicker.core.RxImagePicker;
import com.qingmei2.rximagepicker.entity.Result;
import com.qingmei2.rximagepicker_extension.MimeType;
import com.qingmei2.rximagepicker_extension_zhihu.ZhihuConfigurationBuilder;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle3.android.ActivityEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class MainActivity extends BaseActivity {
    ArrayList<UploadTask> uploadTasks;
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
    @BindView(R.id.progressbar)
    ProgressBar progressbar;

    private ZhihuImagePicker rxImagePicker;


    @Override
    protected int setContentId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {

//        WeatherModule2.getWeather().request(new HttpObserver<Weather>() {
//            @Override
//            public void onSuccess(Weather weather) {
//                content.setText(new Gson().toJson(weather));
//            }
//
//            @Override
//            public void onError(ApiException exception) {
//
//            }
//        });

    }

    @Override
    protected void initClick() {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        rxImagePicker = RxImagePicker.create(ZhihuImagePicker.class);
        //默认下载地址为Download目录
        downloadTask = new DownloadTask();


    }

    @Override
    protected void initLogic() {
        doRequest();
//        doRequest2();
    }

    private void doRequest() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("username", "admin");
        hashMap.put("password", "12345678");
        SysLoginModel sysLoginModel = new SysLoginModel();
        sysLoginModel.setUsername("admin");
        sysLoginModel.setPassword("12345678");
        new RxHttp.Builder()
                .post("user/login")
                .addTypeParameter(sysLoginModel)
                .withView(progressbar)
                .build()
                .request(new HttpObserver<Token2>() {
                    @Override
                    public void onSuccess(Token2 token) {
                        HashMap<String, Object> header = new HashMap<>();
                        header.put("Authorization", token.getData());
                        RxHttp.getConfig().baseHeader(header);
                        content.setText(token.getData());
                    }

                    @Override
                    public void onError(ApiException t) {

                    }
                });
    }

    private void doRequest2() {
        new RxHttp.Builder()
                .baseUrl("http://photo.renren.com")
                .post("photo/252750583/album-278460783/private/ajax")
                .build()
                .request(new HttpObserver<CommonResponse>() {
                    @Override
                    public void onSuccess(CommonResponse o) {
                        content.setText(o.getResult().toString());
                    }

                    @Override
                    public void onError(ApiException t) {

                    }
                });
    }

    private void doJsonRequest() {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(1);
        baseResponse.setMessage("HAHAH");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("folderName", "3333");
//        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(baseResponse));
        new RxHttp.Builder()
                .post("test/json")
//                .setRequestBody(body)
                .addParameter(hashMap)
//                .cacheKey("tets")
                .build()
                .request(new HttpObserver() {
                    @Override
                    public void onSuccess(Object o) {
                        content.setText(o.toString() + "");
                    }

                    @Override
                    public void onError(ApiException t) {

                    }
                });
    }

    private void doProfile() {
//        Type type = new TypeToken<CommonResponse<Profile>>() {
//        }.getType();
        new RxHttp.Builder()
                .post("test/get")
                .build()
                .request(new HttpObserver<CommonResponse<String>>() {
                    @Override
                    public void onSuccess(CommonResponse<String> profile) {
                        content.setText(profile.getResult());
                    }

                    @Override
                    public void onError(ApiException t) {
                        LoggerUtils.error(t.getBodyMessage());
                    }
                });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    //multipart表单提交
    private void upload(ArrayList<UploadTask> uploadTasks) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("folder", "gallery");
        params.put("host", "gallery");
        params.put("folderId", 52L);
        params.put("remark", "androidTest");
        MultiUploadTask multiUploadTask = new MultiUploadTask(uploadTasks);
        new RxHttp.Builder()
                .post("image/upload")
                .upload(multiUploadTask)
                .tag("upload")
                .addParameter(params)
                .lifecycle(MainActivity.this,ActivityEvent.PAUSE)
                .withDialog(new CProgressDialog(MainActivity.this, R.style.CustomDialog))
                .build()
                .request(new UploadObserver<BaseResponse>() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onProgress(BaseTask uploadTask) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                content.setText(uploadTask.getProgress() + "");
                            }
                        });
                    }

                    @Override
                    public void onSuccess(BaseResponse o) {
                        content.setText(o.getMessage().toString());
                    }

                    @Override
                    public void onError(ApiException t) {
                        content.setText(t.getBodyMessage());
                    }
                });

    }


    //oct-stream提交
    private void upload(UploadTask uploadTask) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("folder", "gallery");
        params.put("host", "gallery");
        params.put("folderId", 52L);
        params.put("remark", "androidTest");

        new RxHttp.Builder()
                .post("image/upload2")
                .uploadByStream(uploadTask)
                .tag("upload")
                .addParameter(params)
                .lifecycle(MainActivity.this,ActivityEvent.PAUSE)
                .withDialog(new CProgressDialog(MainActivity.this, R.style.CustomDialog))
                .build()
                .request(new UploadObserver<BaseResponse>() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onProgress(BaseTask uploadTask) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                content.setText(uploadTask.getProgress() + "");
                            }
                        });
                    }

                    @Override
                    public void onSuccess(BaseResponse o) {
                        content.setText(o.getMessage().toString());
                    }

                    @Override
                    public void onError(ApiException t) {
                        content.setText(t.getBodyMessage());
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
                        .subscribe(new Consumer<Permission>() {
                            @Override
                            public void accept(Permission permission) throws Exception {
                                if (permission.granted) {
                                    if (downloadTask.getState() == DownloadTask.State.LOADING) {
                                        //直接取消
                                        downloadDisposable.dispose();
                                        //或者可以使用管理器取消
                                        //RxManager.getDownloadManager().cancel("download");
                                        download.setText(downloadTask.getState().toString() + downloadTask.getProgress() + "%");
                                    } else {
                                        Map<String, Object> map = new HashMap();
                                        map.put("fsname", "com.tencent.mm_6.6.7_1321.apk");
                                        map.put("csr", "1bbd");
                                        downloadDisposable = new RxHttp.Builder()
                                                .baseUrl("http://imtt.dd.qq.com/")
                                                .get("16891/50CC095EFBE6059601C6FB652547D737.apk")
                                                .lifecycle(MainActivity.this,ActivityEvent.PAUSE)
                                                .tag("download")
                                                .get()
                                                .addParameter(map)
//                                                .withDialog(new CProgressDialog(MainActivity.this, R.style.CustomDialog))
                                                .withView(progressbar)
                                                .download(downloadTask)
                                                .build()
                                                .request(new DownloadObserver() {
                                                    @Override
                                                    public void onPause(DownloadTask downloadTask) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                LoggerUtils.info("download1", downloadTask.getState().toString() + downloadTask.getProgress() + "%" + downloadTask.getSpeedFormat());
                                                                download.setText(downloadTask.getState().toString() + downloadTask.getProgress() + "%" + downloadTask.getSpeedFormat() + "平均速度：" + downloadTask.getAverageSpeedFormat() + "用时：" + downloadTask.getDuration() + "速度：" + downloadTask.getAverageSpeed());
                                                            }
                                                        });
                                                        LoggerUtils.info("onPause", downloadTask.getProgress());
                                                    }

                                                    @Override
                                                    public void onProgress(DownloadTask downloadTask) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                LoggerUtils.info("download1", downloadTask.getState().toString() + downloadTask.getProgress() + "%" + downloadTask.getSpeedFormat());
                                                                download.setText(downloadTask.getState().toString() + downloadTask.getProgress() + "%" + downloadTask.getSpeedFormat() + "平均速度：" + downloadTask.getAverageSpeedFormat() + "用时：" + downloadTask.getDuration() + "速度：" + downloadTask.getAverageSpeed());
                                                            }
                                                        });

                                                    }

                                                    @Override
                                                    public void onSuccess(DownloadTask downloadTask) {
                                                        LoggerUtils.info(downloadTask.getState().name());
                                                    }

                                                    @Override
                                                    public void onError(ApiException t) {
                                                        LoggerUtils.error(downloadTask.getState().name());
                                                    }
                                                });
                                        //加入管理器
                                        //RxManager.getDownloadManager().add("download",downloadDisposable);


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
//                doRequest2();
                doJsonRequest();
//                doProfile();
//                WeatherModule2.getWeather().request(new HttpObserver<Weather>() {
//                    @Override
//                    public void onSuccess(Weather weather) {
//                        content.setText(new Gson().toJson(weather));
//                    }
//
//                    @Override
//                    public void onError(ApiException exception) {
//
//                    }
//                });
                break;
            case R.id.stick:
                intent = new Intent(this, SecondActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void requestCameraPermissions() {
        new RxPermissions(this)
                .requestEachCombined(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>() {
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
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            rxImagePicker.openGallery(MainActivity.this,
                                    new ZhihuConfigurationBuilder(MimeType.INSTANCE.ofAll(), false)
                                            .maxSelectable(9)
                                            .countable(true)
                                            .spanCount(3)
                                            .theme(R.style.Zhihu_Normal)
                                            .build())
                                    .compose(RxUtils.lifeCycle(MainActivity.this,ActivityEvent.DESTROY,null))
                                    .compose(RxUtils.ioMain())
                                    .subscribe(new Observer<Result>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {
                                            uploadTasks = new ArrayList<>();
                                        }

                                        @Override
                                        public void onNext(Result result) {
                                            uploadTasks.add(new UploadTask("files", new File(PhotoUtils.getPath(MainActivity.this, result.getUri()))));
                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }

                                        @Override
                                        public void onComplete() {
                                            if (uploadTasks.size() > 1) {
                                                upload(uploadTasks);
                                            }else
                                            {
                                                upload(uploadTasks.get(0));
                                            }
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
    }
}
