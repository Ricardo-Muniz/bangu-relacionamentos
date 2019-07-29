package br.com.bangu_ao_vivo.bangu.Model;

public class ModelNewItemGallery {

    String uid, imageUrl, userID;
    boolean status;

    public ModelNewItemGallery() {
    }

    public ModelNewItemGallery(String uid, String imageUrl, String userID, boolean status) {
        this.uid = uid;
        this.imageUrl = imageUrl;
        this.userID = userID;
        this.status = status;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
