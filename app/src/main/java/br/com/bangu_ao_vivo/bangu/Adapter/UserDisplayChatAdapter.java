package br.com.bangu_ao_vivo.bangu.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import br.com.bangu_ao_vivo.bangu.Activity.ChatProfileActivity;
import br.com.bangu_ao_vivo.bangu.ComunityActivity;
import br.com.bangu_ao_vivo.bangu.Model.ChatUsers;
import br.com.bangu_ao_vivo.bangu.Model.ModelChat;
import br.com.bangu_ao_vivo.bangu.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserDisplayChatAdapter extends RecyclerView.Adapter<UserDisplayChatAdapter.ViewHolder> {

    private Context mContext;
    private List<ChatUsers> chatUsers;
    private Boolean ischat;

    FirebaseUser fuser;

    String theLastMessage = "default";

    public UserDisplayChatAdapter (Context mContext, List<ChatUsers> mChat, boolean ischat) {
        this.mContext = mContext;
        this.chatUsers = mChat;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.line_card_user_chat, parent, false);
        return new UserDisplayChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserDisplayChatAdapter.ViewHolder holder, int position) {

        ChatUsers users = chatUsers.get(position);
        holder.tvNameUser.setText(users.getName());
        Glide.with(mContext)
                .load(users.getUrlImage())
                .asBitmap()
                .centerCrop()
                .into(holder.cvProfileUser);

        if (ischat){
            lastMessage(users.getId(), holder.last_msg);
        } else {
            holder.last_msg.setVisibility(View.GONE);
        }

        if (ischat){
            lastMessage(users.getId(), holder.last_msg);
        } else {
            holder.last_msg.setVisibility(View.GONE);
        }

        if (ischat){
            if (users.getStatus().equals("online")){
                holder.iv_status.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.online_ic));
            } else {
                holder.iv_status.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.offline_ic));
            }
        } else {
            holder.iv_status.setVisibility(View.GONE);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                holder.cvProfileUser.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.circle_selcted_tw));
                holder.layCardProfile.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryInv));
                holder.iv_status.setVisibility(View.GONE);

                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create(); //Read Update
                alertDialog.setCancelable(false);
                alertDialog.setTitle("Deseja excluir essa conversa?");
                alertDialog.setMessage("só deletaremos do seu perfil, as mensagens enviadas não podem ser excluidas ainda.");
                alertDialog.setButton(Dialog.BUTTON_POSITIVE, "sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItem(users.getId());
                    }
                });

                alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Glide.with(mContext)
                                .load(users.getUrlImage())
                                .asBitmap()
                                .centerCrop()
                                .into(holder.cvProfileUser);
                        holder.layCardProfile.setBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
                        holder.iv_status.setVisibility(View.VISIBLE);
                    }
                });
                alertDialog.show();
                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ChatProfileActivity.class);
                intent.putExtra("user", users.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatUsers.size();
    }

    private void dialogDelete(String uid) {
    }

    private void deleteItem(String item) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference()
                .child("chatlist").child(fuser.getUid()).child(item).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(mContext, ComunityActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            mContext.startActivity(intent);
                            ((Activity) mContext).finish();
                        } else {

                        }
                    }
                });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvNameUser;
        public TextView last_msg;
        public ImageView iv_status;
        public CircleImageView cvProfileUser;
        public LinearLayout layCardProfile;

        public ViewHolder(View itemView) {
            super(itemView);

            cvProfileUser = itemView.findViewById(R.id.ivProfileChatDisp);
            tvNameUser = itemView.findViewById(R.id.tvNameUserDisp);
            last_msg = itemView.findViewById(R.id.last_msg);
            iv_status = itemView.findViewById(R.id.img_status);
            layCardProfile = itemView.findViewById(R.id.layCardProfile);

        }
    }

    //check for last message
    private void lastMessage(final String userid, final TextView last_msg){
        SharedPreferences preferences = mContext.getSharedPreferences("login", Context.MODE_PRIVATE);
        String user = preferences.getString("id", "");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chat-geral");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelChat chat = snapshot.getValue(ModelChat.class);
                    if (user != null && chat != null) {
                        if (chat.getReceiver().equals(user) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(user)) {
                            theLastMessage = chat.getMessage();
                        }
                    }
                }

                switch (theLastMessage){
                    case  "default":
                        last_msg.setText("No Message");
                        break;
                    default:
                        last_msg.setText(theLastMessage);
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
