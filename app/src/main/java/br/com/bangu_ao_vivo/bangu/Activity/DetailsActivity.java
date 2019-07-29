package br.com.bangu_ao_vivo.bangu.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.bangu_ao_vivo.bangu.Adapter.VisitGalleryAdapter;
import br.com.bangu_ao_vivo.bangu.Model.ModelAddFavorite;
import br.com.bangu_ao_vivo.bangu.Model.ModelLiked;
import br.com.bangu_ao_vivo.bangu.Model.ModelMatch;
import br.com.bangu_ao_vivo.bangu.Model.ModelNewItemGallery;
import br.com.bangu_ao_vivo.bangu.Model.ModelRequest;
import br.com.bangu_ao_vivo.bangu.Model.ModelVisits;
import br.com.bangu_ao_vivo.bangu.Model.ModelWhiteList;
import br.com.bangu_ao_vivo.bangu.Notifications.Client;
import br.com.bangu_ao_vivo.bangu.Notifications.Data;
import br.com.bangu_ao_vivo.bangu.Notifications.MyResponse;
import br.com.bangu_ao_vivo.bangu.Notifications.Sender;
import br.com.bangu_ao_vivo.bangu.Notifications.Token;
import br.com.bangu_ao_vivo.bangu.R;
import br.com.bangu_ao_vivo.bangu.Utils.ApiServiceNotifications;
import br.com.bangu_ao_vivo.bangu.Utils.LoadDoneFirebase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener, LoadDoneFirebase, ValueEventListener {

    private static boolean TRULED = false;
    private static String TAG_NAME = "";
    private static String TAG_IMG_URL = "";
    private int STRING_COUNTER = 0;
    private ImageView ivLikeUser, ivUserShare, ivSaveUserFavorite, cvMessage, ivVerificateId;
    private RelativeLayout relativeDesc;
    private static String KEY_USER = "";
    private static String NAME_USER = "";
    private final static String AGE_USER = "";
    private Context context = this;
    private TextView[] mDots;
    private ImageView ivHeartLikedUser;
    private LinearLayout mLayoutMain, layReportUser, layShareUser, layVerificateDesc;
    private TextView tvLabelVerificateItem, tvNullVerification;

    ViewPager viewPager;
    VisitGalleryAdapter adapter;
    LoadDoneFirebase loadDoneFirebase;

    private TextView tvNameUserDetails, tvCountryDetails,
            tvDescriptionDetails, tvDescTitle, tvCityDetails, tvInterest;

    private FirebaseUser fuser;
    Intent intent;

    private DatabaseReference reference;
    private DatabaseReference details;

    ApiServiceNotifications apiService;

    private boolean notify = false;
    private boolean verificationIniti = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        intent = getIntent();
        KEY_USER = intent.getStringExtra("user");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        loadDoneFirebase = this;
        details = FirebaseDatabase.getInstance().getReference("galeries-geral").child(KEY_USER); //'Movies' is our database name

        apiService = Client.getClient("https://fcm.googleapis.com/").create(ApiServiceNotifications.class);

        inicializeIDS();
        loadMyProfile();
        loadProfileUserDetails();

        ivLikeUser.setOnClickListener(this);
        ivUserShare.setOnClickListener(this);
        ivSaveUserFavorite.setOnClickListener(this);
        layReportUser.setOnClickListener(this);
        layShareUser.setOnClickListener(this);
        cvMessage.setOnClickListener(this);

        loadMovie();

        //verificacao de dado favoritado
        verificationFavoritate();

        //visita de perfil
        notficationUserVisit();

    }

    private void inicializeIDS() {
        ivLikeUser = findViewById(R.id.ivLikeUser);
        tvNameUserDetails = findViewById(R.id.tvNameUserDetails);
        tvCountryDetails = findViewById(R.id.tvCountryDetails);
        tvDescriptionDetails = findViewById(R.id.tvDescriptionDetails);
        relativeDesc = findViewById(R.id.relativeDesc);
        tvDescTitle = findViewById(R.id.tvDescTitle);
        viewPager = findViewById(R.id.recyclerHeader);
        mLayoutMain = findViewById(R.id.dotsLayout);
        ivUserShare = findViewById(R.id.ivShareUser);
        ivSaveUserFavorite = findViewById(R.id.ivSaveUser);
        tvCityDetails = findViewById(R.id.tvCityDetails);
        layReportUser = findViewById(R.id.layReportUser);
        layShareUser = findViewById(R.id.layShareUser);
        tvInterest = findViewById(R.id.tvInterest);
        cvMessage = findViewById(R.id.ivMessage);
        layVerificateDesc = findViewById(R.id.layVerificateDesc);
        ivVerificateId = findViewById(R.id.ivVerificateId);
        tvLabelVerificateItem = findViewById(R.id.tvLabelVerificateItem);
        tvNullVerification = findViewById(R.id.tvNullVerification);
        ivHeartLikedUser = findViewById(R.id.ivHeartLikedUser);
    }

    private void reportUser() {
        Intent it = new Intent(DetailsActivity.this, ReportActivity.class);
        it.putExtra("userReport", KEY_USER);
        startActivity(it);
    }

    private void loadMovie() {
        /*
        This is a different approach but also correct.
        movies.addListenerForSingleValueEvent(new ValueEventListener() {

            List<Movie> movieList = new ArrayList<>();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot movieSnapShot:dataSnapshot.getChildren())
                movieList.add(movieSnapShot.getValue(Movie.class));

                iFirebaseLoadDone.onFirebaseLoadSuccess(movieList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                iFirebaseLoadDone.onFirebaseLoadFailed(databaseError.getMessage());
            }
        });*/
        details.addValueEventListener(this);
    }

    public void addDotsIndicator(int position) {

        mDots = new TextView[STRING_COUNTER];
        mLayoutMain.removeAllViews();

        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorGrayBg));

            mLayoutMain.addView(mDots[i]);

        }

        if (mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.colorMain));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void notficationUserVisit() {

        verificationIniti = false;

        DatabaseReference refMy = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference childMy = refMy.child("user").child(KEY_USER);

        childMy.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                final DatabaseReference child = ref.child("user").child(KEY_USER);

                ModelVisits visits = new ModelVisits();
                visits.setUid(fuser.getUid());
                visits.setImageUrl(TAG_IMG_URL);
                visits.setName(TAG_NAME);

                child.child("visits").child(visits.getUid()).setValue(visits).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String msg = "visitou seu perfil";
                        sendNotificationVisit(KEY_USER, TAG_NAME, msg);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void loadProfileUserDetails() {

        intent = getIntent();
        KEY_USER = intent.getStringExtra("user");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference child = ref.child("user").child(KEY_USER);

        child.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                NAME_USER = (String) dataSnapshot.child("name").getValue();
                String countryUser = (String) dataSnapshot.child("location").child("state").getValue();
                long age = (long) dataSnapshot.child("age").getValue();
                String cityUser = (String) dataSnapshot.child("location").child("city").getValue();
                String descriptionUser = (String) dataSnapshot.child("extra").child("description").getValue();
                String interest = (String) dataSnapshot.child("extra").child("interest").getValue();
                String verifiedIn = (String) dataSnapshot.child("extra").child("typeVerified").getValue();

                veriFyLiked();

                tvNameUserDetails.setText(NAME_USER + ", " + age);
                tvCountryDetails.setText(countryUser);
                tvDescriptionDetails.setText(descriptionUser);
                tvCityDetails.setText(cityUser);
                tvInterest.setText(interest);
                tvDescTitle.setText("descrição");

                if (!verifiedIn.equals("")) {
                    layVerificateDesc.setVisibility(View.VISIBLE);
                    if (verifiedIn.equals("traditional")) {
                       long i = dataSnapshot.child("extra").child("verificate").getValue(long.class);
                        if (i == 2) {
                            ivVerificateId.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.chek_ic));
                            tvLabelVerificateItem.setText("foto");
                            tvNullVerification.setVisibility(View.GONE);
                        } else {
                            layVerificateDesc.setVisibility(View.GONE);
                            tvNullVerification.setVisibility(View.VISIBLE);
                        }
                    } else if (verifiedIn.equals("google")) {
                        ivVerificateId.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.google_verified_tw));
                        tvLabelVerificateItem.setText("google");
                    } else if (verifiedIn.equals("facebook")) {
                        ivVerificateId.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.facebook_verified));
                        tvLabelVerificateItem.setText("facebook");
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        child.keepSynced(true);
    }

    private void veriFyLiked() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference child = ref.child("user").child(fuser.getUid()).child("myLikeds");
        child.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(KEY_USER).exists()) {
                    ivLikeUser.setVisibility(View.GONE);
                    ivHeartLikedUser.setVisibility(View.VISIBLE);
                } else {
                    ivLikeUser.setVisibility(View.VISIBLE);
                    ivHeartLikedUser.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void whiteList (String uid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference data = ref.child("user").child(fuser.getUid()).child("whiteList");
        ModelWhiteList whiteList = new ModelWhiteList();
        whiteList.setUid(uid);
        data.child(whiteList.getUid()).setValue(whiteList).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });

        DatabaseReference reftw = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference datatw = reftw.child("user").child(uid).child("requests");

        ModelRequest request = new ModelRequest();
        request.setUid(fuser.getUid());
        request.setState(false);

        datatw.child(fuser.getUid()).setValue(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivLikeUser:
                notify = true;

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                final DatabaseReference data = ref.child("user").child(fuser.getUid());
                data.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("likes").child(KEY_USER).exists()) {
                            String msg = " deu match com você";
                            sendNotificationMatch(KEY_USER, TAG_NAME, msg);
                            TRULED = true;
                            notify = false;

                            matchedDatabase();

                            Intent it = new Intent(DetailsActivity.this, MatchActivity.class);
                            it.putExtra("user", KEY_USER);
                            it.putExtra("myUser", fuser.getUid());
                            it.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(it);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                if (notify) {
                    String msg = "Quero dar um oi";
                    sendNotification(KEY_USER, TAG_NAME, msg);
                }
                notify = false;
                break;

            case R.id.ivShareUser:
                shareTextUrl();
                break;

            case R.id.ivSaveUser:
                ivSaveUserFavorite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.star_favorite));
                saveFavorite();
                break;

            case R.id.layReportUser:
                reportUser();
                break;

            case R.id.layShareUser:
                shareTextUrl();
                break;

            case R.id.ivMessage:
                atenptionUser();
                break;
        }
    }

    private void atenptionUser() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference data = ref.child("user").child(KEY_USER);

        ModelMatch match = new ModelMatch();

        match.setName(TAG_NAME);
        match.setId(fuser.getUid());
        match.setImgUrl(TAG_IMG_URL);
        match.setStateMatch(true);

        data.child("match").setValue(match);

        String msg = "Alguem";
        sendNotificationAtemp(KEY_USER, TAG_NAME, msg);
    }

    private void matchedDatabase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference data = ref.child("user").child(KEY_USER);

        ModelMatch match = new ModelMatch();

        match.setName(TAG_NAME);
        match.setId(fuser.getUid());
        match.setImgUrl(TAG_IMG_URL);
        match.setStateMatch(false);
        match.setMatched(true);

        data.child("match").setValue(match);

        DatabaseReference reftw = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference datatw = reftw.child("user").child(KEY_USER).child("requests");

        ModelRequest request = new ModelRequest();
        request.setUid(fuser.getUid());
        request.setState(false);

        datatw.child(fuser.getUid()).setValue(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                whiteList(KEY_USER);
            }
        });
    }

    private void sendNotificationVisit(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.drawable.ic_notf, username + " " + message, "Nova visita",
                            KEY_USER);

                    Sender sender = new Sender(data, token.getToken());

                    if (verificationIniti) {
                        liked();
                    }
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            //Toast.makeText(DetailsActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void verificationFavoritate() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference data = ref.child("user").child(fuser.getUid()).child("favorites").child(KEY_USER);
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ivSaveUserFavorite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.star_favorite));
                } else {
                    ivSaveUserFavorite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.star_save_ic));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void saveFavorite() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference data = ref.child("user").child(fuser.getUid());
        ModelAddFavorite favorite = new ModelAddFavorite();
        favorite.setUid(KEY_USER);
        data.child("favorites").child(favorite.getUid()).setValue(favorite).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                View view = findViewById(R.id.layMasterDetail);
                Snackbar snackbar = Snackbar.make(view, "Adicionado a os favoritos.", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                View view = findViewById(R.id.layMasterDetail);
                Snackbar snackbar = Snackbar.make(view, "Algo deu errado :(.", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });

    }

    private void loadMyProfile() {
        DatabaseReference refTwo = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference dataTwo = refTwo.child("user").child(fuser.getUid());
        dataTwo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                TAG_NAME = dataSnapshot.child("name").getValue(String.class);
                TAG_IMG_URL = dataSnapshot.child("urlImage").getValue(String.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void liked() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference data = ref.child("user").child(fuser.getUid()).child("myLikeds");
        ModelLiked liked = new ModelLiked();
        liked.setUid(KEY_USER);
        data.child(liked.getUid()).setValue(liked).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                ivLikeUser.setVisibility(View.GONE);
            }
        });

        ModelLiked modelLiked = new ModelLiked();
        modelLiked.setUid(fuser.getUid());
        modelLiked.setImgUrl(TAG_IMG_URL);
        modelLiked.setName(TAG_NAME);

        DatabaseReference refTwo = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference dataTwo = refTwo.child("user").child(KEY_USER).child("likes");

        dataTwo.child(fuser.getUid()).setValue(modelLiked);

        DatabaseReference reftw = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference datatw = reftw.child("user").child(KEY_USER).child("requests");

        ModelRequest request = new ModelRequest();
        request.setUid(fuser.getUid());
        request.setState(false);

    }

    private void sendNotification(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.drawable.ic_notf, message, "Nova curtida",
                            KEY_USER);

                    Sender sender = new Sender(data, token.getToken());

                    liked();

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            //Toast.makeText(DetailsActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotificationAtemp(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.drawable.ic_notf, message, "Chamando sua atenção",
                            KEY_USER);

                    Sender sender = new Sender(data, token.getToken());

                    liked();

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            //Toast.makeText(DetailsActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendNotificationMatch(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.drawable.ic_notf, username + message, "Novo match :)",
                            KEY_USER);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            //Toast.makeText(DetailsActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        share.putExtra(Intent.EXTRA_SUBJECT, "Entre no bangu, hoje junto com Ricardo.");
        share.putExtra(Intent.EXTRA_TEXT, "<source url>");

        startActivity(Intent.createChooser(share, "Share text to..."));
    }

    @Override
    public void onFirebaseLoadSuccess(List<ModelNewItemGallery> galleryList) {
        adapter = new VisitGalleryAdapter(context, galleryList);
        viewPager.setAdapter(adapter);
        addDotsIndicator(0);
        viewPager.addOnPageChangeListener(viewListener);
    }

    @Override
    public void onFirebaseLoadFailed(String message) {

    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        List<ModelNewItemGallery> galleryList = new ArrayList<>();
        int childsCount = 0;
        for (DataSnapshot movieSnapShot : dataSnapshot.getChildren()) {
            galleryList.add(movieSnapShot.getValue(ModelNewItemGallery.class));
            childsCount = (int) dataSnapshot.getChildrenCount();
            loadDoneFirebase.onFirebaseLoadSuccess(galleryList);
        }
        STRING_COUNTER = childsCount;
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        loadDoneFirebase.onFirebaseLoadFailed(databaseError.getMessage());

    }

    @Override
    protected void onDestroy() {
        details.removeEventListener(this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        details.addValueEventListener(this);
        super.onResume();
    }

    @Override
    protected void onStop() {
        details.removeEventListener(this);
        super.onStop();
    }
}
