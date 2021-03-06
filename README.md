
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/fcd683b1e966409f920c61901c9e7c0d)](https://www.codacy.com/manual/eajon/RxHttp?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=eajon/RxHttp&amp;utm_campaign=Badge_Grade)

# RxHttp 


         本框架 是对 RXJAVA2 + Retrofit + OkHttp3 + lifecycle的架构的封装
         1.采用链式调用一点到底
         2.支持动态配置和自定义Okhttpclient，支持okhttp自定义cookie管理
         3.支持多种方式访问网络GET、POST、PUT、DELETE等请求协议
         4.支持网络缓存，以及本地缓存,七种缓存策略可选
         5.支持固定添加header和动态添加header
	     6.支持添加全局参数和动态添加局部参数
	     7.支持文件下载,断点续传,监听进度
	     8.支持多文件上传,监听进度
	     9.支持任意数据结构的自动解析,支持gson,fastjson,jackson
	     10.支持显示progressDialog，progreesBar,并且生命周期和请求自动关联,无需手动控制
	     11.支持LifeCycle,所有请求可以配置生命周期与当前页面生命周期绑定
	     等等
         具体功能使用方法参看demo
	 

## 新增功能
支持无需设置返回对象Type 只需用泛型Observer指定对象类型 即可自动解析 例如：

 此处设置为login 返回对象会自动解析成Login 解析失败则会报错，不填类型的时候，默认解析成String

                    .request(new HttpObserver<Login>() {

                        @Override
                        public void onSuccess(Login o) {

                        }

                        @Override
                        public void onError(ApiException t) {

                        }
                    });

 支持对象参数 会自动解析对象中的属性 转成keyvalue

 addTypeParamters(Object object)

 addTypeHeader(Object object)

 2.2.1版本开始支持 gson,jackson,fastjson

 GSON  可以使用@GsonField 自定义注解   过滤掉不需要的参数 或者重定义jsonField值

 JACKSON  可以使用@JsonIgnore 等原生注解 过滤掉不需要的参数 或者重定义jsonField值

 FASTJSON  可以使用@JSONField  等原生注解 过滤掉不需要的参数 或者重定义jsonField值








## 导入


[![](https://jitpack.io/v/eajon/RxHttp.svg)](https://jitpack.io/#eajon/RxHttp)


#### Step 1. Add the JitPack repository to your build file

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}



#### Step 2. Add the dependency

 	dependencies {
	        implementation 'com.github.eajon:RxHttp:2.2.3'
	        选择你需要的JSON解析器 并设置converterType 为该类型 converterType不配置 默认为GSON 需添加GSON依赖
	        implementation 'com.google.code.gson:gson:2.8.5'
            implementation 'com.alibaba:fastjson:1.1.52.android'
            implementation 'com.fasterxml.jackson.core:jackson-databind:2.9.8'
	}



## 如何使用他


## 在Application中设置基础配置

          RxHttp.getConfig()
                .baseUrl(...)/*基础路径，这里配置了请求中可以不单独配置*/
                .converterType(...)/*设置json解析器，默认GSON*/
                .baseHeader(...)/*公用请求头*/
                .baseParameter(...)/*公用请参数*/
                .okHttpClient(...)/*自定义okHttpClient*/
                .log(...)/*自定义Log名称*/
                .rxCache(...);/* 配置cache, 不配置默认使用okhttp的缓存*/




## 常规请求使用




             new RxHttp
                .Builder()
                .get()/*post() put() delete() 按需配置默认POST*/
                .requestBody(...)/*body对象*/
		.json(...)/*同body对象,contentType 自动设置成JSON*/
                .task(...)/*上传或者下载任务*/
                .baseUrl(...)/* 按需配置 RxConfig已配置，可不配*/
                .addHeader(...)/* 按需配置 */
                .addParameter(...)/* 按需配置 */
                .lifecycle(...)/* 关联生命周期,当前Activity需要继承RxAppCompatActivity */
                .withDialog(...)/*是否加入阻塞对话框，可以自定义diolog*/
                .tag(...)/*发射的id*/
                .cacheKey(...)/*缓存Key*/
                .retryTime(...)/*重试次数*/
                .build()
                .request(new HttpObserver<Login>() {

                        @Override
                        public void onSuccess(Login o) {

                        }

                        @Override
                        public void onError(ApiException t) {

                        }


                    });






## 下载例子




    File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "WEIXIN" + ".apk")
    DownloadTask downloadTask = new DownloadTask(file1.getName(), file1.getAbsolutePath());

    Disposable disposeable=  new RxHttp
                .Builder()
		.get("16891/50CC095EFBE6059601C6FB652547D737.apk?fsname=com.tencent.mm_6.6.7_1321.apk&csr=1bbd")
                .baseUrl("http://imtt.dd.qq.com/")
                .tag("download")
                .lifecycle(this)
                .task(downloadTask)
                .withDailog(this)
                .build()
                .request(new DownlaodObserver() {
                    @Override
                    public void onSuccess(DownloadTask downloadTask) {

                    }

                    @Override
                    public void onError(ApiException s) {

                    }

                    @Override
                    public void onPause(DownloadTask downloadTask) {

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
                });







 ## 上传例子

 ####  OCT-STREAM 上传 只支持单文件
             UploadTask uploadTask = new UploadTask( new File(path));
             Disposable disposeable = new RxHttp.Builder()
	                .post("UploadFile?tentantId=16")
                        .baseUrl("https://shop.cxwos.com/admin/File/")
                        .task(uploadTask)/* OCT-STREAM方式上传类型为UploadTask*/
                        .lifecycle(this)
                        .build()
                        .request(new HttpObserver() {
                               @Override
                               public void onSuccess(Object o) {

                               }

                               @Override
                               public void onError(ApiException t) {

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

                        });

 ####  Multipart 表单提交 支持多文件

              ArrayList <UploadTask> uploadTasks = new ArrayList <>();
              UploadTask uploadTask = new UploadTask( new File(path));
              UploadTask uploadTask2 = new UploadTask(new File(path2));
              uploadTasks.add(uploadTask);
              uploadTasks.add(uploadTask2);
              MultiUploadTask multiUploadTask =new MultiUploadTask("muiltTag",uploadTasks);
              Disposable disposeable = new RxHttp.Builder()
                        .baseUrl("https://shop.cxwos.com/admin/File/")
                        .api("UploadFile?tentantId=16")
                        .task(multiUploadTask)/* Multipart/form-data方式上传类型为MultiUploadTask*/
                        .lifecycle(this)/*上传按需配置lifecycle*/
                        .build()
                        .request(new UploadObserver() {
                               @Override
                               public void onSuccess(Object o) {

                               }

                              @Override
                              public void onError(ApiException t) {

                              }

                               @Override
                               public void onProgress(BaseTask task) {
                                    runOnUiThread(new Runnable() {
                                            @Override
                                    public void run() {
                                        content.setText("总进度：" + task.getProgress() + "%" + task.getState().toString());
                                                                 if (task.getUploadTasks().size() == 2) {//假设上传2个文件
                                                                     content1.setText("第一个：" + task.getProgress(0) + "%" + task.getState(0).toString());
                                                                     content2.setText("第二个：" + task.getProgress(1) + "%" + task.getState(1).toString());

                                                                 }
                                              }
                                            });
                                         }

                       });



  #### 如何暂停和取消

            //取消发射，下载即为暂停（下载重启任务可以断点续传），其他任务是取消
            disposeable.dispose();


 ####  如何监听进度


#### 具体用例可参看DEMO,发现BUG，可联系eajon@outlook.com,感谢关注

