package br.com.bangu_ao_vivo.bangu.Fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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

import br.com.bangu_ao_vivo.bangu.Activity.MatchActivity;
import br.com.bangu_ao_vivo.bangu.Adapter.PersonAdapter;
import br.com.bangu_ao_vivo.bangu.Model.ModelLiked;
import br.com.bangu_ao_vivo.bangu.Model.ModelMatch;
import br.com.bangu_ao_vivo.bangu.Model.ModelRequest;
import br.com.bangu_ao_vivo.bangu.Model.ModelUser;
import br.com.bangu_ao_vivo.bangu.Model.ModelWhiteList;
import br.com.bangu_ao_vivo.bangu.Notifications.Client;
import br.com.bangu_ao_vivo.bangu.Notifications.Data;
import br.com.bangu_ao_vivo.bangu.Notifications.MyResponse;
import br.com.bangu_ao_vivo.bangu.Notifications.Sender;
import br.com.bangu_ao_vivo.bangu.Notifications.Token;
import br.com.bangu_ao_vivo.bangu.R;
import br.com.bangu_ao_vivo.bangu.Utils.ApiServiceNotifications;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import swipeable.com.layoutmanager.OnItemSwiped;
import swipeable.com.layoutmanager.SwipeableLayoutManager;
import swipeable.com.layoutmanager.SwipeableTouchHelperCallback;
import swipeable.com.layoutmanager.touchelper.ItemTouchHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonFragment extends Fragment {

    private static String STRING_CURRENT_CONTRY = "";
    private static String TAG_IMG_URL = "";
    private static String TAG_NAME = "";
    private static boolean TRULED = false;
    private boolean notify = false;
    private PersonAdapter adapter;
    private List<ModelUser> list;

    private static String STRING_REGION = "";
    private static String STRING_CURRENT = "";
    private static String STRING_REGION_FILTER = "";
    private static String STRING_SEX = "";
    private static long INT_MIN_AGE = 0;
    private static long INT_MAX_AGE = 0;
    private static boolean activity = false;

    private static String STRING_FINAL_REGION = "";

    ApiServiceNotifications apiService;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private FirebaseUser fuser;

    RecyclerView recyclerView;


    public PersonFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_person, container, false);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(ApiServiceNotifications.class);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.recyclerview_person);

        list = new ArrayList<>();
        adapter = new PersonAdapter(getContext(), list);

        loadMyProfile();
        initializeRegion();
        initializePrefs();
        initializeLocationCurrent();
        initializeDATA();


        SwipeableTouchHelperCallback swipeableTouchHelperCallback =
                new SwipeableTouchHelperCallback(new OnItemSwiped() {
                    @Override
                    public void onItemSwiped() {
                        adapter.removeTopItem();
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onItemSwipedLeft() {
                        String uidper = adapter.uidPerson();
                        whiteList(uidper);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onItemSwipedRight() {
                        notify = true;
                        String uidper = adapter.uidPerson();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        final DatabaseReference data = ref.child("user").child(fuser.getUid());
                        data.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child("likes").child(uidper).exists()) {
                                    String msg = " deu match com você";
                                    sendNotificationMatch(uidper, TAG_NAME, msg);
                                    TRULED = true;
                                    notify = false;

                                    matchedDatabase(uidper);

                                    Intent it = new Intent(getContext(), MatchActivity.class);
                                    it.putExtra("user", uidper);
                                    it.putExtra("myUser", fuser.getUid());
                                    it.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(it);

                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        if (notify) {
                            String msg = "Quero dar um oi";
                            String title = "Uma nova curtida";
                            sendNotification(uidper, msg, title);
                        }
                        notify = false;

                    }

                    @Override
                    public void onItemSwipedUp() {
                        Log.e("SWIPE", "UP");
                    }


                    @Override
                    public void onItemSwipedDown() {
                        Log.e("SWIPE", "DOWN");
                    }
                }) {
                    @Override
                    public int getAllowedSwipeDirectionsMovementFlags(RecyclerView.ViewHolder viewHolder) {
                        return ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;

                    }
                };
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeableTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new SwipeableLayoutManager().setAngle(10)
                .setAnimationDuratuion(450)
                .setMaxShowCount(3)
                .setScaleGap(0.1f)
                .setTransYGap(0));

        return view;
    }

    private void matchedDatabase (String KEY_USER) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference data = ref.child("user").child(KEY_USER);

        ModelMatch match = new ModelMatch();

        match.setName(TAG_NAME);
        match.setId(fuser.getUid());
        match.setImgUrl(TAG_IMG_URL);
        match.setStateMatch(false);
        match.setMatched(true);

        data.child("match").setValue(match).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                whiteList(KEY_USER);
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
                    Data data = new Data(fuser.getUid(), R.drawable.airbnb_ic, username + message, "Novo match :)",
                            receiver);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
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

    private void sendNotification(String receiver, final String message, final String title) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.drawable.logo_ban, message, title,
                            receiver);

                    Sender sender = new Sender(data, token.getToken());

                    liked(receiver);

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            //Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
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

    private void liked(String KEY_USER) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference data = ref.child("user").child(fuser.getUid()).child("myLikeds");
        ModelLiked liked = new ModelLiked();
        liked.setUid(KEY_USER);
        data.child(liked.getUid()).setValue(liked).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //ivLikeUser.setVisibility(View.GONE);
                whiteList(KEY_USER);
            }
        });

        ModelLiked modelLiked = new ModelLiked();
        modelLiked.setUid(fuser.getUid());
        modelLiked.setImgUrl(TAG_IMG_URL);
        modelLiked.setName(TAG_NAME);

        DatabaseReference refTwo = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference dataTwo = refTwo.child("user").child(KEY_USER).child("likes");

        dataTwo.child(fuser.getUid()).setValue(modelLiked);
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

    private void initializePrefs() {

        SharedPreferences preferences = getContext().getSharedPreferences("filter", Context.MODE_PRIVATE);
        boolean active = preferences.getBoolean("filterActivePerson", true);
        if (active) {
            String region = preferences.getString("countryRegionPerson", "");
            String sexPerson = preferences.getString("sexPersonPerson", "");
            long ageMin = preferences.getLong("ageMinPerson", 0);
            long ageMax = preferences.getLong("ageMaxPerson", 0);

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

    private void initializeDATA() {

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
                    long verificate = (long) objSnapshot.child("extra").child("verificate").getValue();
                    long age = (long) objSnapshot.child("age").getValue();

                    data.setName(nameId);
                    data.setId(id);
                    data.setSex(sex);
                    data.setLocalidade(location);
                    data.setCity(city);
                    data.setUrlImage(urlImageId);
                    data.setAge(age);
                    data.setVerificate(verificate);

                    if (activity && !STRING_REGION.isEmpty()) {
                        if (data.getCity().equals(STRING_REGION) && data.getSex().equals(STRING_SEX) && !dataSnapshot.child(
                                fuser.getUid()).child("blackList").child(id).exists() && data.getAge() >= INT_MIN_AGE
                                && data.getAge() <= INT_MAX_AGE && !dataSnapshot.child(fuser.getUid())
                                .child("whiteList").child(id).exists()) {

                            list.add(data);
                        }
                    } else if (activity && !STRING_FINAL_REGION.isEmpty()) {
                        if (data.getLocalidade().equals(STRING_FINAL_REGION) && data.getSex().equals(STRING_SEX) && !dataSnapshot.child(
                                fuser.getUid()).child("blackList").child(id).exists() && !dataSnapshot.child(fuser.getUid())
                                .child("whiteList").child(id).exists()) {
                            list.add(data);
                        }
                    } else {
                        if (data.getLocalidade().equals(STRING_CURRENT_CONTRY) && !dataSnapshot.child(
                                fuser.getUid()).child("blackList").child(id).exists() && !dataSnapshot.child(fuser.getUid())
                                .child("whiteList").child(id).exists()) {
                            list.add(data);
                        }
                    }
                }
                databaseReference.keepSynced(true);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

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

}
