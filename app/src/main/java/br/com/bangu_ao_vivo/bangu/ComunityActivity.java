package br.com.bangu_ao_vivo.bangu;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.com.bangu_ao_vivo.bangu.Activity.AtemptActivity;
import br.com.bangu_ao_vivo.bangu.Activity.LikesActivity;
import br.com.bangu_ao_vivo.bangu.Activity.MatchActivity;
import br.com.bangu_ao_vivo.bangu.Activity.ProfileUserActivity;
import br.com.bangu_ao_vivo.bangu.Dialog.DialogFilter;
import br.com.bangu_ao_vivo.bangu.Dialog.DialogFilterPerson;
import br.com.bangu_ao_vivo.bangu.Fragment.ComunityFragment;
import br.com.bangu_ao_vivo.bangu.Fragment.DisplayChatFragment;
import br.com.bangu_ao_vivo.bangu.Fragment.PersonFragment;
import br.com.bangu_ao_vivo.bangu.Helper.FetchAddressIntentService;
import br.com.bangu_ao_vivo.bangu.Model.ModelLocation;
import de.hdodenhof.circleimageview.CircleImageView;

public class ComunityActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static String LOCATION_CURRENT = "";
    private static String STRING_CITY = "";
    private TextView mTextMessage, tvNameApp, tvCounterActRelease, tvWelcomeNameRelease;
    private Context context = this;
    private CircleImageView ivProfileOne, ivProfileTwo, ivProfileCenter;

    private static final int REQUEST_CHECK_SETTINGS = 613;

    private RelativeLayout layMenu;
    private LinearLayout layFilter;
    private ImageView ivMenu, ivLikess;
    private CardView cvAcessBangu;

    RecyclerView recyclerView;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private double latitude;
    private double longitude;

    private FusedLocationProviderClient mFusedLocationClient;

    protected Location mLastKnowLocation;
    private AddressResultReceiver mResultReceiver;

    public static final int SUCESS_RESULT = 0;
    public static final int SUCESS_RESULT_USING_GOOGLE_MAPS = 2;
    public static final String PACKAGE_NAME = "br.com.bangu_ao_vivo.bangu";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    FirebaseUser fuser;

    private static final int REQUEST_FINE_LOCATION = 2;

    private String mAddressOutput;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        //receptor do fragmento
        android.support.v4.app.Fragment selectedFragment = null;

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    FragmentTransaction transactionOne = getSupportFragmentManager().beginTransaction();
                    transactionOne.replace(R.id.container, new ComunityFragment());
                    transactionOne.commit();
                    layFilter.setVisibility(View.VISIBLE);
                    ivMenu.setVisibility(View.VISIBLE);
                    ivLikess.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_dashboard:
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, new PersonFragment());
                    transaction.commit();
                    layFilter.setVisibility(View.VISIBLE);
                    ivLikess.setVisibility(View.VISIBLE);
                    ivMenu.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_notifications:
                    FragmentTransaction transactionTwo = getSupportFragmentManager().beginTransaction();
                    transactionTwo.replace(R.id.container, new DisplayChatFragment());
                    transactionTwo.commit();
                    layFilter.setVisibility(View.GONE);
                    ivLikess.setVisibility(View.VISIBLE);
                    ivMenu.setVisibility(View.GONE);
                    return true;
            }

            return false;


        }
    };
    private boolean truedLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comunity);

        //cleanDataSavedStore();

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        askForLocationChange();
        printKeyHash();

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //iniciando id
        inicializeIDS();

        //retirando titulos do menu
        //BottomNavigationViewHelper.disableShiftMode(navigation);
        //inicializando na tela principal
        navigation.setSelectedItemId(R.id.navigation_dashboard);

        inicializeFragmentHome(navigation);

        //dando click de widgets
        layMenu.setOnClickListener(this);
        layFilter.setOnClickListener(this);

        updateMatch();

    }

    //metodo de identificaçao da chave para incluir no projeto do facebook login API
    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("br.com.bangu_ao_vivo.bangu",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        cleanDataSavedStore();
        finish();
    }

    private void askForLocationChange() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
               truedLocation = true;
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(ComunityActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException ignored) {
                    }
                }
                truedLocation = false;
            }
        });
    }

    private void inicializeFragmentHome(BottomNavigationView navigationView) {
        if (this.getIntent().getExtras() == null) {
            FragmentTransaction transactionOne = getSupportFragmentManager().beginTransaction();
            transactionOne.replace(R.id.container, new PersonFragment());
            transactionOne.commit();

        } else if (this.getIntent().getExtras().getString("person").equals("comu")) {
            FragmentTransaction transactionOne = getSupportFragmentManager().beginTransaction();
            transactionOne.replace(R.id.container, new ComunityFragment());
            transactionOne.commit();
            filterActive(navigationView);
        } else if (!this.getIntent().getExtras().getString("person").equals("person")){
            FragmentTransaction transactionOne = getSupportFragmentManager().beginTransaction();
            transactionOne.replace(R.id.container, new PersonFragment());
            transactionOne.commit();
            filterActivePerson(navigationView);
        }
    }

    public void updateMatch() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference mostafa = ref.child("user").child(fuser.getUid()).child("match");

        mostafa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String uid = dataSnapshot.child("id").getValue(String.class);
                    boolean state = dataSnapshot.child("stateMatch").getValue(Boolean.class);
                    boolean stateMatched = dataSnapshot.child("matched").getValue(Boolean.class);

                    if (state) {
                        Intent it = new Intent(ComunityActivity.this, AtemptActivity.class);
                        it.putExtra("user", uid);
                        it.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(it);
                    } else if (stateMatched) {
                        Intent it = new Intent(ComunityActivity.this, MatchActivity.class);
                        it.putExtra("user", uid);
                        it.putExtra("myUser", fuser.getUid());
                        it.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(it);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void inicializeIDS() {
        layMenu = findViewById(R.id.layMenu);
        layFilter = findViewById(R.id.layFilter);
        tvCounterActRelease = findViewById(R.id.tvCounterActRelease);
        tvWelcomeNameRelease = findViewById(R.id.tvWelcomeNameRelease);
        cvAcessBangu = findViewById(R.id.cvAcessBangu);
        ivProfileOne = findViewById(R.id.ivProfileOne);
        ivProfileTwo = findViewById(R.id.ivProfileTwo);
        ivProfileCenter = findViewById(R.id.ivProfileCenter);
        ivLikess = findViewById(R.id.ivLikess);
        ivMenu = findViewById(R.id.ivMenu);
    }

    public void filter() {
        DialogFilter filter = new DialogFilter();
        filter.show(getFragmentManager(), "dialog");
    }

    public void filterPerson() {
        DialogFilterPerson filter = new DialogFilterPerson();
        filter.show(getFragmentManager(), "dialog");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (truedLocation) {
            status("online", 1);
        }
        if (googleApiClient.isConnected()) {
            requestLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (truedLocation) {
            status("offline", 2);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layFilter:
                if (ivMenu.getVisibility() == View.VISIBLE) {
                    filter();
                } else {
                    filterPerson();
                }
                break;

            case R.id.layMenu:
                if (ivMenu.getVisibility() == View.VISIBLE) {
                    Intent it = new Intent(ComunityActivity.this, ProfileUserActivity.class);
                    startActivity(it);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    ComunityActivity.super.finish();
                } else {
                    Intent it = new Intent(ComunityActivity.this, LikesActivity.class);
                    startActivity(it);
                }
                break;

        }
    }

    private void fetchAddressButtonHandler() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    mLastKnowLocation = location;

                    if (mLastKnowLocation == null) {
                        return;
                    }
                    if (!Geocoder.isPresent()) {
                        Toast.makeText(getApplicationContext(), "No geocoder present", Toast.LENGTH_LONG).show();
                        return;
                    }
                    startIntetService();
                }
            });
        }

    }

    private void cleanDataSavedStore() {
        SharedPreferences preferences = getSharedPreferences("filter", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("countryRegion", "");
        editor.putString("sexPerson", "");
        editor.putBoolean("filterActive", false);
        editor.putLong("ageMin", 0);
        editor.putLong("ageMax", 0);

        editor.putString("countryRegionPerson", "");
        editor.putBoolean("filterActivePerson", false);
        editor.putString("sexPersonPerson", "");
        editor.putLong("ageMinPerson", 0);
        editor.putLong("ageMaxPerson", 0);

        editor.apply();
    }


    private void filterActivePerson(BottomNavigationView navigation) {
        Intent intent = getIntent();
        navigation.setSelectedItemId(R.id.navigation_dashboard);
        layFilter.setVisibility(View.VISIBLE);
    }


    private void filterActive(BottomNavigationView navigation) {
        Intent intent = getIntent();
        navigation.setSelectedItemId(R.id.navigation_home);
        layFilter.setVisibility(View.VISIBLE);
    }

    private void startIntetService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        mResultReceiver = new AddressResultReceiver(new Handler());
        intent.putExtra(RECEIVER, mResultReceiver);
        intent.putExtra(LOCATION_DATA_EXTRA, mLastKnowLocation);
        startService(intent);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("user");

        String currentLatitude = String.valueOf(latitude);
        String currentLongitude = String.valueOf(longitude);
        String currentRegion = String.valueOf(mAddressOutput);

        try {
            getCityNameByCoordinates(latitude, longitude);
            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(latitude, longitude, 1);

            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();

            String currentState = state + ", " + country;

            SharedPreferences preferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);

            final String mId = preferences.getString("id", "");
            ModelLocation userLocal = new ModelLocation();
            userLocal.setLatitude(String.valueOf(currentLatitude));
            userLocal.setLongitude(currentLongitude);
            userLocal.setCurrentLocation(currentRegion);
            userLocal.setState(currentState);
            userLocal.setCity(STRING_CITY);

            reference.child(mId).child("location").setValue(userLocal);

        } catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }

        fetchAddressButtonHandler();

    }

    private String getCityNameByCoordinates(double lat, double lon) throws IOException {
        Geocoder mGeocoder = new Geocoder(this.getApplicationContext(), Locale.getDefault());
        List<Address> addresses = mGeocoder.getFromLocation(lat, lon, 10);
        if (addresses != null && addresses.size() > 0) {
            for (Address adr : addresses) {
                if (adr.getLocality() != null && adr.getLocality().length() > 0) {
                    return STRING_CITY = adr.getLocality();
                }
            }
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                   // Toast.makeText(this, "Location is now on", Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    finish();
                   // Toast.makeText(this, "User didn't allowed to change location settings", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            mAddressOutput = resultData.getString(RESULT_DATA_KEY);
            displayAddressOutput();

            if (resultCode == SUCESS_RESULT) {
                // Toast.makeText(getApplicationContext(), "Address found", Toast.LENGTH_LONG).show();
            } else if (resultCode == SUCESS_RESULT_USING_GOOGLE_MAPS) {
               // Toast.makeText(getApplicationContext(), "Address found using Google Maps API", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void displayAddressOutput() {
        //tvAddress .setText(mAddressOutput);
    }

    private void nameRegion(double lat, double lng) {
        try {
            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(lat, lng, 1);

            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();

            String currentState = state + ", " + country;

        } catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager
                .PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
            }
        }
    }

    private void status(String status, int timestamp) {
        databaseReference = FirebaseDatabase.getInstance().getReference("user").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        hashMap.put("timestamp", timestamp);

        databaseReference.updateChildren(hashMap);
    }


}