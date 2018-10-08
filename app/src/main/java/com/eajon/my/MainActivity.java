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

import com.bumptech.glide.Glide;
import com.eajon.my.base.BaseActivity;
import com.eajon.my.glide.GlideUtils;

import com.github.eajon.RxHttp;
import com.github.eajon.download.DownloadTask;
import com.github.eajon.observer.DownloadObserver;
import com.github.eajon.observer.HttpObserver;
import com.github.eajon.observer.UploadObserver;
import com.github.eajon.rxbus.RxResponse;
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

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.RequestBody;


public class MainActivity extends BaseActivity {
    ArrayList <UploadTask> uploadTasks = new ArrayList <>();

    File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "WEIXIN" + ".apk");
    File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "QQ" + ".apk");
    String url1 = "http://imtt.dd.qq.com/16891/50CC095EFBE6059601C6FB652547D737.apk?fsname=com.tencent.mm_6.6.7_1321.apk&csr=1bbd";
    String url2 = "http://imtt.dd.qq.com/16891/FC92B1B4471DE5AAD0D009DF9BF1AD01.apk?fsname=com.tencent.mobileqq_7.7.5_896.apk&csr=1bbd";
    DownloadTask downloadTask;
    DownloadObserver observer;
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


        String requestBody = new Gson().toJson(new RequestUid("d967b31e-4b8e-42e3-8634-1f9ee8422287"));
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestBody);
        new RxHttp.Builder().baseUrl("http://139.224.61.120:8086/api/").apiUrl("getSceneList").setRequestBody(body).build().request(new HttpObserver() {
            @Override
            protected void onSuccess(Object o) {

            }

            @Override
            protected void onError(String t) {

            }
        });

    }

    public class RequestUid {

        String user_id;

        public RequestUid(String user_id) {
            this.user_id = user_id;
        }
    }


    @Override
    public void onResponse(RxResponse response) {
        if (response.getTag().equals(file1.getName())) {
            DownloadTask downloadTask = (DownloadTask) response.getData();
            download.setText(downloadTask.getState().toString() + downloadTask.getProgress() + "%");
        }

        LogUtils.e(RxHttp.getConfig().getLogTag(), response.getTag() + "1");
        LogUtils.e(RxHttp.getConfig().getLogTag(), uploadTasks.get(0).getTag() + "2");

        if (response.getTag().equals("mulitTag")) {

            MultipartUploadTask multipartUploadTask = (MultipartUploadTask) response.getData();
            content.setText("总进度：" + multipartUploadTask.getProgress() + "%" + multipartUploadTask.getState().toString());
            if (multipartUploadTask.getUploadTasks().size() == 3) {
                content1.setText("第一个：" + multipartUploadTask.getProgress(0) + "%" + multipartUploadTask.getState(0).toString());
                content2.setText("第二个：" + multipartUploadTask.getProgress(1) + "%" + multipartUploadTask.getState(1).toString());
                content3.setText("第三个：" + multipartUploadTask.getProgress(2) + "%" + multipartUploadTask.getState(2).toString());
            }
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
                .upload(new UploadObserver() {
                    @Override
                    protected void onSuccess(Object o) {

                    }

                    @Override
                    protected void onError(String t) {

                    }
                });

    }

    @OnClick({R.id.download, R.id.upload})
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
                                        downloadTask.setState(DownloadTask.State.PAUSE);
                                        download.setText(downloadTask.getState().toString() + downloadTask.getProgress() + "%");
                                        observer.dispose();
                                    } else {
                                        RxHttp rxHttp = new RxHttp.Builder().lifecycle(MainActivity.this).downloadTask(downloadTask).build();
                                        observer = new DownloadObserver() {
                                            @Override
                                            protected void onSuccess(DownloadTask downloadTask) {
                                                LogUtils.e(RxHttp.getConfig().getLogTag(), downloadTask.getState());
                                            }

                                            @Override
                                            protected void onError(String t) {

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
