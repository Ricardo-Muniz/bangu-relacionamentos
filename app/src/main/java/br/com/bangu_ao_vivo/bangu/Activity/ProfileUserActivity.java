package br.com.bangu_ao_vivo.bangu.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

import br.com.bangu_ao_vivo.bangu.ComunityActivity;
import br.com.bangu_ao_vivo.bangu.Controler.ControlerTips;
import br.com.bangu_ao_vivo.bangu.R;
import de.hdodenhof.circleimageview.CircleImageView;
import it.sephiroth.android.library.tooltip.Tooltip;

public class ProfileUserActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int RESULT_GALLERY = 1;
    private ImageView ivReturn, ivVerificate;
    private CircleImageView ivProfileUser;
    private LinearLayout layEditProf;
    private View include;
    private LinearLayout layGallery;
    private TextView tvProfilePolitica;

    private CardView cvOne, cvTwo, cvThree;

    private TextView tvNameAndAge, tvDescription, tvCardTextOne, tvCardTextTwo, tvCardTextThree, tvProfileSair, tvProfileNotfy;

    private Uri filePath;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReferenceFromUrl("gs://storagebangu.appspot.com/");

    private Context context = this;
    private ProgressDialog progressDialogchange;

    private static final int CONST_INTENT_TIPS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        //inicializa ids
        inicializeIDS();

        //inicializa firebaseApp
        inicializeFirebase();

        //constroi os dados de usuario
        inicializeProfileConstructor();

        ControlerTips tips = new ControlerTips();
        tips.constructorTextCards(tvCardTextOne, tvCardTextTwo, tvCardTextThree);

        //toast tooltip image user profile
        tooltipImage();

        //intencao de click
        ivReturn.setOnClickListener(this);
        layEditProf.setOnClickListener(this);
        include.setOnClickListener(this);
        ivProfileUser.setOnClickListener(this);
        cvOne.setOnClickListener(this);
        cvTwo.setOnClickListener(this);
        cvThree.setOnClickListener(this);
        tvProfileSair.setOnClickListener(this);
        tvProfileNotfy.setOnClickListener(this);
        layGallery.setOnClickListener(this);
        tvProfilePolitica.setOnClickListener(this);

    }

    private void inicializeFirebase() {
        FirebaseApp.initializeApp(context);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void inicializeIDS() {
        ivReturn = findViewById(R.id.ivReturnProfile);
        layEditProf = findViewById(R.id.layEditProfile);
        ivProfileUser = findViewById(R.id.ivUserProfileImage);
        tvNameAndAge = findViewById(R.id.tvNameUserProfile);
        tvDescription = findViewById(R.id.tvDescProfile);
        include = findViewById(R.id.include);
        ivVerificate = findViewById(R.id.ivVerify);
        cvOne = findViewById(R.id.cvOne);
        cvTwo = findViewById(R.id.cvTwo);
        cvThree = findViewById(R.id.cvThree);
        tvCardTextOne = findViewById(R.id.tvCardTextOne);
        tvCardTextTwo = findViewById(R.id.tvCardTextTwo);
        tvCardTextThree = findViewById(R.id.tvCardTextThree);
        tvProfileSair = findViewById(R.id.tvProfileSair);
        tvProfileNotfy = findViewById(R.id.tvProfileNotfy);
        layGallery = findViewById(R.id.layGallery);
        tvProfilePolitica = findViewById(R.id.tvProfilePolitica);
    }

    private void inicializeProfileConstructor() {

        SharedPreferences preferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        final String uid = preferences.getString("id", "");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference child = ref.child("user").child(uid);
        child.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer mAge, mVerificate;
                String mName = (String) dataSnapshot.child("name").getValue();
                mAge = dataSnapshot.child("age").getValue(Integer.class);
                String mDescription = (String) dataSnapshot.child("extra").child("description").getValue();
                String mUrlImageProfile = (String) dataSnapshot.child("urlImage").getValue();
                mVerificate = dataSnapshot.child("extra").child("verificate").getValue(Integer.class);
                tvNameAndAge.setText(mName + ", " + mAge);
                if (mDescription == null) {
                    tvDescription.setText("Aqui você vai ver sua descrição, então tenta fazer a melhor possivel para chamar atenção de um possivel matche. rs");
                } else {
                    tvDescription.setText(mDescription + ".");
                }
                Glide.with(context)
                        .load(mUrlImageProfile)
                        .asBitmap()
                        .centerCrop()
                        .into(ivProfileUser);

                if (mVerificate == null) {
                    ivVerificate.setVisibility(View.GONE);
                } else if (mVerificate == 2) {
                    ivVerificate.setVisibility(View.VISIBLE);
                } else if (mVerificate == 1) {
                    ivVerificate.setVisibility(View.GONE);
                }
                ref.keepSynced(true);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileUserActivity.this, ComunityActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        ProfileUserActivity.this.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivReturnProfile:
                Intent intent = new Intent(ProfileUserActivity.this, ComunityActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                ProfileUserActivity.super.finish();
                break;
            case R.id.layEditProfile:
                Intent intentEdit = new Intent(ProfileUserActivity.this, EditProfileActivity.class);
                startActivity(intentEdit);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                ProfileUserActivity.super.finish();
                break;
            case R.id.include:
                Intent intentVerify = new Intent(ProfileUserActivity.this, VerifyProfileActivity.class);
                startActivity(intentVerify);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.ivUserProfileImage:
                Intent intentChangeImage = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentChangeImage, RESULT_GALLERY);
                break;

            case R.id.cvOne:
                Intent itentTips = new Intent(ProfileUserActivity.this, TipsActivity.class);
                itentTips.putExtra("position", 1);
                startActivity(itentTips);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                ProfileUserActivity.super.finish();
                break;

            case R.id.cvTwo:
                Intent itentTipsTw = new Intent(ProfileUserActivity.this, TipsActivity.class);
                itentTipsTw.putExtra("position", 2);
                startActivity(itentTipsTw);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                ProfileUserActivity.super.finish();
                break;

            case R.id.cvThree:
                Intent itentTipsTr = new Intent(ProfileUserActivity.this, TipsActivity.class);
                itentTipsTr.putExtra("position", 3);
                startActivity(itentTipsTr);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                ProfileUserActivity.super.finish();
                break;

            case R.id.tvProfileSair:
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();

                SharedPreferences preferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                String status = "NoComplete";
                String uidClear = "NoComplete";
                editor.putString("statusLogin", status);
                editor.putString("id", uidClear);
                editor.apply();

                Intent itLogout = new Intent(ProfileUserActivity.this, NewSessionLoginActivity.class);
                startActivity(itLogout);
                ProfileUserActivity.super.finish();

                break;


            case R.id.tvProfileNotfy:
                Intent settingsIntent = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                }
                startActivity(settingsIntent);
                break;

            case R.id.layGallery:
                Intent itGallery = new Intent(ProfileUserActivity.this, GalleryActivity.class);
                startActivity(itGallery);
                break;

            case R.id.tvProfilePolitica:
                Intent itpolicy = new Intent(ProfileUserActivity.this, PlicyActivity.class);
                startActivity(itpolicy);
                break;

        }
    }

    private void changeImageProfile() {
        Random random = new Random();
        final int i1 = random.nextInt(10000 - 300) + 65;
        SharedPreferences preferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        final String uid = preferences.getString("id", "");

        StorageReference child = storageReference.child(i1 + "imageChanged");

        UploadTask uploadTask = child.putFile(filePath);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child(i1 + "imageChanged").getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                final DatabaseReference childExtern = ref.child("user").child(uid).child("urlImage");
                                childExtern.setValue(url).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Tooltip.make(context,
                                                new Tooltip.Builder(101).withStyleId(R.style.ToolTipLayoutCustomStyle)
                                                        .anchor(ivProfileUser, Tooltip.Gravity.BOTTOM)
                                                        .closePolicy(new Tooltip.ClosePolicy()
                                                                .insidePolicy(true, false)
                                                                .outsidePolicy(true, false), 4000)
                                                        .activateDelay(900)
                                                        .showDelay(400)
                                                        .text("seu atual perfil")
                                                        .maxWidth(600)
                                                        .withArrow(true)
                                                        .withOverlay(true).build()
                                        ).show();

                                        Glide.with(context)
                                                .load(url)
                                                .asBitmap()
                                                .centerCrop()
                                                .into(ivProfileUser);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        View view = findViewById(R.id.layProfileUser);
                                        Snackbar snackbar = Snackbar.make(view, "Erro ao conectar.", Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                    }
                                });
                            }
                        });
            }
        });

        ivProfileUser.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.image_load_ic));

        //adicionando apos o download
    }

    @Override
    protected void onDestroy() {
        firstVisitProfileDevice();
        super.onDestroy();
    }

    public void firstVisitProfileDevice() {
        SharedPreferences preferences = getSharedPreferences("row", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int visit = 2;
        editor.putInt("visit", visit);
        editor.apply();
    }

    public void tooltipImage() {

        SharedPreferences preferences = getSharedPreferences("row", Context.MODE_PRIVATE);
        int numberVisit = preferences.getInt("visit", 0);

        if (numberVisit == 0) {
           Tooltip.make(this,
                    new Tooltip.Builder(101).withStyleId(R.style.ToolTipLayoutCustomStyle)
                            .anchor(ivProfileUser, Tooltip.Gravity.BOTTOM)
                            .closePolicy(new Tooltip.ClosePolicy()
                                    .insidePolicy(true, false)
                                    .outsidePolicy(true, false), 4000)
                            .activateDelay(900)
                            .showDelay(400)
                            .text("seu atual perfil")
                            .maxWidth(600)
                            .withArrow(true)
                            .withOverlay(true).build()
            ).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RESULT_GALLERY:
                    filePath = data.getData();
                    changeImageProfile();
                    break;
            }
        }

    }

}
