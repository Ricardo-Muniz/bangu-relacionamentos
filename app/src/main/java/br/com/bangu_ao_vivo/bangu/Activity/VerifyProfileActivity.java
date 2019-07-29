package br.com.bangu_ao_vivo.bangu.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mindorks.paracamera.Camera;

import java.io.ByteArrayOutputStream;
import java.util.Random;

import br.com.bangu_ao_vivo.bangu.Model.ModelVerifyUser;
import br.com.bangu_ao_vivo.bangu.R;

public class VerifyProfileActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int CAMERA_PIC_REQUEST = 1;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 110;
    private CardView cvVerifyProfile;
    private ImageView ivProfileVery;
    private Uri filePath;
    private Context context = this;
    private TextView tvVerifyDesc, tvTitleVerify, tvLabelVerifyButton;

    private ProgressDialog pd;
    private FirebaseUser fuser;

    Camera camera;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReferenceFromUrl("gs://storagebangu.appspot.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_profile);

        initializeIDS();

        SharedPreferences prefs = getSharedPreferences("login", Context.MODE_PRIVATE);
        String status = prefs.getString("verifiedStatus", "");
        if (status.equals("send")) {
            tvTitleVerify.setText("Prontinho");
            tvVerifyDesc.setText("Aguarda ae sua confirmação ou a atualização automatica do seu perfil com seu selo. fica de boa que as vezes leva tempo.");
            tvLabelVerifyButton.setText("tentar novamente");
        }

        cvVerifyProfile.setOnClickListener(this);

        pd = new ProgressDialog(this);
        pd.setMessage("enviando dados...");
        pd.setCancelable(false);

        camera = new Camera.Builder()
                .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                .setTakePhotoRequestCode(1)
                .setDirectory("pics")
                .setName("ali_" + System.currentTimeMillis())
                .setImageFormat(Camera.IMAGE_JPEG)
                .setCompression(75)
                .setImageHeight(1000)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                .build(this);

        fuser = FirebaseAuth.getInstance().getCurrentUser();


    }

    public void initializeIDS() {
        cvVerifyProfile = findViewById(R.id.cvVerifyProfile);
        ivProfileVery = findViewById(R.id.ivProfileVerify);
        tvVerifyDesc = findViewById(R.id.tvVerifyDesc);
        tvTitleVerify = findViewById(R.id.tvTitleVerify);
        tvLabelVerifyButton = findViewById(R.id.tvLabelVerifyButton);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(VerifyProfileActivity.this, ProfileUserActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        VerifyProfileActivity.super.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cvVerifyProfile:
                verifyUser();
                break;
        }

    }

    private boolean verifyConexectivity() {
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

    private void verifyUser() {
        ActivityCompat.requestPermissions(VerifyProfileActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_EXTERNAL_STORAGE);
        try {
            camera.takePicture();
        }catch (Exception e){
            e.printStackTrace();
        }

        if (!verifyConexectivity()) {
            pd.dismiss();
            View view = findViewById(R.id.layVerify);
            Snackbar snackbar = Snackbar.make(view, "Verifique sua conexão.", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Camera.REQUEST_TAKE_PHOTO){
            Bitmap bitmap = camera.getCameraBitmap();
            if(bitmap != null) {
                ivProfileVery.setImageBitmap(bitmap);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
                filePath = Uri.parse(path);
                upload();
            }else{
                Toast.makeText(this.getApplicationContext(),"Picture not taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void upload() {
        pd.show();
        Random random = new Random();
        final int i1 = random.nextInt(10000 - 300) + 65;

        StorageReference child = storageReference.child(i1 + "image");

        UploadTask uploadTask = child.putFile(filePath);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                tvTitleVerify.setText("Prontinho, ja tentou");
                tvVerifyDesc.setText("Aguarda ae sua confirmação ou a atualização automatica do seu perfil com seu selo. fica de boa que as vezes leva tempo.");
                tvLabelVerifyButton.setText("tentar novamente");

                SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("verifiedStatus", "send");
                editor.apply();

                storageReference.child(i1 + "image").getDownloadUrl().addOnSuccessListener(
                        new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String url = uri.toString();
                                String uid = fuser.getUid();

                                ModelVerifyUser user = new ModelVerifyUser();
                                user.setUid(uid);
                                user.setUrlImage(url);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                final DatabaseReference childIdenti = ref.child("verifyUser");

                                childIdenti.child(user.getUid()).setValue(user);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera.deleteImage();
    }
}
