package com.eajon.my.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eajon.my.model.Weather;
import com.github.eajon.RxHttp;
import com.github.eajon.exception.ApiException;
import com.github.eajon.observer.HttpObserver;

import java.util.HashMap;

public class WeatherModule extends ViewModel {
    private MutableLiveData<Weather> weather;

    public LiveData<Weather> getWeather() {
        if (weather == null) {
            weather = new MutableLiveData<Weather>();
            loadWeather();
        }
        return weather;
    }

    private void loadWeather() {
        HashMap map = new HashMap();
        map.put("city", "常熟");
        new RxHttp.Builder()
                .baseUrl("http://wthrcdn.etouch.cn/")
                .get("weather_mini")
                .addParameter(map)
                .tag("weather")
                .build()
                .request(new HttpObserver<Weather>() {
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
