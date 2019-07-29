package br.com.bangu_ao_vivo.bangu.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.bangu_ao_vivo.bangu.Adapter.LikedsAdapter;
import br.com.bangu_ao_vivo.bangu.Model.ModelLiked;
import br.com.bangu_ao_vivo.bangu.R;

public class LikesActivity extends AppCompatActivity implements View.OnClickListener {

    List<ModelLiked> list;
    LikedsAdapter adapter;

    RecyclerView recycler;
    Context context = this;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private LinearLayout layReturnReport, lay_visits;

    private FirebaseUser fuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        initializeIDS();

        recycler = findViewById(R.id.recyclerLikes);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new GridLayoutManager(context, 2));
        recycler.setItemAnimator(new DefaultItemAnimator());

        list = new ArrayList<>();
        adapter = new LikedsAdapter(list, context);

        initializeDataLoadLiked();

        layReturnReport.setOnClickListener(this);
        lay_visits.setOnClickListener(this);

    }

    public void initializeIDS () {
        layReturnReport = findViewById(R.id.layReturnReport);
        lay_visits = findViewById(R.id.lay_visits);
    }

    public void initializeDataLoadLiked() {

        FirebaseApp.initializeApp(context);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("user").child(fuser.getUid()).child("likes");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                list.clear();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {

                    ModelLiked liked = new ModelLiked();
                    String name = (String) objSnapshot.child("name").getValue();
                    String uid = (String) objSnapshot.child("uid").getValue();
                    String urlImageId = (String) objSnapshot.child("imgUrl").getValue();

                    liked.setName(name);
                    liked.setUid(uid);
                    liked.setImgUrl(urlImageId);

                    list.add(liked);
                }
                databaseReference.keepSynced(true);
                recycler.setAdapter(adapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    @Override
    public void onBackPressed() {
        LikesActivity.super.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layReturnReport:
                LikesActivity.super.finish();
                break;
            case R.id.lay_visits:
                Intent intent = new Intent(LikesActivity.this, ViewProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }
}
