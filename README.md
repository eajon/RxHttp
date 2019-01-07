![image](https://github.com/eajon/RxHttp/blob/master/app/src/main/res/drawable/demo.gif)  

# RxHttp
         本框架 是对 RXJAVA2 + Retrofit + RxBus2 + OkHttp3 + lifecycle的架构的封装
         1.采用链式调用一点到底
         2.支持动态配置和自定义Okhttpclient
         3.支持多种方式访问网络GET、POST、PUT、DELETE等请求协议
         4.支持网络缓存,七种缓存策略可选
         5.支持固定添加header和动态添加header
	     6.支持添加全局参数和动态添加局部参数
	     7.支持文件下载,断点续传,通过rxbus可以监听进度
	     8.支持多文件上传,通过rxbus可以监听进度
	     9.支持任意数据结构的自动解析,采用gson
	     10.支持请求数据结果采用订阅和RXBUS注解方式
	     11.支持显示progressDialog,并且生命周期和请求自动关联,无需手动控制
	     12.支持LifeCycle,所有请求可以配置生命周期与当前页面生命周期绑定
	     13.支持RXBUS 使用注解获取数据 并且支持粘性消息 可以替代Intent
         14.支持自动重试
         具体功能使用方法参看demo
         
	   
	     
	     

	  


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
	        implementation 'com.github.eajon:RxHttp:v1.4.1'
	}

	
		
# 如何使用他    

	
## 在Application中设置基础配置

        RxConfig.get()
                .baseUrl(...)/*基础路径，这里配置了请求中可以不单独配置*/
                .baseHeader(...)/*公用请求头*/
                .baseParameter(...)/*公用请参数*/
                .okHttpClient(...)/*自定义okHttpClient*/
                .logTag(...)/*自定义Log名称*/
                .rxCache(...);/* 配置cache, 不配置默认使用okhttp的缓存*/
                
                
                
## 常规请求使用
     
              
           
          
             new RxHttp
                .Builder()
                .get()/*post() put() delete() 按需配置默认POST*/
                .requestBody(...)/*retorfit发射对象*/
                .task(...)/*上传或者下载任务*/
                .baseUrl(...)/* 按需配置 RxConfig已配置，可不配*/
                .apiUrl(...)/* 按需配置 具体接口名称*/
                .entity(...)/* 按需配置 设置返回的数据类型，默认string*/
                .addHeader(...)/* 按需配置 */
                .addParameter(...)/* 按需配置 */
                .lifecycle(...)/* 关联生命周期,当前Activity需要继承RxAppCompatActivity或者RxBusActivity */
                .activityEvent(...)/*具体指定生命周期的动作*
                .isStick(...)/*是否是粘性消息，默认每种类型只保存最后一个*/
                .withDialog(...)/*是否加入阻塞对话框，可以自定义diolog*/
                .eventId(...)/*rxbus发射的id*/
                .cacheKey(...)/*缓存Key*/
                .retryTime(...)/*重试次数*/
                .build()
                .request(new HttpObserver<Login>() {
                
                        @Override
                        public void onSuccess(Response o) {
            
                        }
            
                        @Override
                        public void onError(ApiException t) {
            
                        }
                        
           
                    });
        
         
        
        
 ## RxBus 注解方式
             new RxHttp
                .Builder()
                .get()/*post() put() delete() 按需配置默认POST*/
                .requestBody(...)/*retorfit发射对象*/
                .task(...)/*上传或者下载任务downalodTask uploadtask  mulitpartuploadtask三种任务*/
                .baseUrl(...)/* 按需配置 RxConfig已配置，可不配*/
                .apiUrl(...)/* 按需配置 具体接口名称*/
                .entity(...)/* 按需配置 设置返回的数据类型，默认string*/
                .addHeader(...)/* 按需配置 */
                .addParameter(...)/* 按需配置 */
                .lifecycle(...)/* 关联生命周期,当前Activity需要继承RxAppCompatActivity或者RxBusActivity */
                .activityEvent(...)/*具体指定生命周期的动作*/
                .isStick(...)/*是否是粘性消息，默认每种类型只保存最后一个*/
                .withDialog(...)/*是否加入阻塞对话框，可以自定义diolog*/
                .eventId(...)/*rxbus发射的id*/
                .cacheKey(...)/*缓存Key*/
                .retryTime(...)/*重试次数*/
                .build()
                .request();
                       
                       
                //eventid 和 entity设置对应即可，方法名随意
                @RxSubscribe(observeOnThread = EventThread.MAIN，eventId ="test")
                public void weatherCallBack(Weather weather) {
                           content.setText(new Gson().toJson(weather));
                    }
                       
                @RxSubscribe(observeOnThread = EventThread.MAIN)//异常捕获
                public void weatherCallBack(ApiException e) {
                           content.setText(new Gson().toJson(e));
                }
	 
	    
        
## 下载例子（设置entity无效,成功返回数据必须是DownloadTask）




    File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "WEIXIN" + ".apk")
    DownloadTask downloadTask = new DownloadTask(file1.getName(), file1.getAbsolutePath());
  
    Disposable disposeable=  new RxHttp
                .Builder()
                .baseUrl("http://imtt.dd.qq.com/")
                .apiUrl("16891/50CC095EFBE6059601C6FB652547D737.apk?fsname=com.tencent.mm_6.6.7_1321.apk&csr=1bbd")
                .eventId("download")
                .lifecycle(this)
                .task(downloadTask)
                .withDailog(this)
                .build()
                .download(new DownlaodObserver() {
                    @Override
                    public void onSuccess(DownloadTask downloadTask) {
     
                    }

                    @Override
                    public void onError(ApiException s) {

                    }
                   
                    @Override
                    public void onPause(DownloadTask downloadTask) {
 
                    }
            
         
                });
        

             
                
              
            
                   
 ## 上传例子
 
 ####  单文件 
             UploadTask uploadTask = new UploadTask( new File(path));
             Disposable disposeable = new RxHttp.Builder()
                        .baseUrl("https://shop.cxwos.com/admin/File/")
                        .apiUrl("UploadFile?tentantId=16")
                        .task(uploadTask)/* 单文件必填*/
                        .lifecycle(this)
                        .build()
                        .upload(new HttpObserver() {
                               @Override
                               public void onSuccess(Object o) {

                               }

                               @Override
                               public void onError(ApiException t) {

                               }
                    
                        });
                
 ####  多文件      
 
              ArrayList <UploadTask> uploadTasks = new ArrayList <>();
              UploadTask uploadTask = new UploadTask( new File(path));
              UploadTask uploadTask2 = new UploadTask(new File(path2));
              uploadTasks.add(uploadTask);
              uploadTasks.add(uploadTask2);
              MultipartUploadTask multiUploadTask =new MultipartUploadTask("muiltTag",uploadTasks);
              Disposable disposeable = new RxHttp.Builder()
                        .baseUrl("https://shop.cxwos.com/admin/File/")
                        .apiUrl("UploadFile?tentantId=16")
                        .task(multiUploadTask)
                        .lifecycle(this)/*上传按需配置lifecycle*/
                        .build()
                        .upload(new UploadObserver() {
                               @Override
                               public void onSuccess(Object o) {

                               }

                              @Override
                              public void onError(ApiException t) {

                              }
     
                       });
                       
                      
           
  #### 如何暂停和取消
                
            //取消发射，下载即为暂停（下载重启任务可以断点续传），其他任务是取消
            disposeable.dispose();
                  
                  
 ####  如何监听进度  
 #####上传 和下载一样 用rxbus注解实现,当然你还可以查看实时传输速度哦 task.getSpeed() 
 
                     @RxSubscribe(observeOnThread = EventThread.MAIN) //监听下载进度 
                     public void downloadProgress(DownloadTask task)
                     {
                         download.setText(task.getState().toString() + task.getProgress() + "%");
                     }
                 
                     @RxSubscribe(observeOnThread = EventThread.MAIN) //单文件上传
                     public void uploadProgress(UploadTask task)
                     {
                         upload.setText(task.getState().toString() + task.getProgress() + "%");
                     }
                 
                     @RxSubscribe(observeOnThread = EventThread.MAIN)//多文件上传
                     public void uploadProgress(MultipartUploadTask task)
                     {
                         content.setText("总进度：" + task.getProgress() + "%" + task.getState().toString());
                         if (task.getUploadTasks().size() == 3) {//假设上传3个文件
                             content1.setText("第一个：" + task.getProgress(0) + "%" + task.getState(0).toString());
                             content2.setText("第二个：" + task.getProgress(1) + "%" + task.getState(1).toString());
                             content3.setText("第三个：" + task.getProgress(2) + "%" + task.getState(2).toString());
                         }
                     }


#### 具体用例可参看DEMO,发现BUG，可联系eajon@outlook.com,感谢关注

