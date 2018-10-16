package com.github.eajon;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;


import com.github.eajon.exception.ApiException;
import com.github.eajon.function.DownloadResponseFunction;
import com.github.eajon.function.ErrorResponseFunction;
import com.github.eajon.function.HttpResponseFunction;
import com.github.eajon.download.DownloadInterceptor;
import com.github.eajon.download.DownloadTask;
import com.github.eajon.observer.DownloadObserver;
import com.github.eajon.observer.HttpObserver;
import com.github.eajon.observer.UploadObserver;
import com.github.eajon.retrofit.Method;
import com.github.eajon.retrofit.RxConfig;
import com.github.eajon.upload.MultipartUploadTask;
import com.github.eajon.upload.UploadRequestBody;
import com.github.eajon.upload.UploadTask;
import com.github.eajon.util.LogUtils;
import com.github.eajon.util.NetUtils;
import com.github.eajon.util.RetrofitUtils;
import com.threshold.rxbus2.RxBus;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Http请求类
 *
 * @author wengyijiong
 */
public class RxHttp {

    /*请求方式*/
    private Method method;
    /*请求参数*/
    private Map <String, Object> parameter;
    /*header*/
    private Map <String, Object> header;
    /*json参数*/
    private RequestBody requestBody;
    /*LifecycleProvider*/
    private LifecycleProvider lifecycle;
    /*ActivityEvent*/
    private ActivityEvent activityEvent;
    /*FragmentEvent*/
    private FragmentEvent fragmentEvent;
    /*上传任务列表*/
    private UploadTask uploadTask;
    /*上传任务列表*/
    private MultipartUploadTask multipartUploadTask;
    /*entity*/
    private DownloadTask downloadTask;
    /*基础URL*/
    private String baseUrl;
    /*apiUrl*/
    private String apiUrl;
    /*entity*/
    private Class <?> clazz;
    /*HTTP回调*/
    private HttpObserver httpObserver;
    /*RxDialog提供Context*/
    private Context context;
    /*dialog提示*/
    private String message;
    /*dialog是否默认可以取消*/
    private boolean cancelable;
    /*Retrofit observable */
    Observable observable;
    /*自定义对话框*/
    private ProgressDialog progressDialog;
    /*是否是粘性消息*/
    private boolean isStick;

    /*rxBus发射标识 不配置直接获取 配置了需要配置注解eventId*/
    String eventId;


    /*构造函数*/
    private RxHttp(Builder builder) {
        this.parameter = builder.parameter;
        this.header = builder.header;
        this.lifecycle = builder.lifecycle;
        this.activityEvent = builder.activityEvent;
        this.fragmentEvent = builder.fragmentEvent;
        this.uploadTask = builder.uploadTask;
        this.multipartUploadTask = builder.multipartUploadTask;
        this.baseUrl = builder.baseUrl;
        this.apiUrl = builder.apiUrl;
        this.method = builder.method;
        this.clazz = builder.clazz;
        this.requestBody = builder.requestBody;
        this.downloadTask = builder.downloadTask;
        this.context = builder.context;
        this.message = builder.message;
        this.cancelable = builder.cancelable;
        this.progressDialog = builder.progressDialog;
        this.isStick = builder.isStick;
        this.eventId = builder.eventId;
    }

    public static RxConfig getConfig() {
        return RxConfig.get();
    }


    /*普通Http请求*/
    public void request(HttpObserver httpObserver) {
        this.httpObserver = httpObserver;
        if (httpObserver == null) {
            throw new NullPointerException("Observer must not null!");
        } else {
            doRequest();
        }
    }

    /*普通Http请求*/
    public void request() {
        doRequest();
    }

    /*上传文件请求*/
    public void upload(HttpObserver httpObserver) {
        this.httpObserver = httpObserver;
        if (httpObserver == null) {
            throw new NullPointerException("UploadObserve must not null!");
        } else {
            if (uploadTask == null && multipartUploadTask == null) {
                throw new NullPointerException("UploadTask must not null!");
            } else {
                doUpload();
            }

        }
    }

    public void upload() {
        if (uploadTask == null && multipartUploadTask == null) {
            throw new NullPointerException("UploadTask must not null!");
        } else {
            doUpload();
        }
    }

    /*下载文件请求*/
    public void download(HttpObserver httpObserver) {
        this.httpObserver = httpObserver;
        if (httpObserver == null) {
            throw new NullPointerException("DownloadObserver must not null!");
        } else {
            if (downloadTask == null) {
                throw new NullPointerException("DownloadTask must not null!");
            } else {
                doDownload();
            }
        }
    }

    public void download() {
        if (downloadTask == null) {
            throw new NullPointerException("DownloadTask must not null!");
        } else {
            doDownload();
        }
    }


    /*执行请求*/
    private void doRequest() {
        /*header处理*/
        disposeHeader();

        /*Parameter处理*/
        disposeParameter();

        /*请求方式处理*/
        if (method == null) {
            method = Method.POST;
        }
        switch (method) {
            case GET:
                observable = RetrofitUtils.get().getRetrofit(getBaseUrl()).get(disposeApiUrl(), parameter, header);
                break;
            case POST:
                if (requestBody != null) {
                    observable = RetrofitUtils.get().getRetrofit(getBaseUrl()).post(disposeApiUrl(), requestBody, header);
                } else {
                    observable = RetrofitUtils.get().getRetrofit(getBaseUrl()).post(disposeApiUrl(), parameter, header);
                }
                break;
            case DELETE:
                observable = RetrofitUtils.get().getRetrofit(getBaseUrl()).delete(disposeApiUrl(), parameter, header);
                break;
            case PUT:
                observable = RetrofitUtils.get().getRetrofit(getBaseUrl()).put(disposeApiUrl(), parameter, header);
                break;
            default:
                observable = RetrofitUtils.get().getRetrofit(getBaseUrl()).post(disposeApiUrl(), parameter, header);
                break;
        }
        subscribe();
    }

    /*执行文件上传*/
    private void doUpload() {
        /*header处理*/
        disposeHeader();

        /*Parameter处理*/
        disposeParameter();

        /*处理文件*/
        List <MultipartBody.Part> partList = new ArrayList <>();
        File file;
        RequestBody requestBody;
        if (uploadTask != null) {
            file = uploadTask.getFile();
            requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData(uploadTask.getFileName(), file.getName(), new UploadRequestBody(requestBody, eventId, isStick, uploadTask));
            observable = RetrofitUtils.get().getRetrofit(getBaseUrl()).upload(disposeApiUrl(), parameter, header, part);

        } else {
            for (int i = 0; i < multipartUploadTask.getUploadTasks().size(); i++) {
                UploadTask task = multipartUploadTask.getUploadTasks().get(i);
                file = task.getFile();
                requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part part = MultipartBody.Part.createFormData(task.getFileName(), file.getName(), new UploadRequestBody(requestBody, eventId, isStick, task, multipartUploadTask));
                partList.add(part);
            }
            observable = RetrofitUtils.get().getRetrofit(getBaseUrl()).upload(disposeApiUrl(), parameter, header, partList);

        }

        /*请求处理*/
        subscribe();
    }

    private void doDownload() {


        observable = RetrofitUtils.get().getRetrofit(NetUtils.getBaseUrl(downloadTask.getServerUrl()), new DownloadInterceptor(eventId, isStick, downloadTask)).download(downloadTask.getServerUrl(), "bytes=" + downloadTask.getCurrentSize() + "-");
        /*请求处理*/
        subscribe();


    }

    private void subscribe() {
        if (httpObserver != null) {
            if (progressDialog != null || context != null) {
                dialogObserver().subscribe(httpObserver);
            } else {
                observe().subscribe(httpObserver);
            }
        } else {
            observe().subscribe(new HttpObserver() {
                @Override
                public void onSuccess(Object o) {
                    sendBus(eventId, isStick, o);
                }

                @Override
                public void onError(ApiException t) {
                    sendBus(eventId, isStick, t);
                }

            });
        }
    }


    /* compose 操作符 介于 map onErrorResumeNext */
    private Observable compose() {
        if (lifecycle != null) {
            if (activityEvent != null || fragmentEvent != null) {
                //两个同时存在,以 activity 为准
                if (activityEvent != null && fragmentEvent != null) {
                    return map().compose(lifecycle.bindUntilEvent(activityEvent));
                }
                if (activityEvent != null) {
                    return map().compose(lifecycle.bindUntilEvent(activityEvent));
                }
                if (fragmentEvent != null) {
                    return map().compose(lifecycle.bindUntilEvent(fragmentEvent));
                }
            } else {
                return map().compose(lifecycle.bindToLifecycle());
            }
        }
        return map();
    }


    private Observable map() {
        if (downloadTask != null) {
            return observable.map(new DownloadResponseFunction(downloadTask));
        } else {
            return observable.map(new HttpResponseFunction(clazz));
        }
    }


    private Observable dialogObserver() {

        return Observable.using(new Callable <ProgressDialog>() {
            @Override
            public ProgressDialog call() {
                if (progressDialog != null) {
                    progressDialog.show();
                    return progressDialog;
                }
                return ProgressDialog.show(context, null, message, true, cancelable);
            }
        }, new Function <ProgressDialog, Observable <? extends Object>>() {
            @Override
            public Observable <? extends Object> apply(final ProgressDialog progressDialog) throws Exception {
                return observe().doOnSubscribe(new Consumer <Disposable>() {
                    @Override
                    public void accept(final Disposable disposable) throws Exception {
                        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                LogUtils.e("dialog", "dismiss");
                                disposable.dispose();
                            }
                        });
                    }
                });
            }
        }, new Consumer <ProgressDialog>() {
            @Override
            public void accept(ProgressDialog progressDialog) throws Exception {
                LogUtils.e("dialog", "accept");
                progressDialog.dismiss();
            }
        });
    }


    //    /*线程设置*/
    public Observable observe() {

        return compose().onErrorResumeNext(new ErrorResponseFunction <>())
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
//                        LogUtils.e("dialog", "doOnDispose");
                    }
                }).doOnLifecycle(new Consumer <Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        LogUtils.e("dialog", "doOnLifecycle");
                        if (downloadTask != null) {
                            downloadTask.setState(DownloadTask.State.LOADING);
                            downloadTask.sendBus(eventId, isStick);
                        }
                        if (uploadTask != null) {
                            uploadTask.setState(UploadTask.State.LOADING);
                            uploadTask.sendBus(eventId, isStick);
                        }
                        if (multipartUploadTask != null) {
                            multipartUploadTask.setState(UploadTask.State.LOADING);
                            multipartUploadTask.sendBus(eventId, isStick);

                        }
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
//                        LogUtils.e("dialog", "doOnLifecycle action");
                    }
                }).doOnTerminate(new Action() {
                    //当取消
                    //判断下载或者上传是否完成 未完成即为取消
                    @Override
                    public void run() throws Exception {
                        LogUtils.e("dialog", "doOnTerminate");
                        if (downloadTask != null) {
                            if (downloadTask.isFinish()) {
                                downloadTask.setState(DownloadTask.State.FINISH);
                                downloadTask.sendBus(eventId, isStick);
                            } else {
                                downloadTask.setState(DownloadTask.State.PAUSE);
                                downloadTask.sendBus(eventId, isStick);
                                if (httpObserver instanceof DownloadObserver) {
                                    ((DownloadObserver) httpObserver).onPause(downloadTask);
                                }

                            }
                        } else if (uploadTask != null) {
                            if (uploadTask.isFinish()) {
                                uploadTask.setState(UploadTask.State.FINISH);
                                uploadTask.sendBus(eventId, isStick);
                            } else {
                                uploadTask.setState(UploadTask.State.CANCEL);
                                uploadTask.sendBus(eventId, isStick);
                                if (httpObserver instanceof UploadObserver) {
                                    ((UploadObserver) httpObserver).onCancel();
                                }
                            }
                        } else if (multipartUploadTask != null) {
                            if (multipartUploadTask.isFinish()) {
                                multipartUploadTask.setState(UploadTask.State.FINISH);
                                multipartUploadTask.sendBus(eventId, isStick);
                            } else {
                                multipartUploadTask.setState(UploadTask.State.CANCEL);
                                for (UploadTask uploadTask : multipartUploadTask.getUploadTasks()) {
                                    uploadTask.setState(UploadTask.State.CANCEL);
                                }
                                multipartUploadTask.sendBus(eventId, isStick);
                                if (httpObserver instanceof UploadObserver) {
                                    ((UploadObserver) httpObserver).onCancel();
                                }
                            }
                        } else {

                        }
                    }
                }).doOnError(new Consumer <Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("dialog", "doOnError");
                        //由于手动取消RXJAVA2会进入异常 防止两次判断造成状态错误
                        if (downloadTask != null) {
                            if (downloadTask.getState() != DownloadTask.State.PAUSE) {
                                downloadTask.setState(DownloadTask.State.ERROR);
                                downloadTask.sendBus(eventId, isStick);
                            }
                        } else if (uploadTask != null) {
                            if (uploadTask.getState() != UploadTask.State.CANCEL) {
                                uploadTask.setState(UploadTask.State.ERROR);
                                uploadTask.sendBus(eventId, isStick);
                            }
                        } else if (multipartUploadTask != null) {
                            if (multipartUploadTask.getState() != UploadTask.State.CANCEL) {
                                multipartUploadTask.setState(UploadTask.State.ERROR);
                                for (UploadTask uploadTask : multipartUploadTask.getUploadTasks()) {
                                    uploadTask.setState(UploadTask.State.ERROR);
                                }
                                multipartUploadTask.sendBus(eventId, isStick);
                            }
                        }
                    }
                }).doOnNext(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
//                        LogUtils.e("dialog", "doOnNext");
                    }
                }).doAfterNext(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
//                        LogUtils.e("dialog", "doAfterNext");
                    }
                }).doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
//                        LogUtils.e("dialog", "doFinally");
                    }
                }).doAfterTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
//                        LogUtils.e("dialog", "doAfterTerminate");
                    }
                }).doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
//                        LogUtils.e("dialog", "doOnComplete");

                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /*获取基础URL*/
    private String getBaseUrl() {
        //如果没有重新指定URL则是用默认配置
        return TextUtils.isEmpty(baseUrl) ? getConfig().getBaseUrl() : baseUrl;
    }

    /*ApiUrl处理*/
    private String disposeApiUrl() {
        return TextUtils.isEmpty(apiUrl) ? "" : apiUrl;
    }

    /*处理Header*/
    private void disposeHeader() {

        /*header空处理*/
        if (header == null) {
            header = new TreeMap <>();
        }

        //添加基础 Header
        Map <String, Object> baseHeader = getConfig().getBaseHeader();
        if (baseHeader != null && baseHeader.size() > 0) {
            header.putAll(baseHeader);
        }

        if (!header.isEmpty()) {
            //处理header中文或者换行符出错问题
            for (String key : header.keySet()) {
                header.put(key, NetUtils.getHeaderValueEncoded(header.get(key)));
            }
        }

    }

    /*处理 Parameter*/
    private void disposeParameter() {
        /*空处理*/
        if (parameter == null) {
            parameter = new TreeMap <>();
        }
        //添加基础 Parameter
        Map <String, Object> baseParameter = getConfig().getBaseParameter();
        if (baseParameter != null && baseParameter.size() > 0) {
            parameter.putAll(baseParameter);
        }
    }


    /**
     * Builder
     * 构造Request所需参数，按需设置
     */
    public static final class Builder {
        /*请求方式*/
        Method method;
        /*请求参数*/
        Map <String, Object> parameter;
        /*header*/
        Map <String, Object> header;
        /*json参数*/
        RequestBody requestBody;
        /*LifecycleProvider*/
        LifecycleProvider lifecycle;
        /*ActivityEvent*/
        ActivityEvent activityEvent;
        /*FragmentEvent*/
        FragmentEvent fragmentEvent;
        /*单个上传任务*/
        UploadTask uploadTask;
        /*上传任务列表*/
        MultipartUploadTask multipartUploadTask;
        /*基础URL*/
        String baseUrl;
        /*apiUrl*/
        String apiUrl;
        /*下载任务*/
        DownloadTask downloadTask;
        /*entity*/
        Class <?> clazz;
        /*RxDialog提供Context*/
        Context context;
        /*dialog提示*/
        String message;
        /*dialog是否默认可以取消*/
        boolean cancelable;
        /*自定义progressDialog*/
        ProgressDialog progressDialog;
        /*是否是粘性消息*/
        boolean isStick;
        /*rxBus发射标识 不配置直接获取 配置了需要配置注解eventId*/
        String eventId;

        public Builder() {
        }

        /*GET*/
        public RxHttp.Builder get() {
            this.method = Method.GET;
            return this;
        }

        /*POST*/
        public RxHttp.Builder post() {
            this.method = Method.POST;
            return this;
        }

        /*DELETE*/
        public RxHttp.Builder delete() {
            this.method = Method.DELETE;
            return this;
        }

        /*PUT*/
        public RxHttp.Builder put() {
            this.method = Method.PUT;
            return this;
        }

        /*基础URL*/
        public RxHttp.Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /*API URL*/
        public RxHttp.Builder apiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
            return this;
        }

        /* 增加 Parameter 不断叠加参数 包括基础参数 */
        public RxHttp.Builder addParameter(Map <String, Object> parameter) {
            if (this.parameter == null) {
                this.parameter = new TreeMap <>();
            }
            this.parameter.putAll(parameter);
            return this;
        }

        /*设置 Parameter 会覆盖 Parameter 包括基础参数*/
        public RxHttp.Builder setParameter(Map <String, Object> parameter) {
            this.parameter = parameter;
            return this;
        }

        /* 增加 Header 不断叠加 Header 包括基础 Header */
        public RxHttp.Builder addHeader(Map <String, Object> header) {
            if (this.header == null) {
                this.header = new TreeMap <>();
            }
            this.header.putAll(header);
            return this;
        }

        /*设置 Header 会覆盖 Header 包括基础参数*/
        public RxHttp.Builder setHeader(Map <String, Object> header) {
            this.header = header;
            return this;
        }

        /*设置RequestBody*/
        public RxHttp.Builder setRequestBody(RequestBody requestBody) {
            this.requestBody = requestBody;
            return this;
        }

        /*LifecycleProvider*/
        public RxHttp.Builder lifecycle(LifecycleProvider lifecycle) {
            this.lifecycle = lifecycle;
            return this;
        }

        /*ActivityEvent*/
        public RxHttp.Builder activityEvent(ActivityEvent activityEvent) {
            this.activityEvent = activityEvent;
            return this;
        }

        /*FragmentEvent*/
        public RxHttp.Builder fragmentEvent(FragmentEvent fragmentEvent) {
            this.fragmentEvent = fragmentEvent;
            return this;
        }


        public RxHttp.Builder entity(Class <?> clazz) {
            this.clazz = clazz;
            return this;
        }

        /*单个文件*/
        public RxHttp.Builder uploadTask(UploadTask uploadTask) {
            this.uploadTask = uploadTask;
            return this;
        }

        /*文件集合*/
        public RxHttp.Builder multipartUploadTask(MultipartUploadTask multipartUploadTask) {
            this.multipartUploadTask = multipartUploadTask;
            return this;
        }

        /*下载任务*/
        public RxHttp.Builder downloadTask(DownloadTask downloadTask) {
            this.downloadTask = downloadTask;
            return this;
        }

        /*阻塞对话框*/
        public RxHttp.Builder withDialog(Context context) {
            this.context = context;
            this.message = "";
            this.cancelable = true;
            return this;
        }

        /*阻塞对话框*/
        public RxHttp.Builder withDialog(Context context, String message) {
            this.context = context;
            this.message = message;
            this.cancelable = true;
            return this;
        }

        /*阻塞对话框*/
        public RxHttp.Builder withDialog(Context context, String message, boolean cancelable) {
            this.context = context;
            this.message = message;
            this.cancelable = cancelable;
            return this;
        }

        /*阻塞对话框*/
        public RxHttp.Builder withDialog(ProgressDialog progressDialog) {
            this.progressDialog = progressDialog;
            return this;
        }


        /*是否是粘性消息*/
        public RxHttp.Builder isStick(boolean isStick) {
            this.isStick = isStick;
            return this;
        }

        /*eventId*/
        public RxHttp.Builder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public RxHttp build() {
            return new RxHttp(this);
        }
    }

    public void sendBus(String eventId, boolean isStick, Object object) {
        if (isStick) {
            RxBus.getDefault().removeStickyEventType(object.getClass());
            if (TextUtils.isEmpty(eventId)) {
                RxBus.getDefault().postSticky(object);
            } else {
                RxBus.getDefault().postSticky(eventId, object);
            }
        } else {
            if (TextUtils.isEmpty(eventId)) {
                RxBus.getDefault().post(object);
            } else {
                RxBus.getDefault().post(eventId, object);
            }
        }
    }
}

