package com.eajon.my;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eajon.my.base.BaseActivity;
import com.eajon.my.glide.GlideUtils;
import com.github.eajon.RxHttp;
import com.github.eajon.download.DownloadTask;
import com.github.eajon.exception.ApiException;
import com.github.eajon.observer.HttpObserver;
import com.github.eajon.upload.MultipartUploadTask;
import com.github.eajon.upload.UploadTask;
import com.github.eajon.util.LogUtils;
import com.google.gson.Gson;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.loader.ImageLoader;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.threshold.rxbus2.annotation.RxSubscribe;
import com.threshold.rxbus2.util.EventThread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;


public class MainActivity extends BaseActivity {
    ArrayList <UploadTask> uploadTasks = new ArrayList <>();

    File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "WEIXIN" + ".apk");
    File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "QQ" + ".apk");
    String url1 = "http://imtt.dd.qq.com/16891/50CC095EFBE6059601C6FB652547D737.apk?fsname=com.tencent.mm_6.6.7_1321.apk&csr=1bbd";
    String url2 = "http://imtt.dd.qq.com/16891/FC92B1B4471DE5AAD0D009DF9BF1AD01.apk?fsname=com.tencent.mobileqq_7.7.5_896.apk&csr=1bbd";
    DownloadTask downloadTask;
    HttpObserver observer;
    @BindView(R.id.request)
    Button request;
    @BindView(R.id.download)
    Button download;
    @BindView(R.id.upload)
    Button upload;

    @BindView(R.id.content)
    TextView content;

    @BindView(R.id.content1)
    TextView content1;
    @BindView(R.id.content2)
    TextView content2;
    @BindView(R.id.content3)
    TextView content3;


    @Override
    protected int setContentId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initClick() {
        /**
         * 图片选择配置
         */
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(false);  //显示拍照按钮
        imagePicker.setCrop(false);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(3);    //选中数量限制
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        downloadTask = new DownloadTask(file1.getName(), file1.getAbsolutePath(), url1);

    }

    @Override
    protected void initLogic() {

        doRequest();

    }

    private void doRequest() {
//        HashMap map = new HashMap();
//        map.put("city", "常熟");
//        new RxHttp.Builder()
//                .get()
//                .baseUrl("http://wthrcdn.etouch.cn/")
//                .apiUrl("weather_mini")
//                .addParameter(map)
//                .entity(Weather.class)
//                .build()
//                .request();
    }


    @RxSubscribe(observeOnThread = EventThread.MAIN)
    public void weatherCallBack(Weather weather) {
        content.setText(new Gson().toJson(weather));
    }

    @RxSubscribe(observeOnThread = EventThread.MAIN)//异常捕获
    public void weatherCallBack(ApiException e) {
        content.setText(new Gson().toJson(e));
    }

    //下载监听
    @RxSubscribe(observeOnThread = EventThread.MAIN)
    public void downloadProgress(DownloadTask downloadTask) {
        download.setText(downloadTask.getState().toString() + downloadTask.getProgress() + "%");
    }


    //上传监听
    @RxSubscribe(observeOnThread = EventThread.MAIN)
    public void uploadProgress(UploadTask uploadTask) {
        upload.setText(uploadTask.getState().toString() + uploadTask.getProgress() + "%");
    }

    @RxSubscribe(observeOnThread = EventThread.MAIN)
    public void uploadProgress(MultipartUploadTask multipartUploadTask) {
        content.setText("总进度：" + multipartUploadTask.getProgress() + "%" + multipartUploadTask.getState().toString());
        if (multipartUploadTask.getUploadTasks().size() == 3) {
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


        MultipartUploadTask multipartUploadTask = new MultipartUploadTask("mulitTag", uploadTasks);
        /**
         * 发送请求
         */
        new RxHttp.Builder()
                .baseUrl("https://shop.cxwos.com/admin/File/")
                .apiUrl("UploadFile?tentantId=16")
                .multipartUploadTask(multipartUploadTask)
                .lifecycle(this)
                .build()
                .upload();

    }

    @OnClick({R.id.download, R.id.upload, R.id.request})
    public void onViewClicked(View view) {
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
                                        RxHttp rxHttp = new RxHttp.Builder().lifecycle(MainActivity.this).withDialog(MainActivity.this,"下载").downloadTask(downloadTask).build();
                                        observer = new HttpObserver<DownloadTask>() {

                                            @Override
                                            public void onCancelOrPause() {
                                                LogUtils.e("dialog", "onpause");
                                                download.setText(downloadTask.getState().toString() + downloadTask.getProgress() + "%");
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
                Intent intent = new Intent(this, ImageGridActivity.class);
                startActivityForResult(intent, 1000);
                break;
            case R.id.request:

                HashMap map = new HashMap();
                map.put("city", "苏州");
                new RxHttp.Builder()
                        .get()
                        .baseUrl("http://wthrcdn.etouch.cn/")
                        .apiUrl("weather_mini")
                        .addParameter(map)
                        .withDialog(MainActivity.this)
                        .entity(Weather.class)
                        .build()
                        .request(new HttpObserver<Weather>() {
                            @Override
                            public void onCancelOrPause() {

                            }

                            @Override
                            public void onSuccess(Weather weather) {
                                content.setText(new Gson().toJson(weather));
                            }

                            @Override
                            public void onError(ApiException t) {

                            }
                        });
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 1000) {
                ArrayList <ImageItem> images = (ArrayList <ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                /**
                 * 获取选择图片之后上传
                 */
                File file;

                String key;
                String path;
                uploadTasks.clear();
                for (int i = 0; i < images.size(); i++) {
                    UploadTask uploadTask = new UploadTask(images.get(i).name, new File(images.get(i).path));
                    uploadTasks.add(uploadTask);
                }
                try {
                    Thread.sleep(500);//延时0.5秒看效果
                    //上传图片
                    upload(uploadTasks);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * 图片加载器
     */
    public class GlideImageLoader implements ImageLoader {

        @Override
        public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
            Uri uri = Uri.fromFile(new File(path));
            GlideUtils.loadImg(activity, path, imageView);
        }

        @Override
        public void displayImagePreview(Activity activity, String path, ImageView imageView, int width, int height) {
            Uri uri = Uri.fromFile(new File(path));
            GlideUtils.loadImg(activity, path, imageView);
        }

        @Override
        public void clearMemoryCache() {
        }
    }
}
