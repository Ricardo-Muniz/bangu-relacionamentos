package br.com.bangu_ao_vivo.bangu.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import br.com.bangu_ao_vivo.bangu.ComunityActivity;
import br.com.bangu_ao_vivo.bangu.R;
import br.com.bangu_ao_vivo.bangu.Utils.Person;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChangeProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvHow, tvMudar, tvFinalizar;
    private Context context = this;
    private CircleImageView cvProfile;

    private final int RESULT_GALLERY = 1;
    private final int PERMISSAO_REQUEST = 2;

    Uri filePath;
    private ProgressDialog pd;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReferenceFromUrl("gs://storagebangu.appspot.com/");

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);


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

        iniciafirebase();

        pd = new ProgressDialog(this);
        pd.setMessage("enviando...");


        //chamada de ids
        tvHow = findViewById(R.id.tvHow);
        tvMudar = findViewById(R.id.tvMudar);
        tvFinalizar = findViewById(R.id.tvFinalizar);
        cvProfile = findViewById(R.id.cvProfile);

        //dando o onclick aos atributos
        cvProfile.setOnClickListener(this);
        tvFinalizar.setOnClickListener(this);

        Spannable spannable = new SpannableString("perfil > foto > galeria");
        spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorMain)),
                9, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvHow.setText(spannable);

        //extras da intent anterior, login
     /*   String dataNome = getIntent().getExtras().getString("nomeUser", "");
        String dataEmail = getIntent().getExtras().getString("emailUser", "");
        String dataSenha = getIntent().getExtras().getString("senhaUser", "");
        String dataLocal = getIntent().getExtras().getString("localUser", ""); */
        tvMudar.setText("Ricardo" + ", você precisa ficar mais apresentavel, use um perfil.");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cvProfile:
                Intent intentChangeImage = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentChangeImage, RESULT_GALLERY);

                break;

            case R.id.tvFinalizar:
                if (filePath != null) {
                    pd.show();

                    Random random = new Random();
                    final int i1 = random.nextInt(10000 - 300) + 65;

                    StorageReference child = storageReference.child(i1 + "image");

                    UploadTask uploadTask = child.putFile(filePath);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            // Toast.makeText(context, "sucesso", Toast.LENGTH_LONG).show();

                            storageReference.child(i1 + "image").getDownloadUrl().addOnSuccessListener(
                                    new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            String url = uri.toString();
                                            String dataNome = getIntent().getExtras().getString("nomeUser", "");
                                            String dataEmail = getIntent().getExtras().getString("emailUser", "");
                                            String dataSenha = getIntent().getExtras().getString("senhaUser", "");
                                            String dataLocal = getIntent().getExtras().getString("localUser", "");
                                            String dataSex = getIntent().getExtras().getString("sexUser", "");

                                            Person person = new Person();
                                            person.setName(dataNome);
                                            person.setSenha(dataSenha);
                                            person.setEmail(dataEmail);
                                            person.setLocalidade(dataLocal);
                                            person.setSex(dataSex);
                                            person.setUrlImage(url);
                                            String id = person.setId(UUID.randomUUID().toString());
                                            person.setId(id);

                                            databaseReference.child("user").child(person.getId()).setValue(person);


                                            SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = preferences.edit();

                                            editor.putString("name", dataNome);
                                            editor.putString("image", url);
                                            editor.putString("id", id);
                                            editor.apply();

                                            Intent intent = new Intent(ChangeProfileActivity.this,
                                                    ComunityActivity.class);
                                            startActivity(intent);

                                        }
                                    });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(context, "falha", Toast.LENGTH_LONG).show();
                        }
                    });

                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
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

    private void iniciafirebase() {

        FirebaseApp.initializeApp(context);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

    }

    public void saveUser(String url) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSAO_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {

            }
            return;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
