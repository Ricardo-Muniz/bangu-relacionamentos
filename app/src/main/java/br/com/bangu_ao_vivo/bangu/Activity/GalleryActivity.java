package br.com.bangu_ao_vivo.bangu.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import br.com.bangu_ao_vivo.bangu.Adapter.GalleryUploadsAdapter;
import br.com.bangu_ao_vivo.bangu.Model.ModelNewItemGallery;
import br.com.bangu_ao_vivo.bangu.R;
import br.com.bangu_ao_vivo.bangu.Utils.RandomString;

public class GalleryActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView cvNewImageGallery;

    private Uri filePath;
    private final int RESULT_GALLERY = 1;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReferenceFromUrl("gs://storagebangu.appspot.com/");
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private FirebaseUser fuser;
    List<ModelNewItemGallery> list;
    GalleryUploadsAdapter adapter;

    RecyclerView recycler;

    private Context context = this;

    private ProgressDialog pd;

    Bundle bund;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        bund = savedInstanceState;


        //inicialize ids
        inicializeIDS();

        pd = new ProgressDialog(this);
        pd.setMessage("salvando...");
        pd.setCancelable(false);

        //inicializacao firebase
        iniciafirebase();

        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new GridLayoutManager(context, 3));
        recycler.setItemAnimator(new DefaultItemAnimator());

        list = new ArrayList<>();
        adapter = new GalleryUploadsAdapter(list, context);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        galleryUploads();

        //inicialize click buttons..layouts
        cvNewImageGallery.setOnClickListener(this);

    }

    private void inicializeIDS() {
        cvNewImageGallery = findViewById(R.id.cvNewImageGallery);
        recycler = findViewById(R.id.recycler_display_gallery);
    }

    private void galleryUploads() {

        String uid = fuser.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference data = ref.child("galeries-geral").child(uid);
        data.keepSynced(true);
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                list.clear();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {

                    ModelNewItemGallery gallery = new ModelNewItemGallery();
                    String metaUID = (String) objSnapshot.child("uid").getValue();
                    String metaLink = (String) objSnapshot.child("imageUrl").getValue();
                    Boolean status = (Boolean) objSnapshot.child("status").getValue();

                    gallery.setUid(metaUID);
                    gallery.setImageUrl(metaLink);
                    gallery.setStatus(status);

                    list.add(gallery);
                }
                recycler.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cvNewImageGallery:
                Intent intentChangeImage = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentChangeImage, RESULT_GALLERY);
                break;
        }
    }

    private void iniciafirebase() {

        FirebaseApp.initializeApp(context);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

    }

    private void newImageGallery() {

        if (filePath != null) {
            pd.show();

            String easy = RandomString.digits + "ACEFGHJKLMNPQRUVWXYabcdefhijkprstuvwx";
            RandomString tickets = new RandomString(23, new SecureRandom(), easy);
            String ranndum = tickets.nextString();

            StorageReference child = storageReference.child(ranndum + "image-galery");

            UploadTask uploadTask = child.putFile(filePath);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    storageReference.child(ranndum + "image-galery").getDownloadUrl().addOnSuccessListener(
                            new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    pd.dismiss();

                                    String easy = RandomString.digits + "ACEFGHJKLMNPQRUVWXYabcdefhijkprstuvwx";
                                    RandomString tks = new RandomString(23, new SecureRandom(), easy);
                                    String uid = tks.nextString();

                                    String url = uri.toString();

                                    ModelNewItemGallery newImage = new ModelNewItemGallery();
                                    newImage.setUid(uid);
                                    newImage.setImageUrl(url);
                                    newImage.setStatus(false);
                                    newImage.setUserID(fuser.getUid());

                                    databaseReference.child("galeries-geral").child(fuser.getUid()).child(uid).setValue(newImage);

                                    reload();

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

    }

    private void reload () {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case RESULT_GALLERY:
                    filePath = data.getData();
                    newImageGallery();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), filePath);
                    } catch (IOException e) {
                        Log.i("TAG", "Some exception " + e);
                    }
                    break;
            }
    }
}
