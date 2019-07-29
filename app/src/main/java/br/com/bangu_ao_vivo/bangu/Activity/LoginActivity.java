package br.com.bangu_ao_vivo.bangu.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.maps.android.SphericalUtil;
import com.shuhart.stepview.StepView;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.bangu_ao_vivo.bangu.ComunityActivity;
import br.com.bangu_ao_vivo.bangu.Helper.Connection;
import br.com.bangu_ao_vivo.bangu.Helper.FetchAddressIntentService;
import br.com.bangu_ao_vivo.bangu.Model.ModelEditProfile;
import br.com.bangu_ao_vivo.bangu.Model.ModelLocation;
import br.com.bangu_ao_vivo.bangu.Model.ModelMatch;
import br.com.bangu_ao_vivo.bangu.Model.ModelReportUser;
import br.com.bangu_ao_vivo.bangu.Model.ModelRequest;
import br.com.bangu_ao_vivo.bangu.R;
import br.com.bangu_ao_vivo.bangu.Utils.Person;
import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 1;
    private static final int RC_SIGN_IN = 103;
    private static final int MY_REQ = 7171;
    private static String STRING_CITY = "";
    private TextView tv_welcome, tvHomem, tvMulher, tvAge, tvReconnect;
    private CardView cv_cad, layLoginFacebook;
    private TextView tvHow, tvMudar, tvFinalizar;
    private LinearLayout swHomem, swMulher, layMulher, layHomem, layConclud;
    private RelativeLayout layInputInitial, layInputMore, laySex, layChangeProfile, layWelcome, layMain, layNotConnect;
    private EditText edtNome, edtEmail, edtSenha, edtAge, edtLocal;
    private CardView cvAge, cvProximo, cv_cadastrar_google;
    private DatePickerDialog datePicker;
    private Calendar calendar;
    private StepView stepView;
    private Context context = this;

    private CircleImageView cvProfile;

    private FirebaseAuth auth;
    private CallbackManager callbackManager;

    private final int RESULT_GALLERY = 1;
    private final int PERMISSAO_REQUEST = 3;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public static final int SUCESS_RESULT = 0;
    public static final int SUCESS_RESULT_USING_GOOGLE_MAPS = 2;
    public static final String PACKAGE_NAME = "br.com.bangu_ao_vivo.bangu";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    private static final int REQUEST_CHECK_SETTINGS = 613;

    public static int AGE = 0;

    private static final int REQUEST_FINE_LOCATION = 2;

    private static String currentState = "";
    private static String currentLatitude = "";
    private static String currentLongitude = "";
    private static String currentRegion = "";
    private static String currentCity = "";

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private double latitude;
    private double longitude;

    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastKnowLocation;
    private AddressResultReceiver mResultReceiver;
    private String mAddressOutput;

    GoogleSignInClient mGoogleSignInClient;
    SignInButton signInButton;

    View my_view;

    Uri filePath;
    private ProgressDialog pd;

    private int STATUS_LAY = 0;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReferenceFromUrl("gs://storagebangu.appspot.com/");

    private String mSexo = "Homem";

    private FusedLocationProviderClient fusedLocationClient;
    private String TAG;

    List<AuthUI.IdpConfig> providers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //inicializaçao do firebase
        inicializeFirebase();

        //incia widgets
        incializeIdsWidgets();

        auth = FirebaseAuth.getInstance();

        //location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationUser();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.token_login))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        printKeyHash();
        FacebookSdk.setApplicationId(getString(R.string.facebook_application_id));
        FacebookSdk.sdkInitialize(getApplicationContext());

        //visibilidades
        laySex.setVisibility(View.VISIBLE);

        //inicializa stepper
        stepper();

        //inicializa layout
        statusLogin(1);

        //intencao correta
        intentLoginConclude();

        //teste de conexao
        testConnect();

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                pd.show();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        //auto implementando click
        cv_cad.setOnClickListener(this);
        cvAge.setOnClickListener(this);
        swHomem.setOnClickListener(this);
        swMulher.setOnClickListener(this);
        cvProximo.setOnClickListener(this);
        layConclud.setOnClickListener(this);
        layLoginFacebook.setOnClickListener(this);
        tvReconnect.setOnClickListener(this);
        cv_cadastrar_google.setOnClickListener(this);

        //cor inicial no ola do login
        Spannable spannable = new SpannableString("Ola, bem vindo, não nos conhecemos ainda, me fala de você.");
        spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorMain)), 0,
                5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_welcome.setText(spannable);

        pd = new ProgressDialog(this);
        pd.setMessage("enviando...");
        pd.setCancelable(false);

        //dando o onclick aos atributos
        cvProfile.setOnClickListener(this);
        tvFinalizar.setOnClickListener(this);

    }

    private void showsiginOptions() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.Mytheme)
                .build(), MY_REQ
        );
    }

    private void askForLocationChange() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(LoginActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException ignored) {
                    }
                }
            }
        });
    }

    private boolean verifyConnection() {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null && conectivtyManager
                .getActiveNetworkInfo().isAvailable() && conectivtyManager
                .getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }

    private void testConnect() {
        if (verifyConnection()) {
            layMain.setVisibility(View.VISIBLE);
            layNotConnect.setVisibility(View.GONE);
            locationUser();
        } else {
            layNotConnect.setVisibility(View.VISIBLE);
            layMain.setVisibility(View.GONE);
        }
    }

    private void incializeIdsWidgets() {
        //chamadas de ids
        tv_welcome = findViewById(R.id.tv_welcome);
        cv_cad = findViewById(R.id.cv_cadastrar);
        edtNome = findViewById(R.id.edt_nome);
        edtEmail = findViewById(R.id.edt_email);
        edtSenha = findViewById(R.id.edt_senha);
        cvAge = findViewById(R.id.cv_nascimento);
        cvProximo = findViewById(R.id.cvProximo);
        swHomem = findViewById(R.id.sw_Homem);
        swMulher = findViewById(R.id.sw_Mulher);
        tvHomem = findViewById(R.id.tvHomem);
        tvHomem = findViewById(R.id.tvHomem);
        tvAge = findViewById(R.id.tvAge);
        layHomem = findViewById(R.id.lay_homem);
        layMulher = findViewById(R.id.lay_mulher);
        layInputInitial = findViewById(R.id.lay_input);
        layInputMore = findViewById(R.id.lay_input_more);
        laySex = findViewById(R.id.lay_sex);
        stepView = findViewById(R.id.step_view);
        layChangeProfile = findViewById(R.id.layChangeProf);
        layWelcome = findViewById(R.id.lay_welcome);

        tvHow = findViewById(R.id.tvHow);
        tvMudar = findViewById(R.id.tvMudar);
        tvReconnect = findViewById(R.id.tvTentarNovamente);
        tvFinalizar = findViewById(R.id.tvFinalizar);
        cvProfile = findViewById(R.id.cvProfile);
        cv_cadastrar_google = findViewById(R.id.cv_cadastrar_google);

        layConclud = findViewById(R.id.layConclud);
        layMain = findViewById(R.id.layMain);
        layNotConnect = findViewById(R.id.layFailedConnection);

        my_view = findViewById(R.id.layContentLogin);

        layLoginFacebook = findViewById(R.id.cv_cadastrar_fcb);
    }

    private void fetchAddressButtonHandler() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    mLastKnowLocation = location;

                    if (mLastKnowLocation == null) {
                        return;
                    }
                    if (!Geocoder.isPresent()) {
                        //Toast.makeText(getApplicationContext(), "No geocoder present", Toast.LENGTH_LONG).show();
                        return;
                    }
                    startIntetService();
                }
            });
        }

    }

    //update de localização no momento de execuçao do app
    private void updateLocation(String latitude, String longitude, String currentLocation) {

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        ModelLocation location = new ModelLocation();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setCurrentLocation(currentLocation);
        location.setState(currentState);

        databaseReference.child("location").setValue(location);

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
        // requestLocationUpdates();

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

        currentLatitude = String.valueOf(latitude);
        currentLongitude = String.valueOf(longitude);
        currentRegion = String.valueOf(mAddressOutput);

        nameRegion(latitude, longitude);
        // updateLocation(String.valueOf(latitude), String.valueOf(longitude), String.valueOf(mAddressOutput));

        // tvLocal.setText(String.valueOf(latitude) + " , " + String.valueOf(longitude));
        fetchAddressButtonHandler();

        double lat2 = -16.828076;
        double lng2 = -49.372462;

        LatLng posicaoInicial = new LatLng(latitude, longitude);
        LatLng posicaoFinal = new LatLng(lat2, lng2);

        double distance = SphericalUtil.computeDistanceBetween(posicaoInicial, posicaoFinal);
        Log.i("LOGET", "A Distancia é = " + formatNumber(distance));

    }

    private String formatNumber(double distance) {
        String unit = "m";
        if (distance == 1000) {
            distance /= 1000;
            unit = "km";
        }
        return String.format("%4.3f%s", distance, unit);
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
                //Toast.makeText(getApplicationContext(), "Address found using Google Maps API", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void nameRegion(double lat, double lng) {
        try {
            getCityNameByCoordinates(latitude, longitude);
            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(lat, lng, 1);

            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();

            currentState = state + ", " + country;

        } catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
    }



    private void displayAddressOutput() {
        //tvAddress .setText(mAddressOutput);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {

        String emailValidate = edtEmail.getText().toString();
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher m = p.matcher(emailValidate);
        boolean matchFound = m.matches();

        switch (v.getId()) {
            case R.id.cv_cadastrar:
                if (edtNome.getText().toString().isEmpty()) {
                    edtNome.requestFocus();
                    edtNome.setError("Obrigatorio");
                } else if (edtEmail.getText().toString().isEmpty()) {
                    edtEmail.requestFocus();
                    edtEmail.setError("Obrigatorio");
                } else if (!matchFound) {
                    edtEmail.requestFocus();
                    edtEmail.setError("Email invalido");
                } else {

                    String name = edtNome.getText().toString();

                    statusLogin(2);
                    STATUS_LAY = 2;
                    tv_welcome.setText(name + ", estamos quase la, so falta mais alguns dados e uma foto maneira hehe!");

                    stepView.go(1, true);
                    if (verifyConnection()) {
                        testConnect();
                    } else {
                        testConnect();
                    }

                }
                break;

            case R.id.sw_Homem:
                layHomem.setVisibility(View.VISIBLE);
                layMulher.setVisibility(View.GONE);

                mSexo = "Homem";
                break;

            case R.id.sw_Mulher:
                layMulher.setVisibility(View.VISIBLE);
                layHomem.setVisibility(View.GONE);

                mSexo = "Mulher";
                break;

            case R.id.cv_nascimento:
                calendar = Calendar.getInstance();

                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                datePicker = new DatePickerDialog(LoginActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {
                        //date
                        tvAge.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
                        AGE = mYear;
                    }

                }, day, month, year);
                datePicker.show();
                break;

            case R.id.cvProximo:
                if (edtSenha.getText().toString().isEmpty()) {
                    edtSenha.requestFocus();
                    edtSenha.setError("Obrigatorio");
                } else if (edtSenha.length() < 6) {
                    edtSenha.setError("Inferior a 6 digitos");
                    edtSenha.requestFocus();
                } else {
                    statusLogin(3);
                    STATUS_LAY = 3;
                    imageProfile();
                    stepView.go(2, true);
                }
                //verificar conexao para evitar erros
                if (verifyConnection()) {
                    testConnect();
                } else {
                    testConnect();
                }
                break;

            case R.id.cvProfile:
                Intent intentChangeImage = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentChangeImage, RESULT_GALLERY);
                break;

            case R.id.layConclud:
                String name = edtNome.getText().toString();
                String email = edtEmail.getText().toString();
                String password = edtSenha.getText().toString();

                Calendar cal = Calendar.getInstance();
                int yearOne = cal.get(Calendar.YEAR);
                int age = yearOne - AGE;

                if (age == 2019) {
                    age = 18;
                }

                loginUser(name, email, password, mSexo, age);
                break;

            case R.id.cv_cadastrar_fcb:
               // loginFacebook();
                break;

            case R.id.tvTentarNovamente:
                verifyConnection();
                if (verifyConnection()) {
                    testConnect();
                } else {
                    testConnect();
                }
                break;

            case R.id.cv_cadastrar_google:
                Intent intent = new Intent(LoginActivity.this, NewSessionLoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                LoginActivity.super.finish();
                break;
        }

    }

    private void locationUser() {

        if (verifyConnection()) {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(LoginActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(LoginActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_READ_LOCATION);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                }
                            }
                        });
            }
        } else {
            testConnect();
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = auth.getCurrentUser();

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            final DatabaseReference data = ref.child("user");

                            data.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child(user.getUid()).exists()) {

                                        String name = dataSnapshot.child(user.getUid()).child("name").getValue(String.class);
                                        String photoUrl = dataSnapshot.child(user.getUid()).child("urlImage").getValue(String.class);
                                        String uid = dataSnapshot.child(user.getUid()).child("id").getValue(String.class);

                                        SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();

                                        String status = "complete";

                                        editor.putString("name", name);
                                        editor.putString("image", photoUrl);
                                        editor.putString("id", uid);
                                        editor.putString("statusLogin", status);
                                        editor.apply();

                                        pd.dismiss();

                                        Intent intent = new Intent(LoginActivity.this,
                                                ComunityActivity.class);
                                        startActivity(intent);

                                        LoginActivity.this.finish();
                                    } else {

                                        String description = "Musica, entretenimento, trabalho(nem sempre), cultura e coisas que me fazem me sentir melhor do que naturalmente.";
                                        String interest = "Estou em busca de conhecer novas pessoas, interagir mais. Em um momento da minha vida de descobertas e desejos. Não tenho interesse em coisas que não agregam no meu bem estar, então busco pessoas que queiram algo como eu que é viver a vida adoidado.";
                                        String email = user.getEmail();
                                        String uid = user.getUid();
                                        String name = user.getDisplayName();
                                        String sex = "Homem";
                                        String photoUrl = user.getPhotoUrl().toString();
                                        photoUrl = photoUrl + "?height=500";

                                        Person person = new Person();
                                        person.setName(name);
                                        person.setEmail(email);
                                        person.setUrlImage(photoUrl);
                                        person.setInterest(interest);
                                        person.setId(uid);
                                        person.setSex(sex);
                                        person.setAge(18);

                                        ModelEditProfile profile = new ModelEditProfile();
                                        profile.setDescription(description);
                                        profile.setInterest(interest);
                                        profile.setName(name);
                                        profile.setVerificate(2);
                                        profile.setTypeVerified("facebook");

                                        ModelLocation location = new ModelLocation();
                                        location.setLatitude(currentLatitude);
                                        location.setLongitude(currentLatitude);
                                        location.setCurrentLocation(currentRegion);
                                        location.setState(currentState);
                                        location.setCity(STRING_CITY);

                                        ModelReportUser reportUser = new ModelReportUser();
                                        reportUser.setBlockOption(true);
                                        reportUser.setUidReported(user.getUid());

                                        ModelRequest request = new ModelRequest();
                                        request.setUid(uid);
                                        request.setState(false);

                                        databaseReference.child("user").child(person.getId()).setValue(person);
                                        databaseReference.child("user").child(person.getId()).child("location").setValue(location);
                                        databaseReference.child("user").child(person.getId()).child("blackList").child(user.getUid()).setValue(reportUser);
                                        databaseReference.child("user").child(person.getId()).child("extra").setValue(profile);
                                        databaseReference.child("user").child(person.getId()).child("requests").child(person.getId()).setValue(request);
                                        databaseReference.child("user").child(person.getId()).child("permited").child(person.getId()).setValue(request);


                                        SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();

                                        String status = "complete";

                                        editor.putString("name", name);
                                        editor.putString("image", photoUrl);
                                        editor.putString("id", uid);
                                        editor.putString("statusLogin", status);
                                        editor.apply();

                                        firstMatch(uid);

                                        pd.dismiss();

                                        Intent intent = new Intent(LoginActivity.this,
                                                ComunityActivity.class);
                                        startActivity(intent);

                                        LoginActivity.this.finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(context, "Login ja existente, verifique o meio utilizado.", Toast.LENGTH_LONG).show();
                                }
                            });

                        } else {
                            pd.dismiss();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Autenticação falhou.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            auth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInWithCredential:success");
                                FirebaseUser user = auth.getCurrentUser();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                final DatabaseReference data = ref.child("user");

                                data.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.child(user.getUid()).exists()) {

                                            String name = dataSnapshot.child(user.getUid()).child("name").getValue(String.class);
                                            String photoUrl = dataSnapshot.child(user.getUid()).child("urlImage").getValue(String.class);
                                            String uid = dataSnapshot.child(user.getUid()).child("id").getValue(String.class);

                                            SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = preferences.edit();

                                            String status = "complete";

                                            editor.putString("name", name);
                                            editor.putString("image", photoUrl);
                                            editor.putString("id", uid);
                                            editor.putString("statusLogin", status);
                                            editor.apply();

                                            pd.dismiss();

                                            Intent intent = new Intent(LoginActivity.this,
                                                    ComunityActivity.class);
                                            startActivity(intent);
                                            LoginActivity.this.finish();

                                        } else {
                                            String description = "Musica, entretenimento, trabalho(nem sempre), cultura e coisas que me fazem me sentir melhor do que naturalmente.";
                                            String interest = "Estou em busca de conhecer novas pessoas, interagir mais. Em um momento da minha vida de descobertas e desejos. Não tenho interesse em coisas que não agregam no meu bem estar, então busco pessoas que queiram algo como eu que é viver a vida adoidado.";
                                            String photoUrl = user.getPhotoUrl().toString();
                                            String name = user.getDisplayName();
                                            String email = user.getEmail();
                                            String sex = "Homem";
                                            String uid = user.getUid();

                                            Person person = new Person();
                                            person.setName(name);
                                            person.setEmail(email);
                                            person.setUrlImage(photoUrl);
                                            person.setId(uid);
                                            person.setSex(sex);
                                            person.setAge(18);

                                            ModelEditProfile profile = new ModelEditProfile();
                                            profile.setDescription(description);
                                            profile.setInterest(interest);
                                            profile.setName(name);
                                            profile.setVerificate(2);
                                            profile.setTypeVerified("google");

                                            ModelLocation location = new ModelLocation();
                                            location.setLatitude(currentLatitude);
                                            location.setLongitude(currentLatitude);
                                            location.setCurrentLocation(currentRegion);
                                            location.setState(currentState);
                                            location.setCity(STRING_CITY);

                                            ModelReportUser reportUser = new ModelReportUser();
                                            reportUser.setBlockOption(true);
                                            reportUser.setUidReported(user.getUid());

                                            ModelRequest request = new ModelRequest();
                                            request.setUid(uid);
                                            request.setState(false);

                                            databaseReference.child("user").child(person.getId()).setValue(person);
                                            databaseReference.child("user").child(person.getId()).child("location").setValue(location);
                                            databaseReference.child("user").child(person.getId()).child("blackList").child(user.getUid()).setValue(reportUser);
                                            databaseReference.child("user").child(person.getId()).child("extra").setValue(profile);
                                            databaseReference.child("user").child(person.getId()).child("requests").child(person.getId()).setValue(request);
                                            databaseReference.child("user").child(person.getId()).child("permited").child(person.getId()).setValue(request);

                                            SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = preferences.edit();

                                            String status = "complete";

                                            editor.putString("name", name);
                                            editor.putString("image", photoUrl);
                                            editor.putString("id", uid);
                                            editor.putString("statusLogin", status);
                                            editor.apply();

                                            firstMatch(uid);

                                            pd.dismiss();

                                            Intent intent = new Intent(LoginActivity.this,
                                                    ComunityActivity.class);
                                            startActivity(intent);

                                            LoginActivity.this.finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                         Toast.makeText(context, "Login ja existente, verifique o meio utilizado.", Toast.LENGTH_LONG).show();
                                    }
                                });

                            } else {
                                pd.dismiss();
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LoginActivity.this, "Autenticação falhou.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //metodo de identificaçao da chave para incluir no projeto do facebook login API
    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("br.com.bangu_ao_vivo.bangu",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void stepper() {

        stepView.getState()
                .selectedTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                .animationDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .animationType(StepView.ANIMATION_CIRCLE)
                .selectedCircleColor(ContextCompat.getColor(this, R.color.colorMain))
                .selectedStepNumberColor(ContextCompat.getColor(this, R.color.colorWhite))
                // You should specify only stepsNumber or steps array of strings.
                // In case you specify both steps array is chosen.
                .steps(new ArrayList<String>() {{
                    add("");
                    add("");
                    add("");
                }})
                // You should specify only steps number or steps array of strings.
                // In case you specify both steps array is chosen.
                .stepsNumber(4)
                .animationDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .typeface(ResourcesCompat.getFont(context, R.font.overpass))
                // other state methods are equal to the corresponding xml attributes
                .commit();

    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
        auth = Connection.getFirebaseAuth();

    }

    @Override
    protected void onResume() {
        super.onResume();
        askForLocationChange();
        statusLogin(STATUS_LAY);
        if (googleApiClient.isConnected()) {
            requestLocationUpdates();
        }
        locationUser();
    }

    @Override
    protected void onPause() {
        super.onPause();
        statusLogin(STATUS_LAY);
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void statusLogin(int state) {

        if (state == 1) {
            layInputInitial.setVisibility(View.VISIBLE);
            laySex.setVisibility(View.VISIBLE);

            STATUS_LAY = 1;
        } else if (state == 2) {
            layInputInitial.setVisibility(View.GONE);
            laySex.setVisibility(View.GONE);

            layInputMore.setVisibility(View.VISIBLE);
            STATUS_LAY = 2;
        } else if (state == 3) {
            layInputMore.setVisibility(View.GONE);
            layWelcome.setVisibility(View.GONE);

            layChangeProfile.setVisibility(View.VISIBLE);
            STATUS_LAY = 3;
        }


    }

    private void inicializeFirebase() {
        FirebaseApp.initializeApp(context);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void imageProfile() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSAO_REQUEST);
            }
        }

        //spannable
        Spannable spannabledois = new SpannableString("perfil > foto > galeria");
        spannabledois.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorMain)),
                9, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvHow.setText(spannabledois);

        String name = edtNome.getText().toString();

        tvMudar.setText(name + ", você precisa ficar mais apresentavel, use um perfil.");


    }

    private void loginUser(final String name, final String email, final String password, final String sex, int idade) {

        if (filePath != null) {

            pd.show();

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        //atençao nesta linha de codigo
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        assert firebaseUser != null;
                        final String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        uploadData(name, password, email, sex, currentuser, idade);
                    } else {

                    }


                }
            });


        }


    }

    private void uploadData(final String name, final String password,
                            final String email, final String sex, final String currentuser, final int idade) {
        Random random = new Random();
        final int i1 = random.nextInt(10000 - 300) + 65;

        StorageReference child = storageReference.child(i1 + "image");

        UploadTask uploadTask = child.putFile(filePath);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                storageReference.child(i1 + "image").getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();

                                String description = "Musica, entretenimento, trabalho(nem sempre), cultura e coisas que me fazem me sentir melhor do que naturalmente.";
                                String interest = "Estou em busca de conhecer novas pessoas, interagir mais. Em um momento da minha vida de descobertas e desejos. Não tenho interesse em coisas que não agregam no meu bem estar, então busco pessoas que queiram algo como eu que é viver a vida adoidado.";
                                String sex = "Homem";

                                Person person = new Person();
                                person.setName(name);
                                person.setSenha(password);
                                person.setEmail(email);
                                person.setSex(sex);
                                person.setUrlImage(url);
                                person.setId(currentuser);
                                person.setAge(idade);

                                ModelEditProfile profile = new ModelEditProfile();
                                profile.setDescription(description);
                                profile.setInterest(interest);
                                profile.setName(name);
                                profile.setVerificate(1);
                                profile.setTypeVerified("traditional");

                                ModelLocation location = new ModelLocation();
                                location.setLatitude(currentLatitude);
                                location.setLongitude(currentLongitude);
                                location.setCurrentLocation(currentRegion);
                                location.setState(currentState);
                                location.setCity(STRING_CITY);

                                ModelReportUser reportUser = new ModelReportUser();
                                reportUser.setBlockOption(true);
                                reportUser.setUidReported(person.getId());

                                ModelRequest request = new ModelRequest();
                                request.setUid(currentuser);
                                request.setState(false);

                                databaseReference.child("user").child(person.getId()).setValue(person);
                                databaseReference.child("user").child(person.getId()).child("location").setValue(location);
                                databaseReference.child("user").child(person.getId()).child("blackList").child(person.getId()).setValue(reportUser);
                                databaseReference.child("user").child(person.getId()).child("extra").setValue(profile);
                                databaseReference.child("user").child(person.getId()).child("requests").child(person.getId()).setValue(request);
                                databaseReference.child("user").child(person.getId()).child("permited").child(person.getId()).setValue(request);

                                SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();

                                String status = "complete";

                                editor.putString("name", name);
                                editor.putString("image", url);
                                editor.putString("id", currentuser);
                                editor.putString("statusLogin", status);
                                editor.apply();

                                firstMatch(currentuser);

                                Intent intent = new Intent(LoginActivity.this,
                                        ComunityActivity.class);
                                startActivity(intent);


                            }
                        });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    //Toast.makeText(this, "Location is now on", Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    finish();
                    Toast.makeText(this, "Necessario ativar localização em tempo real.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RESULT_GALLERY:
                    filePath = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), filePath);
                        cvProfile.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Log.i("TAG", "Some exception " + e);
                    }
                    break;
            }
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                pd.show();
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
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


    public void intentLoginConclude() {
        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        String login = sharedPreferences.getString("statusLogin", "");

        if (login.equals("complete")) {
            Intent it = new Intent(LoginActivity.this, ComunityActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(it);
            finish();
        }
    }

    public void firstMatch(String currentuser) {

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("user");

        ModelMatch match = new ModelMatch();

        match.setName("");
        match.setImgUrl("");
        match.setId("");
        match.setStateMatch(false);

        databaseReference.child(currentuser).child("match").setValue(match);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (verifyConnection()) {
            if (requestCode == REQUEST_FINE_LOCATION) {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callGPSSettingIntent = new Intent(
                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(callGPSSettingIntent);

                } else {
                    Toast.makeText(getApplicationContext(), "Necessario aceitar", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            if (requestCode == PERMISSAO_REQUEST) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
            }
        } else {
            testConnect();
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
