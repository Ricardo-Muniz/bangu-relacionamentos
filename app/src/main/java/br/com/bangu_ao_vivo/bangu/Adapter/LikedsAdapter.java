package br.com.bangu_ao_vivo.bangu.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
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

import java.util.List;

import br.com.bangu_ao_vivo.bangu.Activity.DetailsActivity;
import br.com.bangu_ao_vivo.bangu.Activity.LikesActivity;
import br.com.bangu_ao_vivo.bangu.Activity.MatchActivity;
import br.com.bangu_ao_vivo.bangu.Model.ModelLiked;
import br.com.bangu_ao_vivo.bangu.Model.ModelMatch;
import br.com.bangu_ao_vivo.bangu.Model.ModelRequest;
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

public class LikedsAdapter extends RecyclerView.Adapter<LikedsAdapter.ViewHolder> {

    List<ModelLiked> values;
    Context context;
    FirebaseUser fuser;
    ApiServiceNotifications apiService;

    public LikedsAdapter(List<ModelLiked> values, Context context) {
        this.values = values;
        this.context = context;
    }

    @NonNull
    @Override
    public LikedsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_likeds, parent,
                false);
        return new LikedsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LikedsAdapter.ViewHolder holder, int position) {

        ModelLiked liked = values.get(position);
        Glide.with(context)
                .load(liked.getImgUrl())
                .asBitmap()
                .centerCrop()
                .into(holder.ivPersonLiked);

        holder.tvNameLiked.setText(liked.getName());

        holder.ivPersonLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, DetailsActivity.class);
                it.putExtra("user", liked.getUid());
                it.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(it);
            }
        });

        holder.layRecuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fuser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase.getInstance().getReference()
                        .child("user").child(fuser.getUid()).child("likes").child(liked.getUid()).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(context, LikesActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    context.startActivity(intent);
                                    ((Activity) context).finish();
                                    notifyDataSetChanged();
                                } else {

                                }
                            }
                        });
            }
        });

        holder.layAcept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fuser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                final DatabaseReference data = ref.child("user").child(fuser.getUid());
                data.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("likes").child(liked.getUid()).exists()) {
                            String TAG_NAME = dataSnapshot.child("name").getValue(String.class);
                            String TAG_IMAGE = dataSnapshot.child("urlImage").getValue(String.class);
                            String msg = " deu match com você";
                            sendNotificationMatch(liked.getUid(), TAG_NAME, msg, liked.getUid(), TAG_IMAGE);

                            matchedDatabase(liked.getUid(), TAG_NAME, TAG_IMAGE);

                            Intent it = new Intent(context, MatchActivity.class);
                            it.putExtra("user", liked.getUid());
                            it.putExtra("myUser", fuser.getUid());
                            it.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            context.startActivity(it);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    private void sendNotificationMatch(String receiver, final String username, final String message, String KEY_USER, String TAG_URL) {
        apiService = Client.getClient("https://fcm.googleapis.com/").create(ApiServiceNotifications.class);
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.drawable.ic_notf, username + message, "Novo match :)",
                            KEY_USER);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
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

        liked(KEY_USER,TAG_URL,username);

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

    private void matchedDatabase (String KEY_USER, String TAG_NAME, String TAG_IMG_URL) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference data = ref.child("user").child(KEY_USER);

        ModelMatch match = new ModelMatch();

        match.setName(TAG_NAME);
        match.setId(fuser.getUid());
        match.setImgUrl(TAG_IMG_URL);
        match.setStateMatch(false);
        match.setMatched(true);

        data.child("match").setValue(match);

        DatabaseReference reftw = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference datatw = reftw.child("user").child(KEY_USER).child("requests");

        ModelRequest request = new ModelRequest();
        request.setUid(fuser.getUid());
        request.setState(false);

        datatw.child(fuser.getUid()).setValue(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivPersonLiked;
        private TextView tvNameLiked;
        private LinearLayout layAcept;
        private LinearLayout layRecuse;

        public ViewHolder(View itemView) {
            super(itemView);

            ivPersonLiked = itemView.findViewById(R.id.ivPersonLiked);
            layRecuse = itemView.findViewById(R.id.layRecuse);
            layAcept = itemView.findViewById(R.id.layAcept);
            tvNameLiked = itemView.findViewById(R.id.tvNameLiked);
        }
    }
}
