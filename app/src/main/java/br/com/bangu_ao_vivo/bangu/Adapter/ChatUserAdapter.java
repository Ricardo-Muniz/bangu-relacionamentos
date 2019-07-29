package br.com.bangu_ao_vivo.bangu.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.bangu_ao_vivo.bangu.Model.ModelChat;
import br.com.bangu_ao_vivo.bangu.R;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<ModelChat> mChat;
    private String imageurl;

    public ChatUserAdapter(Context context, List<ModelChat> mChat, String imgUrl) {
        this.mContext = context;
        this.mChat = mChat;
        this.imageurl = imgUrl;
    }

    @NonNull
    @Override
    public ChatUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new ChatUserAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new ChatUserAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatUserAdapter.ViewHolder holder, int position) {

        ModelChat chat = mChat.get(position);

        holder.show_message.setText(chat.getMessage());

        if (position == mChat.size()-1){
            if (chat.isIsseen()){
                holder.txt_seen.setText("visualizado");
            } else {
                holder.txt_seen.setText("entregue");
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView show_message;
        private TextView txt_seen;


        @SuppressLint("ResourceType")
        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.tvMessageUser);
            txt_seen = itemView.findViewById(R.id.txt_seen);

        }
    }

    @Override
    public int getItemViewType(int position) {
        SharedPreferences preferences = mContext.getSharedPreferences("login", Context.MODE_PRIVATE);
        String uid = preferences.getString("id", "");
        if (mChat.get(position).getSender().equals(uid)) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }

    }
}
