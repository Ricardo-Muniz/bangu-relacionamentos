package br.com.bangu_ao_vivo.bangu.Model;

public class ModelPerson {

    String uid, name, urlImage;
    boolean verificate;

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

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public boolean isVerificate() {
        return verificate;
    }

    public void setVerificate(boolean verificate) {
        this.verificate = verificate;
    }
}
