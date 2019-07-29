package br.com.bangu_ao_vivo.bangu.Utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Toshiba pc on 14/03/2019.
 */

public class Utils {

    private static final String TAG = "Utils";


    public static List<Person> loadPerson (Context context){
        try {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            JSONArray array = new JSONArray(loadJSONFromAsset(context, "person.json"));
            List<Person> personList = new ArrayList<>();

            for (int i = 0; i < array.length(); i++){
                Person person = gson.fromJson(array.getString(i), Person.class);
                personList.add(person);
            }
            return personList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    private static String loadJSONFromAsset (Context context, String jsonFileName){

        String json = null;
        InputStream is = null;

        try {
            AssetManager manager = context.getAssets();
            is = manager.open(jsonFileName);
            int size = is.available();
            byte[] buffer = new byte[size];

            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }


}
