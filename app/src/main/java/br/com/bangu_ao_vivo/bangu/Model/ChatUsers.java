package br.com.bangu_ao_vivo.bangu.Model;

public class ChatUsers {

    String name, id, urlImage, status;

    public ChatUsers(String name, String id, String urlImage, String status) {
        this.name = name;
        this.id = id;
        this.urlImage = urlImage;
        this.status = status;

    }

    public ChatUsers() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
