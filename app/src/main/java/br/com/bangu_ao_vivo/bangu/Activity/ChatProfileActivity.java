package br.com.bangu_ao_vivo.bangu.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import java.util.HashMap;
import java.util.List;

import br.com.bangu_ao_vivo.bangu.Adapter.ChatUserAdapter;
import br.com.bangu_ao_vivo.bangu.Model.ChatUsers;
import br.com.bangu_ao_vivo.bangu.Model.ModelChat;
import br.com.bangu_ao_vivo.bangu.Model.ModelRequest;
import br.com.bangu_ao_vivo.bangu.Notifications.Client;
import br.com.bangu_ao_vivo.bangu.NotificationsLikeUser.DataLike;
import br.com.bangu_ao_vivo.bangu.NotificationsLikeUser.MyResponseLike;
import br.com.bangu_ao_vivo.bangu.NotificationsLikeUser.SenderLike;
import br.com.bangu_ao_vivo.bangu.NotificationsLikeUser.TokenLike;
import br.com.bangu_ao_vivo.bangu.R;
import br.com.bangu_ao_vivo.bangu.Utils.ApiServiceNotificationsLike;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatProfileActivity extends AppCompatActivity implements View.OnClickListener {

    public static String KEY_RESULT_UID_CONSTANT = "";
    public static String RESULT_IMAGE_PROFILE = "";
    private CircleImageView ivProfileuserChat;
    private TextView tvNameProfileChat;
    private EditText edtMessageChat;
    private CardView cvSendMessage;
    private ImageView ivReturnProfile;
    private CardView cvSendRequest;
    private LinearLayout laySendRequest, layVisitProfile;

    private Context context = this;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    FirebaseUser fuser;

    ChatUserAdapter chatUserAdapter;
    List<ModelChat> mchat;

    ApiServiceNotificationsLike apiService;
    boolean notify = false;
    boolean state = false;

    RecyclerView recycler;

    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_profile);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(ApiServiceNotificationsLike.class);

        //inicializador de ids de widgets
        inicializeIDS();

        //load user chat
        loadProfileChat();

        //inicialize firebase app
        initializeFirebaseApp();

        recycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recycler.setLayoutManager(linearLayoutManager);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        enablePrivate();

        //item value click
        cvSendMessage.setOnClickListener(this);
        ivReturnProfile.setOnClickListener(this);
        cvSendRequest.setOnClickListener(this);
        layVisitProfile.setOnClickListener(this);
    }

    private void inicializeIDS() {
        ivProfileuserChat = findViewById(R.id.ivProfileChat);
        tvNameProfileChat = findViewById(R.id.tvNameProfileChat);
        edtMessageChat = findViewById(R.id.edtMessage);
        cvSendMessage = findViewById(R.id.cvSendMessage);
        recycler = findViewById(R.id.recyclerChat);
        ivReturnProfile = findViewById(R.id.ivReturnProfile);
        cvSendRequest = findViewById(R.id.cvSendRequest);
        laySendRequest = findViewById(R.id.laySendRequest);
        layVisitProfile = findViewById(R.id.layVisitProfile);
    }

    private void initializeFirebaseApp () {
        FirebaseApp.initializeApp(context);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }

    private void loadProfileChat () {

        intent = getIntent();
        KEY_RESULT_UID_CONSTANT = intent.getStringExtra("user");

        SharedPreferences prefs = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        String myUid = prefs.getString("id", "");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference child = ref.child("user").child(KEY_RESULT_UID_CONSTANT);
        child.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String urlProfileImage = (String) dataSnapshot.child("urlImage").getValue();
                String nameUserChat = (String) dataSnapshot.child("name").getValue();

                Glide.with(getApplicationContext()).load(urlProfileImage).into(ivProfileuserChat);

                tvNameProfileChat.setText(nameUserChat);
                RESULT_IMAGE_PROFILE = urlProfileImage;

                readMessages(myUid, KEY_RESULT_UID_CONSTANT, RESULT_IMAGE_PROFILE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void enablePrivate () {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference data = ref.child("user").child(fuser.getUid()).child("requests");

        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    if (dataSnapshot.child(KEY_RESULT_UID_CONSTANT).child("state").exists()) {
                        state = dataSnapshot.child(KEY_RESULT_UID_CONSTANT).child("state").getValue(boolean.class);
                    }
                    if (dataSnapshot.child(KEY_RESULT_UID_CONSTANT).exists() && state) {
                        cvSendRequest.setVisibility(View.VISIBLE);
                    } else {
                        cvSendRequest.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessageUser (String sender, String receiver, String message) {
        reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);

        reference.child("chat-geral").push().setValue(hashMap);


        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chatlist")
                .child(sender)
                .child(receiver);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef.child("id").setValue(receiver);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("chatlist")
                .child(receiver)
                .child(sender);
        chatRefReceiver.child("id").setValue(fuser.getUid());


        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("user").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ChatUsers user = dataSnapshot.getValue(ChatUsers.class);
                if (notify) {
                    sendNotifiaction(receiver, user.getName(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void currentUser(String userid){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }

    private void readMessages (String myUid, String userUid, String imgUrl){
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("chat-geral");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelChat chat = snapshot.getValue(ModelChat.class);
                    if (chat.getReceiver().equals(myUid) && chat.getSender().equals(userUid) ||
                    chat.getReceiver().equals(userUid) && chat.getSender().equals(myUid)) {
                        mchat.add(chat);
                    }
                    chatUserAdapter = new ChatUserAdapter(ChatProfileActivity.this, mchat, imgUrl);
                    recycler.setAdapter(chatUserAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendNotifiaction(String receiver, final String username, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    TokenLike token = snapshot.getValue(TokenLike.class);
                    DataLike data = new DataLike(fuser.getUid(), R.drawable.ic_notf, username+": "+message, "Nova mensagem",
                            KEY_RESULT_UID_CONSTANT);

                    SenderLike sender = new SenderLike(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponseLike>() {
                                @Override
                                public void onResponse(Call<MyResponseLike> call, Response<MyResponseLike> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            //Toast.makeText(ChatProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponseLike> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void status(String status, int timestamp){
        reference = FirebaseDatabase.getInstance().getReference("user").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        hashMap.put("timestamp", timestamp);

        reference.updateChildren(hashMap);
    }


    @Override
    protected void onResume() {
        super.onResume();
        status("online", 1);
        currentUser(KEY_RESULT_UID_CONSTANT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline", 2);
        currentUser("none");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cvSendMessage:
                notify = true;
                String message = edtMessageChat.getText().toString();
                if (!message.equals("")) {
                    sendMessageUser(fuser.getUid(), KEY_RESULT_UID_CONSTANT, message);
                } else {
                    Toast.makeText(context, "Sem mensagens a enviar.", Toast.LENGTH_LONG).show();
                }
                edtMessageChat.getText().clear();
                break;

            case R.id.ivReturnProfile:
                ChatProfileActivity.super.finish();
                break;

            case R.id.cvSendRequest:
                requestAcept();
                break;

            case R.id.layVisitProfile:
                Intent intentDetails = new Intent(ChatProfileActivity.this, DetailsActivity.class);
                intentDetails.putExtra("user", KEY_RESULT_UID_CONSTANT);
                startActivity(intentDetails);
                break;
        }
    }

    private void requestAcept() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference data = ref.child("user").child(KEY_RESULT_UID_CONSTANT).child("permited");

        DatabaseReference reftw = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference datatw = reftw.child("user").child(fuser.getUid()).child("requests");

        ModelRequest request = new ModelRequest();
        request.setUid(fuser.getUid());

        data.child(fuser.getUid()).setValue(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Voce liberou ao usuario.", Toast.LENGTH_LONG).show();
            }
        });

        ModelRequest requesttw = new ModelRequest();
        requesttw.setUid(KEY_RESULT_UID_CONSTANT);
        requesttw.setState(false);

        datatw.child(KEY_RESULT_UID_CONSTANT).setValue(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                cvSendRequest.setVisibility(View.GONE);
            }
        });
    }
}
