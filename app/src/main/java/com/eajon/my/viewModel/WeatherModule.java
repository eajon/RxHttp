package com.eajon.my.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.eajon.my.model.Weather;
import com.github.eajon.RxHttp;
import com.github.eajon.exception.ApiException;
import com.github.eajon.observer.HttpObserver;

import java.util.HashMap;

public class WeatherModule extends ViewModel {
    private MutableLiveData <Weather> weather;

    public LiveData <Weather> getWeather() {
        if (weather == null) {
            weather = new MutableLiveData <Weather>();
            loadWeather();
        }
        return weather;
    }

    private void loadWeather() {
        HashMap map = new HashMap();
        map.put("city", "常熟");
        new RxHttp.Builder()
                .get()
                .baseUrl("http://wthrcdn.etouch.cn/")
                .apiUrl("weather_mini")
                .addParameter(map)
                .entity(Weather.class)
                .tag("weather")
                .isStick(true)
                .build()
                .request(new HttpObserver <Weather>() {
                    @Override
                    public void onSuccess(Weather o) {
                        weather.postValue(o);
                    }

                    @Override
                    public void onError(ApiException t) {

                    }
                });

    }


}
