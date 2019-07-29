package br.com.bangu_ao_vivo.bangu.Helper;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchAddressIntentService extends IntentService {


    public static final int SUCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final int SUCESS_RESULT_USING_GOOGLE_MAPS = 2;
    public static final String PACKAGE_NAME = "br.com.bangu_ao_vivo.bangu";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
    protected ResultReceiver mReceiver;

    private boolean geoCoderSucessful;
    protected String response;
    protected boolean addressFound;

    private boolean testAPI = false;



    public FetchAddressIntentService() {
        super("name");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String errorMessage = "";
        geoCoderSucessful = false;
        addressFound = false;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());



        Location location = intent.getParcelableExtra(LOCATION_DATA_EXTRA);
        mReceiver = intent.getParcelableExtra(RECEIVER);

        List<Address> addresses = null;

        if (geocoder.isPresent() && !testAPI) {

            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                geoCoderSucessful = true;
            } catch (IOException e) {
                errorMessage = "service not avaliable";
                Log.e("ERRORMESSAGE", errorMessage);
                e.printStackTrace();
            } catch (IllegalArgumentException illegalArgumentException) {
                errorMessage = "invalid latitude and longitude used";
                Log.e("ERRORMESSAGE", errorMessage + ", " + "latitude = " + location.getLatitude() + ",longitude = " +
                        location.getLongitude());
            }

            if (addresses == null || addresses.size() == 0) {
                if (errorMessage.isEmpty()) {
                    errorMessage = "no address found";
                    Log.e("ERRORMESSAGE", errorMessage);

                }
                deliverResultsToReceiver(FAILURE_RESULT, errorMessage);

            } else {
                Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<>();

                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address.getAddressLine(i));

                }
                Log.i("ERRORMESSAGE", "adress found");
                addressFound = true;
                deliverResultsToReceiver(SUCESS_RESULT, TextUtils.join(System.getProperty("line.separator"), addressFragments));

            }
        }
        if ((!geocoder.isPresent() || !geoCoderSucessful && isOnline())) {
            GetHTTP googleMapsApi = new GetHTTP();
            String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + location.getLatitude() + "," +
                    location.getLongitude() + "%sensor=true";

            try {
                response = googleMapsApi.run(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = new JSONObject(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                String status = jsonObject.getString("status").toString();
                if (status.equalsIgnoreCase("OK")) {
                    JSONArray results = jsonObject.getJSONArray("results");
                    JSONObject jsonzero = results.getJSONObject(0);
                    String formattedAddressApi = jsonzero.getString("formatted_address").toString();
                    Log.i("ERRORMESSAGE", "Address found using Google Maps API");
                    addressFound = true;
                    deliverResultsToReceiver(SUCESS_RESULT_USING_GOOGLE_MAPS, formattedAddressApi);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                errorMessage = "Google Maps API failed";
                deliverResultsToReceiver(FAILURE_RESULT, errorMessage);
            }

        }
        if (!addressFound && !isOnline()) {
            errorMessage = "Geocoder failed not internet";
            Log.e("ERRORMESSAGE", errorMessage);
            deliverResultsToReceiver(FAILURE_RESULT, errorMessage);

        }

    }
    private boolean isOnline () {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void deliverResultsToReceiver(int failureResult, String message) {

        Bundle bundle = new Bundle();
        bundle.putString(RESULT_DATA_KEY, message);
        mReceiver.send(failureResult, bundle);

    }


    public class GetHTTP {
        OkHttpClient client = new OkHttpClient();
        String run (String url) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()){

                return response.body().string();


            }
        }


    }
}


