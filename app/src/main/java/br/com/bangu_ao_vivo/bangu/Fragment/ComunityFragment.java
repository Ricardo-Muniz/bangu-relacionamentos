package br.com.bangu_ao_vivo.bangu.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import br.com.bangu_ao_vivo.bangu.Adapter.SwipeCardAdapter;
import br.com.bangu_ao_vivo.bangu.Model.ModelUser;
import br.com.bangu_ao_vivo.bangu.R;


public class ComunityFragment extends Fragment {

    private static String STRING_FINAL_REGION = "";
    private static String STRING_CURRENT_CONTRY = "";
    List<ModelUser> list;
    SwipeCardAdapter adapter;

    RecyclerView recycler;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private static String STRING_REGION = "";
    private static String STRING_REGION_FILTER = "";
    private static String STRING_SEX = "";
    private static long INT_MIN_AGE = 0;
    private static long INT_MAX_AGE = 0;
    private static boolean activity = false;

    FirebaseUser fuser;

    public ComunityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comunity, container, false);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        initializeRegion();
        initializePrefs();
        initializeLocationCurrent();

        recycler = view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recycler.setItemAnimator(new DefaultItemAnimator());

        list = new ArrayList<>();
        adapter = new SwipeCardAdapter(list, getContext(), true);

        initializeDataLoadPerson();

        return view;
    }

    private String initializeRegion() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference data = ref.child("user").child(fuser.getUid()).child("location");
        data.keepSynced(true);
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                STRING_REGION_FILTER = dataSnapshot.child("state").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return STRING_REGION_FILTER;
    }


    private void initializePrefs() {
        SharedPreferences preferences = getContext().getSharedPreferences("filter", Context.MODE_PRIVATE);
        boolean active = preferences.getBoolean("filterActive", true);
        if (active) {
            String region = preferences.getString("countryRegion", "");
            String sexPerson = preferences.getString("sexPerson", "");
            long ageMin = preferences.getLong("ageMin", 0);
            long ageMax = preferences.getLong("ageMax", 0);

            STRING_REGION = region;
            STRING_SEX = sexPerson;
            INT_MIN_AGE = ageMin;
            INT_MAX_AGE = ageMax;

            if (region.equals(STRING_REGION_FILTER)) {
                STRING_FINAL_REGION = region;
                STRING_REGION = "";
            }

            activity = true;
        } else {
            activity = false;
        }
    }

    public void initializeLocationCurrent() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference data = ref.child("user").child(fuser.getUid()).child("location");
        data.keepSynced(true);
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                STRING_CURRENT_CONTRY = dataSnapshot.child("state").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void initializeDataLoadPerson() {

        FirebaseApp.initializeApp(getContext());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("user");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                list.clear();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {

                    ModelUser data = new ModelUser();
                    String nameId = (String) objSnapshot.child("name").getValue();
                    String id = (String) objSnapshot.child("id").getValue();
                    String urlImageId = (String) objSnapshot.child("urlImage").getValue();
                    String location = (String) objSnapshot.child("location").child("state").getValue();
                    String city = (String) objSnapshot.child("location").child("city").getValue();
                    String sex = (String) objSnapshot.child("sex").getValue();
                    String status = (String) objSnapshot.child("status").getValue();
                    long age = (long) objSnapshot.child("age").getValue();

                    data.setName(nameId);
                    data.setId(id);
                    data.setSex(sex);
                    data.setLocalidade(location);
                    data.setCity(city);
                    data.setUrlImage(urlImageId);
                    data.setAge(age);
                    data.setStatus(status);

                    if (activity && !STRING_REGION.isEmpty()) {
                       if (data.getCity().equals(STRING_REGION) && data.getSex().equals(STRING_SEX) && !dataSnapshot.child(
                                fuser.getUid()).child("blackList").child(id).exists() && data.getAge() >= INT_MIN_AGE
                                && data.getAge() <= INT_MAX_AGE) {

                            list.add(data);
                        }
                    } else if (activity && !STRING_FINAL_REGION.isEmpty()) {
                        if (data.getLocalidade().equals(STRING_FINAL_REGION) && data.getSex().equals(STRING_SEX) && !dataSnapshot.child(
                                fuser.getUid()).child("blackList").child(id).exists()) {
                            list.add(data);
                        }
                    } else {
                        if (data.getLocalidade().equals(STRING_CURRENT_CONTRY) && !dataSnapshot.child(
                                fuser.getUid()).child("blackList").child(id).exists()) {
                            list.add(data);
                        }
                    }

                   /*if (data.getLocalidade().equals(loca)) {
                        list.add(data);
                    }*/
                }
                databaseReference.keepSynced(true);
                recycler.setAdapter(adapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

}
