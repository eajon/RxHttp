package com.eajon.my.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.module.LibraryGlideModule;
import com.github.eajon.RxConfig;

import java.io.InputStream;


/**
 * 混淆：
 * -keep public class * implements com.bumptech.glide.module.GlideModule
 * -keep public class * extends com.bumptech.glide.AppGlideModule
 * -keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
 * *[] $VALUES;
 * public *;
 * }
 * <p>
 * # for DexGuard only
 * -keepresourcexmlelements manifest/application/meta-data@value=GlideModule
 * Created by Administrator on 2017/10/2.
 * glide的module类
 */
@com.bumptech.glide.annotation.GlideModule
public class GlideModule extends AppGlideModule {
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(RxConfig.get().getOkHttpClient()));
    }


    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    //自定义缓存设置
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context)
                .setMemoryCacheScreens(3)
                .build();
        builder.setMemoryCache(new LruResourceCache(calculator.getMemoryCacheSize()));
        //配置图片池大小   20MB
        builder.setBitmapPool(new LruBitmapPool(calculator.getBitmapPoolSize()));
        builder.setDiskCache(new ExternalPreferredCacheDiskCacheFactory(context, "GlideCache", 100 * 1024 * 1024));

    }
}
