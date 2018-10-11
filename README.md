
![image](https://github.com/eajon/RxHttp/blob/master/app/src/main/res/drawable/demo.gif)  

# RxHttp
         本框架 是对 RXJAVA2 + Retrofit + RxBus2 + OkHttp3 的架构的封装 
         达成目标：链式调用，简单明了
         1.支持 主流的Http 请求 
         2.支持 多文件上传 监听进度
         3.支持 大文件下载 监听进度
         4.所有请求支持RXBUS获取返回数据


# Future 
          1.增加发送粘性消息
          2.增加rxchche
          3.增加自动重试
	  


# 导入


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
	        implementation 'com.github.eajon:RxHttp:0.4.0'
	}

	
		
 # 最新更新
                  1.请求的时候不传observer默认使用rxbus发射，返回结果可以@RxSubscribe 标签获取
                  2.增加阻塞对话框,调用withDialog 就可以增加阻塞dialog,如果使用lifecycle对话框取消也会跟随生命周期，同时对话框消失任务会暂停或者取消
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
        
 # 如何使用他    

	
## 在Application中设置基础配置（可选）

               RxConfig.init(this)/*必填*/
                .baseUrl("http://192.168.0.1/api/")/*基础路径，这里配置了请求中可以不单独配置*/
                .baseHeader(null)/*公用请求头*/
                .baseParameter(null)/*公用请参数*/
                .okHttpClient(null)/*自定义okHttpClient*/
                .logTag("RxHttp");/*自定义Log名称*/
                
                
                
## HTTP 请求
     
               
####  Get Post Delete Put
           
           //返回对象
         new RxHttp
                .Builder()
                .get()/*post() put() delete() 按需配置默认POST*/
                .baseUrl("http://192.168.0.1/api/")/* 按需配置 RxConfig已配置，可不配*/
                .apiUrl("login")/* 按需配置 具体接口名称*/
                .entity(Login.class)/* 按需配置 返回的数据类型，默认string*/
                .addHeader(null)/* 按需配置 */
                .addParameter(null)/* 按需配置 */
                .lifecycle(this)/* 关联生命周期，可以指定到Activity具体动作，使用生命周期当前Activity需要继承RxAppCompatActivity 或者RxBusActivity */
                .build()
                .request(new HttpObserver<Login>() {
             @Override
             public void onSuccess(Response o) {
            
             }
            
             @Override
             public void onError(ApiException t) {
            
             }
                        
             @Override
             public void onCancelOrPause() {
                        
             }
        });
        
          //返回string数据
          new RxHttp
                .Builder()
                .get()/*post() put() delete() 按需配置默认POST*/
                .baseUrl("http://192.168.0.1/api/")/* 按需配置 RxConfig已配置，可不配*/
                .apiUrl("login")/* 按需配置 具体接口名称*/
                .addHeader(null)/* 按需配置 */
                .addParameter(null)/* 按需配置 */
                .lifecycle(this)/* 关联生命周期，可以指定到Activity具体动作，使用生命周期当前Activity需要继承RxAppCompatActivity 或者RxBusActivity */
                .build()
                .request(new HttpObserver() {
            @Override
            public void onSuccess(Response o) {
           
            }
           
            @Override
            public void onError(ApiException t) {
           
            }
                       
             @Override
            public void onCancelOrPause() {
                                    
            }
        });
        
        
 #### RxBus 方式
        HashMap map = new HashMap();
               map.put("city", "常熟");
               new RxHttp.Builder()
                       .get()
                       .baseUrl("http://wthrcdn.etouch.cn/")
                       .apiUrl("weather_mini")
                       .addParameter(map)
                       .entity(Weather.class)
                       .build()
                       .request();
                       
        @RxSubscribe(observeOnThread = EventThread.MAIN)
        public void weatherCallBack(Weather weather) {//entity 设置为哪个对象，RxbSubscribe那个对象即可
                  content.setText(new Gson().toJson(weather));
        }
                       
         @RxSubscribe(observeOnThread = EventThread.MAIN)//异常捕获
         public void weatherCallBack(ApiException e) {
              content.setText(new Gson().toJson(e));
         }
                
                
 ####  Post   对象  例如JSON
 
 
        String requestBody = new Gson().toJson(new Login("username","password"));
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestBody);//对象转化为RequestBody
            new RxHttp
                .Builder()
                .baseUrl("http://192.168.0.1/api/")
                .apiUrl("login")
                .entity(Response.class)
                .addHeader(null)
                .lifecycle(this)/* 关联生命周期，可以指定到Activity具体动作，使用生命周期当前Activity需要继承RxAppCompatActivity 或者RxBusActivity */
                .setRequestBody(body)/*必填参数 */
                .build()
                .request(new HttpObserver<Response>() {
            @Override
            public void onSuccess(Response o) {

            }

            @Override
            public void onError(ApiException t) {

            }
            
             @Override
            public void onCancelOrPause() {
                                    
            }
            
            
        });
        
        
        /* 不需要关心cancel，可以使用BaseObserver   */
        
## 下载（设置entity无效,成功返回数据必须是DownloadTask）

####  普通下载  支持RXBUS方式


    File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "WEIXIN" + ".apk")
    String url1 = "http://imtt.dd.qq.com/16891/50CC095EFBE6059601C6FB652547D737.apk?fsname=com.tencent.mm_6.6.7_1321.apk&csr=1bbd";
    DownloadTask downloadTask = new DownloadTask(file1.getName(), file1.getAbsolutePath(), url1);
    DownloadTask downloadTask;
             new RxHttp
                .Builder()
                .lifecycle(this)/*下载按需配置lifecycle 一般不配置*/
                .downloadTask(downloadTask)
                .build()
                .download(new HttpObserver() {
            @Override
            public void onSuccess(DownloadTask downloadTask) {

            }

            @Override
            public void onError(ApiException s) {

            }
            
             @Override
            public void onCancelOrPause() {
                                    
            }
        });
        
#### 下载 暂停 继续 支持断点续传 并可以监听进度（监听进度在最下面）
           File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "WEIXIN" + ".apk")
           String url1 = "http://imtt.dd.qq.com/16891/50CC095EFBE6059601C6FB652547D737.apk?fsname=com.tencent.mm_6.6.7_1321.apk&csr=1bbd";
           DownloadTask downloadTask = new DownloadTask(file1.getName(), file1.getAbsolutePath(), url1);
           RxHttp rxHttp = new RxHttp.Builder()
           .lifecycle(this)/*下载按需配置lifecycle 一般不配置*/
           .downloadTask(downloadTask)
           .build();
                                        
            HttpObserver downloadObserver = new HttpObserver() {
                @Override
                public void onSuccess(DownloadTask downloadTask) {
                       LogUtils.e(RxHttp.getConfig().getLogTag(), downloadTask.getState());
                }

                @Override
                public void onError(String t) {

                }
                         
                 @Override
                 public void onCancelOrPause() {
                                        
                 }

            };
                
            rxHttp.download(downloadObserver);
  
                
                
                //暂停OnClick
                downloadObserver.dispose();//取消发射，暂停下载 注：调用dispose 下载由于支持断点续传为暂停,上传和请求均为进入取消请求
                downloadTask.getState();//此时获取状态是暂停
                downloadTask.getProgress();//获取进度
                
              
                 
                   
 ## 上传
 
 ####  单文件 支持RXBUS方式
              UploadTask uploadTask = new UploadTask("SingleTag", new File(path));
              new RxHttp.Builder()
                .baseUrl("https://shop.cxwos.com/admin/File/")
                .apiUrl("UploadFile?tentantId=16")
                .uploadTask(uploadTask)/* 单文件必填*/
                .lifecycle(this)/*上传按需配置lifecycle*/
                .build()
                .upload(new HttpObserver() {
                    @Override
                    public void onSuccess(Object o) {

                    }

                    @Override
                    public void onError(ApiException t) {

                    }
                    
                     @Override
                     public void onCancelOrPause() {
                                            
                     }
                });
                
 ####  多文件        支持RXBUS方式
 
              ArrayList <UploadTask> uploadTasks = new ArrayList <>();
              UploadTask uploadTask = new UploadTask("SingleTag", new File(path));
              UploadTask uploadTask2 = new UploadTask("Single2Tag", new File(path2));
              uploadTasks.add(uploadTask);
              uploadTasks.add(uploadTask2);
               MultipartUploadTask multipartUploadTask =new MultipartUploadTask("muiltTag",uploadTasks);
              new RxHttp.Builder()
                .baseUrl("https://shop.cxwos.com/admin/File/")
                .apiUrl("UploadFile?tentantId=16")
                .multipartUploadTask(multipartUploadTask)
                .lifecycle(this)/*上传按需配置lifecycle*/
                .build()
                .upload(new UploadObserver() {
                    @Override
                    public void onSuccess(Object o) {

                    }

                    @Override
                    public void onError(ApiException t) {

                    }
                    
                    @Override
                    public void onCancelOrPause() {
                                           
                    }
                });
                  
                  
 ####  关于进度  上传进度 和下载一样 通过继承RxBusActivity实现  同时开启多个任务的时候 可以通过任务中的tag区分                
                     @RxSubscribe(observeOnThread = EventThread.MAIN) //监听下载进度 
                     public void downloadProgress(DownloadTask downloadTask)
                     {
                         download.setText(downloadTask.getState().toString() + downloadTask.getProgress() + "%");
                     }
                 
                     @RxSubscribe(observeOnThread = EventThread.MAIN) //单文件上传
                     public void uploadProgress(UploadTask uploadTask)
                     {
                         upload.setText(uploadTask.getState().toString() + uploadTask.getProgress() + "%");
                     }
                 
                     @RxSubscribe(observeOnThread = EventThread.MAIN)//多文件上传
                     public void uploadProgress(MultipartUploadTask multipartUploadTask)
                     {
                         content.setText("总进度：" + multipartUploadTask.getProgress() + "%" + multipartUploadTask.getState().toString());
                         if (multipartUploadTask.getUploadTasks().size() == 3) {//上传3个文件
                             content1.setText("第一个：" + multipartUploadTask.getProgress(0) + "%" + multipartUploadTask.getState(0).toString());
                             content2.setText("第二个：" + multipartUploadTask.getProgress(1) + "%" + multipartUploadTask.getState(1).toString());
                             content3.setText("第三个：" + multipartUploadTask.getProgress(2) + "%" + multipartUploadTask.getState(2).toString());
                         }
                     }


#### 具体用例可参看DEMO,发现BUG，可联系eajon@outlook.com,感谢关注

