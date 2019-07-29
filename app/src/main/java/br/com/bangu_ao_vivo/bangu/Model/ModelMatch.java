package br.com.bangu_ao_vivo.bangu.Model;

/**
 * Created by Toshiba pc on 20/03/2019.
 */

public class ModelMatch {

    String name, id, imgUrl;
    boolean stateMatch, matched;

    public ModelMatch(String name, String id, String imgUrl, boolean stateMatch, boolean matched) {
        this.name = name;
        this.id = id;
        this.imgUrl = imgUrl;
        this.stateMatch = stateMatch;
        this.matched = matched;
    }

    public ModelMatch() {
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public boolean isStateMatch() {
        return stateMatch;
    }

    public void setStateMatch(boolean stateMatch) {
        this.stateMatch = stateMatch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

}
