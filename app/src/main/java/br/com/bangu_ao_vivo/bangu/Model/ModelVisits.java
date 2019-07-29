package br.com.bangu_ao_vivo.bangu.Model;

public class ModelVisits {

    String uid, name, imageUrl;

    public ModelVisits(String uid, String name, String imageUrl) {
        this.uid = uid;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public ModelVisits() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
