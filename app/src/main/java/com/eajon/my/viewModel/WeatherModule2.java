package com.eajon.my.viewModel;

import android.arch.lifecycle.ViewModel;

import com.eajon.my.model.BaseResponse;
import com.github.eajon.RxHttp;
import com.github.eajon.annotation.Name;

public class WeatherModule2 extends ViewModel {

    public void getWeather() {
        TestGson testGson = new TestGson();
        testGson.setCity2("常熟");
        testGson.setTest("111");
        testGson.setTest2("222");
        testGson.setCity3("222");
        testGson.setCity4(1);
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(1);
        baseResponse.setMessage("HAHAH");
        testGson.setBaseResponse(baseResponse);
//        HashMap map = new HashMap();
//        map.put("city", "常熟");
        new RxHttp.Builder()
                .get()
                .baseUrl("http://wthrcdn.etouch.cn/")
                .apiUrl("weather_mini")
                .addObjectParameter(testGson)
//                .addParameter(map)
//                .entity(Weather.class)
                .tag("weather")
                .build()
                .request();

    }

    private class TestGson {

        @Name(value = "haha", require = false)
        private String test;

        @Name(value = "haha2", require = false)
        private String test2;

        @Name(value = "city")
        private String city2;

        private String city3;

        private int city4;


        private BaseResponse baseResponse;

        public BaseResponse getBaseResponse() {
            return baseResponse;
        }

        public void setBaseResponse(BaseResponse baseResponse) {
            this.baseResponse = baseResponse;
        }

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }

        public String getTest2() {
            return test2;
        }

        public void setTest2(String test2) {
            this.test2 = test2;
        }

        public String getCity2() {
            return city2;
        }

        public void setCity2(String city2) {
            this.city2 = city2;
        }

        public String getCity3() {
            return city3;
        }

        public void setCity3(String city3) {
            this.city3 = city3;
        }

        public int getCity4() {
            return city4;
        }

        public void setCity4(int city4) {
            this.city4 = city4;
        }

    }
}
