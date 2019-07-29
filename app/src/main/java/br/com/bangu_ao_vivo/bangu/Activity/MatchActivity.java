package br.com.bangu_ao_vivo.bangu.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
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

public class MatchActivity extends AppCompatActivity {

    private static String KEY_MY_UID = "";
    private static String KEY_USER = "";
    private CircleImageView ivPersonCenter, ivPersonBottom;
    private TextView tvNames;
    private Context context = this;
    private CardView cvMessage;

    FirebaseUser fuser;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        intent = getIntent();
        KEY_USER = intent.getExtras().getString("user");
        KEY_MY_UID = intent.getExtras().getString("myUser");

        initializeIDS();

        initializeUSER();

        cvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MatchActivity.this, ChatProfileActivity.class);
                it.putExtra("user", KEY_USER);
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
        match.setMatched(false);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final Query child = ref.child("user").child(fuser.getUid()).child("match");
        final Task<Void> voidTask = ((DatabaseReference) child).setValue(match);
        voidTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                MatchActivity.this.finish();
            }
        });

    }

    private void initializeUSER() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference data = ref.child("user");
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String urlOther = dataSnapshot.child(KEY_USER).child("urlImage").getValue(String.class);
                String nameOther = dataSnapshot.child(KEY_USER).child("name").getValue(String.class);
                String urlMy = dataSnapshot.child(KEY_MY_UID).child("urlImage").getValue(String.class);
                Glide.with(context)
                        .load(urlOther)
                        .asBitmap()
                        .into(ivPersonCenter);
                Glide.with(context)
                        .load(urlMy)
                        .asBitmap()
                        .into(ivPersonBottom);
                tvNames.setText("Você e " + nameOther + " deram Match");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeIDS() {
        ivPersonCenter = findViewById(R.id.ivProfileCenter);
        ivPersonBottom = findViewById(R.id.ivProfileOne);
        tvNames = findViewById(R.id.tvNamesMatch);
        cvMessage = findViewById(R.id.cvMessageMatch);
    }
}
