package br.com.bangu_ao_vivo.bangu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import br.com.bangu_ao_vivo.bangu.Activity.DetailsActivity;
import br.com.bangu_ao_vivo.bangu.Activity.MatchActivity;
import br.com.bangu_ao_vivo.bangu.Model.ModelLiked;
import br.com.bangu_ao_vivo.bangu.Model.ModelMatch;
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

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder> {

    Context context;
    List<ModelUser> values;
    public static String uidConstant;

    FirebaseUser fuser;
    ApiServiceNotifications apiService;
    private boolean notify = false;
    private static boolean TRULED = false;

    public PersonAdapter(Context context, List<ModelUser> values) {
        this.context = context;
        this.values = values;
    }

    @NonNull
    @Override
    public PersonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_cards_person, parent,
                false);
        return new PersonAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonAdapter.ViewHolder holder, int position) {
        ModelUser user = values.get(position);
        holder.tv_name_age.setText(user.getName()+ ", " + user.getAge());
        Glide.with(context)
                .load(user.getUrlImage())
                .asBitmap()
                .into(holder.iv_Person);

        uidConstant = user.getId();

        if (user.getVerificate() != 0) {
            if (user.getVerificate() == 1) {
                holder.ivVerificateSw.setVisibility(View.GONE);
            } else if (user.getVerificate() == 2) {
                holder.ivVerificateSw.setVisibility(View.VISIBLE);
            }
        }

        holder.ivRecusePerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whiteList(user.getId());
                removeTopItem();
                notifyDataSetChanged();
            }
        });

        holder.ivMessageAte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atenptionUser(user.getId());
                removeTopItem();
                notifyDataSetChanged();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, DetailsActivity.class);
                it.putExtra("user", user.getId());
                it.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(it);
            }
        });

        holder.ivPersonLikeSw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whiteList(user.getId());
                likeUser(user.getId());
                removeTopItem();
                notifyDataSetChanged();
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
                    Data data = new Data(fuser.getUid(), R.drawable.ic_notf, username + message, "Novo match :)",
                            receiver);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            //Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
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


    private void likeUser(String uidper) {

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        notify = true;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference data = ref.child("user").child(fuser.getUid());
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("likes").child(uidper).exists()) {
                    String TAG_NAME = dataSnapshot.child("name").getValue(String.class);
                    String TAG_IMAGE = dataSnapshot.child("urlImage").getValue(String.class);
                    String msg = " deu match com você";
                    sendNotificationMatch(uidper, TAG_NAME, msg);
                    TRULED = true;
                    notify = false;

                    matchedDatabase(uidper, TAG_NAME, TAG_IMAGE);

                    Intent it = new Intent(context, MatchActivity.class);
                    it.putExtra("user", uidper);
                    it.putExtra("myUser", fuser.getUid());
                    it.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(it);

                    notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (notify) {
            DatabaseReference reftw = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference datatw = reftw.child("user").child(fuser.getUid());
            datatw.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("likes").child(uidper).exists()) {
                        String TAG_NAME = dataSnapshot.child("name").getValue(String.class);
                        String TAG_IMAGE = dataSnapshot.child("urlImage").getValue(String.class);

                        String msg = "Quero dar um oi";
                        String title = "Uma nova curtida";
                        sendNotification(uidper, msg, title, TAG_IMAGE, TAG_NAME);

                        notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        notify = false;

    }

    private void matchedDatabase (String KEY_USER, String TAG_NAME, String TAG_IMG_URL) {
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


    private void whiteList (String uid) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference data = ref.child("user").child(fuser.getUid()).child("whiteList");
        ModelWhiteList whiteList = new ModelWhiteList();
        whiteList.setUid(uid);
        data.child(whiteList.getUid()).setValue(whiteList).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });

    }

    private void atenptionUser(String KEY_USER) {

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference refTwo = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference dataTwo = refTwo.child("user").child(fuser.getUid());
        dataTwo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    String TAG_NAME = dataSnapshot.child("name").getValue(String.class);
                    String TAG_IMG_URL = dataSnapshot.child("urlImage").getValue(String.class);


                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    final DatabaseReference data = ref.child("user").child(KEY_USER);

                    ModelMatch match = new ModelMatch();

                    match.setName(TAG_NAME);
                    match.setId(fuser.getUid());
                    match.setImgUrl(TAG_IMG_URL);
                    match.setStateMatch(true);

                    data.child("match").setValue(match);

                    String msg = "Alguem";
                    sendNotification(KEY_USER, TAG_NAME, msg, TAG_IMG_URL, TAG_NAME);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(String receiver, final String username, final String message, String IMG_URL, String NAME) {
        apiService = Client.getClient("https://fcm.googleapis.com/").create(ApiServiceNotifications.class);

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.drawable.ic_notf, message, "Chamando sua atenção",
                            receiver);

                    Sender sender = new Sender(data, token.getToken());

                    liked(receiver, IMG_URL, NAME);

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            //Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
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

    private void liked(String KEY_USER, String TAG_IMG_URL, String TAG_NAME) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference data = ref.child("user").child(fuser.getUid()).child("myLikeds");
        ModelLiked liked = new ModelLiked();
        liked.setUid(KEY_USER);
        data.child(liked.getUid()).setValue(liked).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //ivLikeUser.setVisibility(View.GONE);
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

    public  String uidPerson () {
        return uidConstant;
    }

    public void removeTopItem() {
        values.remove(0);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_Person;
        TextView tv_name_age;
        ImageView ivRecusePerson;
        ImageView ivMessageAte;
        ImageView ivPersonLikeSw;
        ImageView ivVerificateSw;

        public ViewHolder(View itemView) {
            super(itemView);

            iv_Person = itemView.findViewById(R.id.iv_Person);
            tv_name_age = itemView.findViewById(R.id.tvNameAgePerson);
            ivRecusePerson = itemView.findViewById(R.id.ivRecusePerson);
            ivMessageAte = itemView.findViewById(R.id.ivMessageAte);
            ivPersonLikeSw = itemView.findViewById(R.id.ivPersonLikeSw);
            ivVerificateSw = itemView.findViewById(R.id.ivVerificateSw);
        }
    }
}
