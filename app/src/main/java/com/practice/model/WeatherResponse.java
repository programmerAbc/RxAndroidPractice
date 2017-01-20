package com.practice.model;

/**
 * Created by gaofeng on 2017-01-17.
 */
//http://www.weather.com.cn/data/sk/101010100.html
public class WeatherResponse {
    private Weatherinfo weatherinfo;

    public Weatherinfo getWeatherinfo() {
        return weatherinfo;
    }

    public void setWeatherinfo(Weatherinfo weatherinfo) {
        this.weatherinfo = weatherinfo;
    }

    public static class Weatherinfo {
        private String city;

        private String cityid;

        private String temp;

        private String WD;

        private String WS;

        private String SD;

        private String WSE;

        private String time;

        private String isRadar;

        private String Radar;

        private String njd;

        private String qy;

        private String rain;

        public void setCity(String city) {
            this.city = city;
        }

        public String getCity() {
            return this.city;
        }

        public void setCityid(String cityid) {
            this.cityid = cityid;
        }

        public String getCityid() {
            return this.cityid;
        }

        public void setTemp(String temp) {
            this.temp = temp;
        }

        public String getTemp() {
            return this.temp;
        }

        public void setWD(String WD) {
            this.WD = WD;
        }

        public String getWD() {
            return this.WD;
        }

        public void setWS(String WS) {
            this.WS = WS;
        }

        public String getWS() {
            return this.WS;
        }

        public void setSD(String SD) {
            this.SD = SD;
        }

        public String getSD() {
            return this.SD;
        }

        public void setWSE(String WSE) {
            this.WSE = WSE;
        }

        public String getWSE() {
            return this.WSE;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getTime() {
            return this.time;
        }

        public void setIsRadar(String isRadar) {
            this.isRadar = isRadar;
        }

        public String getIsRadar() {
            return this.isRadar;
        }

        public void setRadar(String Radar) {
            this.Radar = Radar;
        }

        public String getRadar() {
            return this.Radar;
        }

        public void setNjd(String njd) {
            this.njd = njd;
        }

        public String getNjd() {
            return this.njd;
        }

        public void setQy(String qy) {
            this.qy = qy;
        }

        public String getQy() {
            return this.qy;
        }

        public void setRain(String rain) {
            this.rain = rain;
        }

        public String getRain() {
            return this.rain;
        }

        @Override
        public String toString() {
            return "city="+city+"\ncityid="+cityid+"\ntemp="+temp+"\nWD="+WD+"\nWS="+WS+"\nSD="+SD+"\nWSE="+WSE+"\ntime="+time+"\nisRadar="+isRadar+"\nRadar="+Radar+"\nnjd="+njd+"\nqy="+qy+"\nrain="+rain;
        }
    }
}
