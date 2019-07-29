package br.com.bangu_ao_vivo.bangu.Model;

public class ModelChatList {

    public String id;

    public ModelChatList(String id, String name, String urlImage, String status) {
        this.id = id;
    }

    public ModelChatList() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
