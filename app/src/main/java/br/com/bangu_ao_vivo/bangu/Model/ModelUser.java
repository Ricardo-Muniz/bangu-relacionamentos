package br.com.bangu_ao_vivo.bangu.Model;

/**
 * Created by Toshiba pc on 18/03/2019.
 */

public class ModelUser {


    String id, urlImage, name, localidade, sex, city, status, currentLocation;
    long age;
    double latitude, longitude;
    long verificate;

    public ModelUser() {
    }

    public ModelUser(String id, String urlImage, String name, String localidade, String sex, String city, String status, String currentLocation, long age, double latitude, double longitude, long verificate) {
        this.id = id;
        this.urlImage = urlImage;
        this.name = name;
        this.localidade = localidade;
        this.sex = sex;
        this.city = city;
        this.status = status;
        this.currentLocation = currentLocation;
        this.age = age;
        this.latitude = latitude;
        this.longitude = longitude;
        this.verificate = verificate;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public long getVerificate() {
        return verificate;
    }

    public void setVerificate(long verificate) {
        this.verificate = verificate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }
}
