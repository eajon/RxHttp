package com.github.eajon;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;


import com.github.eajon.exception.ApiException;
import com.github.eajon.download.DownloadInterceptor;
import com.github.eajon.download.DownloadTask;
import com.github.eajon.observer.HttpObserver;
import com.github.eajon.retrofit.Method;
import com.github.eajon.retrofit.RxConfig;
import com.github.eajon.upload.MultipartUploadTask;
import com.github.eajon.upload.UploadRequestBody;
import com.github.eajon.upload.UploadTask;
import com.github.eajon.util.NetUtils;
import com.github.eajon.util.RetrofitUtils;
import com.github.eajon.util.RxBusUtils;
import com.github.eajon.util.RxUtils;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.BehaviorSubject;
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
    private Type type;
    /*HTTP回调*/
    private HttpObserver httpObserver;
    /*RxDialog提供Context*/
    private Context dialogContext;
    /*dialog提示*/
    private String message;
    /*dialog是否默认可以取消*/
    private boolean cancelable;
    /*自定义对话框*/
    private Dialog dialog;
    /*是否是粘性消息*/
    private boolean isStick;

    /*rxBus发射标识 不配置直接获取 配置了需要配置注解eventId*/
    private String eventId;
    /*cacheKey*/
    private String cacheKey;
    /*重试次数*/
    private int retryTime;


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
        this.type = builder.type;
        this.requestBody = builder.requestBody;
        this.downloadTask = builder.downloadTask;
        this.dialogContext = builder.dialogContext;
        this.message = builder.message;
        this.cancelable = builder.cancelable;
        this.dialog = builder.dialog;
        this.isStick = builder.isStick;
        this.eventId = builder.eventId;
        this.cacheKey = builder.cacheKey;
        this.retryTime = builder.retryTime;
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

    /*普通HttpRxBus*/
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

    /*上传文件RXBUS*/
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

    /*下载文件RXBUS*/
    public void download() {
        if (downloadTask == null) {
            throw new NullPointerException("DownloadTask must not null!");
        } else {
            doDownload();
        }
    }


    /*执行请求*/
    private void doRequest() {

        Observable observable;
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
        subscribe(observable);
    }

    /*执行文件上传*/
    private void doUpload() {
        Observable observable;
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
        subscribe(observable);
    }

    private void doDownload() {


        Observable observable = RetrofitUtils.get().getRetrofit(NetUtils.getBaseUrl(downloadTask.getServerUrl()), new DownloadInterceptor(eventId, isStick, downloadTask)).download(downloadTask.getServerUrl(), "bytes=" + downloadTask.getCurrentSize() + "-");
        /*请求处理*/
        subscribe(observable);


    }

    private void subscribe(Observable observable) {
        if (httpObserver != null) {
            //自定义dilaog或者有dialog的context
            if (dialog != null || dialogContext != null) {
                dialogObserver(observable).subscribe(httpObserver);
            } else {
                observe(observable).subscribe(httpObserver);
            }
        } else {
            HttpObserver observer = new HttpObserver() {
                @Override
                public void onSuccess(Object o) {
                    RxBusUtils.sendBus(eventId, isStick, o);
                }

                @Override
                public void onError(ApiException t) {
                    RxBusUtils.sendBus(eventId, isStick, t);
                }
            };
            if (dialog != null || dialogContext != null) {
                dialogObserver(observable).subscribe(observer);
            } else {
                observe(observable).subscribe(observer);
            }
        }
    }


    //dialog
    private Observable dialogObserver(final Observable observable) {
        return Observable.using(new Callable <Dialog>() {
            @Override
            public Dialog call() {
                if (dialog != null) {
                    dialog.show();
                    return dialog;
                }
                return ProgressDialog.show(dialogContext, null, message, true, cancelable);
            }
        }, new Function <Dialog, Observable <? extends Object>>() {
            @Override
            public Observable <? extends Object> apply(final Dialog progressDialog) throws Exception {
                final BehaviorSubject <Boolean> dialogSubject = BehaviorSubject.create();
                return observe(observable).doOnSubscribe(new Consumer <Disposable>() {
                    @Override
                    public void accept(final Disposable disposable) throws Exception {
                        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
//                                LogUtils.e("dialog", "doOnSubscribe");
                                dialogSubject.onNext(true);
                            }
                        });
                    }
                }).takeUntil(dialogSubject);
            }
        }, new Consumer <Dialog>() {
            @Override
            public void accept(Dialog dialog) throws Exception {
//                LogUtils.e("dialog", "dismiss");
                dialog.dismiss();
            }
        });
    }


    //构建数据发射器
    public Observable observe(Observable observable) {
        return observable
                .compose(RxUtils.map(downloadTask, type))
                .compose(RxUtils.cache(cacheKey, type, isRequest()))
                .compose(RxUtils.lifeCycle(lifecycle, activityEvent, fragmentEvent))
                .compose(RxUtils.retryPolicy(retryTime))
                .compose(RxUtils.sendEvent(downloadTask, uploadTask, multipartUploadTask, eventId, isStick))
                .compose(RxUtils.io_main());

    }

    private boolean isRequest() {
        if (uploadTask != null || multipartUploadTask != null || downloadTask != null) {
            return false;
        }
        return true;
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
        Type type;
        /*RxDialog提供Context*/
        Context dialogContext;
        /*dialog提示*/
        String message;
        /*dialog是否默认可以取消*/
        boolean cancelable;
        /*自定义progressDialog*/
        Dialog dialog;
        /*是否是粘性消息*/
        boolean isStick;
        /*rxBus发射标识 不配置直接获取 配置了需要配置注解eventId*/
        String eventId;

        String cacheKey;

        int retryTime;

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


        public RxHttp.Builder entity(Type type) {
            this.type = type;
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
            this.dialogContext = context;
            this.message = "";
            this.cancelable = true;
            return this;
        }

        /*阻塞对话框*/
        public RxHttp.Builder withDialog(Context context, String message) {
            this.dialogContext = context;
            this.message = message;
            this.cancelable = true;
            return this;
        }

        /*阻塞对话框*/
        public RxHttp.Builder withDialog(Context context, String message, boolean cancelable) {
            this.dialogContext = context;
            this.message = message;
            this.cancelable = cancelable;
            return this;
        }

        /*阻塞对话框*/
        public RxHttp.Builder withDialog(Dialog dialog) {
            this.dialog = dialog;
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

        /*eventId*/
        public RxHttp.Builder cacheKey(String cacheKey) {
            this.cacheKey = cacheKey;
            return this;
        }

        /*eventId*/
        public RxHttp.Builder retryTime(int retryTime) {
            this.retryTime = retryTime;
            return this;
        }

        public RxHttp build() {
            return new RxHttp(this);
        }
    }


}

