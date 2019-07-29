package br.com.bangu_ao_vivo.bangu.Utils;

import java.util.List;

import br.com.bangu_ao_vivo.bangu.Model.ModelNewItemGallery;

public interface LoadDoneFirebase {

    //an interface to load movie from the firebase Database
    void onFirebaseLoadSuccess(List<ModelNewItemGallery> galleryList);
    void onFirebaseLoadFailed(String message);
}
