package br.com.bangu_ao_vivo.bangu.Model;

public class ModelRequest {

    String uid;
    boolean state;

    public ModelRequest(String uid, boolean state) {
        this.uid = uid;
        this.state = state;
    }

    public ModelRequest() {
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
