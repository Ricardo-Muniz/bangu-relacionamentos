package br.com.bangu_ao_vivo.bangu.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import br.com.bangu_ao_vivo.bangu.Adapter.UserDisplayChatAdapter;
import br.com.bangu_ao_vivo.bangu.Model.ChatUsers;
import br.com.bangu_ao_vivo.bangu.Model.ModelChatList;
import br.com.bangu_ao_vivo.bangu.Notifications.Token;
import br.com.bangu_ao_vivo.bangu.R;

public class DisplayChatFragment extends Fragment {

    private static long INT_VALUE_COUNT = 0;
    List<ChatUsers> chat;
    UserDisplayChatAdapter adapter;

    RecyclerView recyclerChat;
    View includeChat;
    TextView tvConversas;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseUser fuser;

    List<ModelChatList> chatList;


    public DisplayChatFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        tvConversas = view.findViewById(R.id.tvConversas);
        includeChat = view.findViewById(R.id.includeChat);
        recyclerChat = view.findViewById(R.id.recycler_display_chat);
        recyclerChat.setHasFixedSize(true);
        recyclerChat.setLayoutManager(new LinearLayoutManager(getContext()));

        chatList = new ArrayList<>();
        adapter = new UserDisplayChatAdapter(getContext(), chat, true);

        initializeDataLoadChat();

        //printAlphaNumeric();

        return view;
    }

    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }

    private void initializeDataLoadChat() {

        FirebaseApp.initializeApp(getContext());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("chatlist").child(fuser.getUid());
        Query recentMessages = databaseReference;

        recyclerChat.setVisibility(View.VISIBLE);
        includeChat.setVisibility(View.GONE);

        recentMessages.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long value = dataSnapshot.getChildrenCount();
                if (value > 0) {
                    chatList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        recyclerChat.setVisibility(View.VISIBLE);
                        includeChat.setVisibility(View.GONE);
                        tvConversas.setVisibility(View.VISIBLE);

                        ModelChatList listChat = snapshot.getValue(ModelChatList.class);

                        chatList.add(listChat);
                    }

                } else {
                    includeChat.setVisibility(View.VISIBLE);
                    recyclerChat.setVisibility(View.GONE);
                    tvConversas.setVisibility(View.GONE);
                }

                chatList();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());

    }

    private void chatList() {
        chat = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("user");
        Query recent = databaseReference.orderByChild("timestamp");

        recent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatUsers user = snapshot.getValue(ChatUsers.class);
                    for (ModelChatList chatlist : chatList) {
                        if (user.getId().equals(chatlist.getId())) {
                                chat.add(user);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                adapter = new UserDisplayChatAdapter(getContext(), chat, true);
                recyclerChat.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
