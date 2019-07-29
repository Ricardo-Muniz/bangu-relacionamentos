package br.com.bangu_ao_vivo.bangu.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.bangu_ao_vivo.bangu.Adapter.VisitsAdapter;
import br.com.bangu_ao_vivo.bangu.Model.ModelVisits;
import br.com.bangu_ao_vivo.bangu.R;

public class ViewProfileActivity extends AppCompatActivity implements View.OnClickListener {

    List<ModelVisits> list;
    Context context = this;

    private RecyclerView recyclerView;
    private VisitsAdapter adapter;
    private FirebaseUser fuser;
    private LinearLayout layReturnVisits;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = findViewById(R.id.recycler_view_profile);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        list = new ArrayList<>();
        adapter = new VisitsAdapter(context, list);

        initializeDataVisits();

        initializeIDS();

        layReturnVisits.setOnClickListener(this);

    }

    private void initializeIDS() {
        layReturnVisits = findViewById(R.id.layReturnVisits);
    }

    private void initializeDataVisits() {
        FirebaseApp.initializeApp(context);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("user").child(fuser.getUid()).child("visits");
        Query visits = databaseReference.limitToLast(5);

        visits.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot dsnap : dataSnapshot.getChildren()) {
                    String name = dsnap.child("name").getValue(String.class);
                    String uid = dsnap.child("uid").getValue(String.class);
                    String image = dsnap.child("imageUrl").getValue(String.class);

                    ModelVisits vis = new ModelVisits();
                    vis.setName(name);
                    vis.setUid(uid);
                    vis.setImageUrl(image);

                    list.add(vis);
                }
                databaseReference.keepSynced(true);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layReturnVisits:
                Intent it = new Intent(ViewProfileActivity.this, LikesActivity.class);
                startActivity(it);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                ViewProfileActivity.super.finish();
                break;
        }
    }
}
