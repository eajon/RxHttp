# RxHttp
         本框架 是对 RXJAVA2 + Retrofit + RxBus + OkHttp 的架构的封装 
         达成目标：链式调用，简单明了
         1.支持 主流的Http 请求 
         2.支持 多文件上传 监听进度
         3.支持 大文件下载 监听进度


# Future 
          1.Http 请求支持RxBus 发射
          2.请求增加阻塞Dialog


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
	        implementation 'com.github.eajon:RxHttp:0.2.0'
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
                .lifecycle(this)/* 关联生命周期，可以指定到Activity具体动作，使用生命周期当前Activity需要继承RxAppCompatActivity 或者BaseMvpActivity */
                .build()
                .request(new HttpObserver<Login>() {
             @Override
             public void onSuccess(Response o) {
            
             }
            
             @Override
             public void onError(String t) {
            
             }
                        
             @Override
             public void onCancel(String t) {
                        
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
                .lifecycle(this)/* 关联生命周期，可以指定到Activity具体动作，使用生命周期当前Activity需要继承RxAppCompatActivity 或者BaseMvpActivity */
                .build()
                .request(new HttpObserver() {
            @Override
            public void onSuccess(Response o) {
           
            }
           
            @Override
            public void onError(String t) {
           
            }
                       
            @Override
            public void onCancel(String t) {
                       
            }
        });
                
                
 ####  Post   JSON对象  
 
 
        String requestBody = new Gson().toJson(new Login("username","password"));
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestBody);//对象转化为RequestBody
            new RxHttp
                .Builder()
                .baseUrl("http://192.168.0.1/api/")
                .apiUrl("login")
                .entity(Response.class)
                .addHeader(null)
                .lifecycle(this)/* 关联生命周期，可以指定到Activity具体动作，使用生命周期当前Activity需要继承RxAppCompatActivity 或者BaseMvpActivity */
                .setRequestBody(body)/*必填参数 */
                .build()
                .request(new HttpObserver<Response>() {
            @Override
            public void onSuccess(Response o) {

            }

            @Override
            public void onError(String t) {

            }
            
             @Override
            public void onCancel(String t) {
            
            }
            
            
        });
        
        
        /* 不需要关心cancel，可以使用BaseObserver   */
        
## 下载

####  普通下载 


    File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "WEIXIN" + ".apk")
    String url1 = "http://imtt.dd.qq.com/16891/50CC095EFBE6059601C6FB652547D737.apk?fsname=com.tencent.mm_6.6.7_1321.apk&csr=1bbd";
    DownloadTask downloadTask = new DownloadTask(file1.getName(), file1.getAbsolutePath(), url1);
    DownloadTask downloadTask;
             new RxHttp
                .Builder()
                .lifecycle(this)/*下载按需配置lifecycle*/
                .downloadTask(downloadTask)
                .build()
                .download(new DownloadObserver() {
            @Override
            public void onSuccess(DownloadTask downloadTask) {

            }

            @Override
            public void onError(String s) {

            }
            
             @Override
            public void onCancel(String t) {
                                
            }
        });
        
#### 下载 暂停 继续 支持断点续传 并监听进度
           File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "WEIXIN" + ".apk")
           String url1 = "http://imtt.dd.qq.com/16891/50CC095EFBE6059601C6FB652547D737.apk?fsname=com.tencent.mm_6.6.7_1321.apk&csr=1bbd";
           DownloadTask downloadTask = new DownloadTask(file1.getName(), file1.getAbsolutePath(), url1);
           RxHttp rxHttp = new RxHttp.Builder()
           .lifecycle(this)/*下载按需配置lifecycle*/
           .downloadTask(downloadTask)
           .build();
                                        
            DownloadObserver downloadObserver = new DownloadObserver() {
                @Override
                public void onSuccess(DownloadTask downloadTask) {
                       LogUtils.e(RxHttp.getConfig().getLogTag(), downloadTask.getState());
                }

                @Override
                public void onError(String t) {

                }
                         
                @Override
                public void onPause(String t) {
                         
                }

            };
                
            rxHttp.download(downloadObserver);
  
                
                
                //暂停OnClick
                downloadObserver.dispose();//取消发射，暂停下载 注：调用dispose 下载由于支持断点续传为暂停onPause(),上传和请求均为取消onCancel()
                downloadTask.getState();//此时获取状态是暂停
                downloadTask.getProgress();//获取进度
                
                //实时获取进度  通过RXbus实现 Activity需要继承BaseMvpActivity 
                  @Override
                  public void onResponse(RxResponse response) {
                        if (response.getTag().equals(downloadTask.getTag())) {//判断tag Tag相同表示是当前任务 注：在多线程下载的过程中，确保Tag唯一
                          DownloadTask downloadTask = (DownloadTask) response.getData();//获取当前下载任务
                          downloadTask.getState();//下载状态
                          downloadTask.getProgress();//下载进度
                       }
                   }
                   
                   
 ## 上传
 
 ####  单文件 
              UploadTask uploadTask = new UploadTask("SingleTag", new File(path));
              new RxHttp.Builder()
                .baseUrl("https://shop.cxwos.com/admin/File/")
                .apiUrl("UploadFile?tentantId=16")
                .uploadTask(uploadTask)/* 单文件必填*/
                .lifecycle(this)/*上传按需配置lifecycle*/
                .build()
                .upload(new UploadObserver() {
                    @Override
                    public void onSuccess(Object o) {

                    }

                    @Override
                    public void onError(String t) {

                    }
                    
                    @Override
                    public void onCancel(String t) {
                    
                    }
                });
                
 ####  多文件        
 
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
                    public void onError(String t) {

                    }
                    
                    @Override
                    public void onCancel(String t) {
                                        
                    }
                });
                  
                  
 ####  上传进度 和下载一样                   
                  //实时获取进度  通过RXbus实现 Activity需要继承BaseMvpActivity 
                  @Override
                  public void onResponse(RxResponse response) {
                        if (response.getTag().equals(uploadTask.getTag())) {//单文件 
                           UploadTask uploadTask = (UploadTask)response.getData();
                           uploadTask.getState(); //文件状态
                           uploadTask.getProgress();//文件进度
                       }
                       
                       if (response.getTag().equals(multipartUploadTask.getTag()) {//多文件
                          MultipartUploadTask multipartUploadTask = (MultipartUploadTask) response.getData();
                          multipartUploadTask.getProgress();总进度
                          multipartUploadTask.getState();//总状态
                          //其中某个文件进度 
                          multipartUploadTask.getProgress(0);
                          //其中某个文件状态
                          multipartUploadTask.getState(0);
                          
                       }
                   }
