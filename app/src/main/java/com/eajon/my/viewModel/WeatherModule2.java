package com.eajon.my.viewModel;

import android.arch.lifecycle.ViewModel;
import android.view.View;

import com.eajon.my.util.Weather;
import com.github.eajon.RxHttp;
import com.github.eajon.exception.ApiException;
import com.github.eajon.observer.HttpObserver;

import java.util.HashMap;

public class WeatherModule2 extends ViewModel {

    public void getWeather() {
        HashMap map = new HashMap();
        map.put("city", "常熟");
        new RxHttp.Builder()
                .get()
                .baseUrl("http://wthrcdn.etouch.cn/")
                .apiUrl("weather_mini")
                .addParameter(map)
                .entity(Weather.class)
                .eventId("weather")
                .cacheKey("weather")
                .build()
                .request();

    }
}
