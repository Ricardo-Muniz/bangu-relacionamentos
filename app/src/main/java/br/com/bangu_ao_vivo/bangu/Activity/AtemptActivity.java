package br.com.bangu_ao_vivo.bangu.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import br.com.bangu_ao_vivo.bangu.Model.ModelMatch;
import br.com.bangu_ao_vivo.bangu.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class AtemptActivity extends AppCompatActivity {

    private static String TAG_UID = "";
    private CircleImageView ivImaePerson;
    private TextView tvNamePerson;
    private CardView cvNewMessage;
    private FirebaseUser fuser;
    private Context context = this;
    private LinearLayout layExitImageAt;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atempt);

        initializeIDS();
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        intent = getIntent();
        TAG_UID = intent.getExtras().getString("user");

        initializeUSER();

        layExitImageAt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              finalizeACT();
            }
        });

        cvNewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(AtemptActivity.this, ChatProfileActivity.class);
                it.putExtra("user", TAG_UID);
                startActivity(it);
                finalizeACT();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finalizeACT();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finalizeACT();
    }

    private void finalizeACT() {
        ModelMatch match = new ModelMatch();
        match.setName("");
        match.setId("");
        match.setImgUrl("");
        match.setStateMatch(false);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final Query child = ref.child("user").child(fuser.getUid()).child("match");
        final Task<Void> voidTask = ((DatabaseReference) child).setValue(match);
        voidTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                AtemptActivity.this.finish();
            }
        });

    }

    private void initializeUSER() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final Query child = ref.child("user").child(fuser.getUid()).child("match");
        child.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue(String.class);
                String url = dataSnapshot.child("imgUrl").getValue(String.class);
                String id = dataSnapshot.child("id").getValue(String.class);

                Glide.with(context)
                        .load(url)
                        .asBitmap()
                        .into(ivImaePerson);

                tvNamePerson.setText("Oi, sou " + name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeIDS() {
        ivImaePerson = findViewById(R.id.ivImageAt);
        tvNamePerson = findViewById(R.id.tvNameAt);
        cvNewMessage = findViewById(R.id.cvMessageAt);
        layExitImageAt = findViewById(R.id.layExitImageAt);
    }


}
