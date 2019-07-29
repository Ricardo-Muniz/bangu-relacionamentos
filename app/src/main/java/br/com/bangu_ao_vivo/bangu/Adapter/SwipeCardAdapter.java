package br.com.bangu_ao_vivo.bangu.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.bangu_ao_vivo.bangu.Activity.DetailsActivity;
import br.com.bangu_ao_vivo.bangu.Model.ModelFilter;
import br.com.bangu_ao_vivo.bangu.Model.ModelMatch;
import br.com.bangu_ao_vivo.bangu.Model.ModelUser;
import br.com.bangu_ao_vivo.bangu.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Toshiba pc on 15/03/2019.
 */

public class SwipeCardAdapter extends RecyclerView.Adapter<SwipeCardAdapter.ViewHolder> {

    List<ModelUser> values;
    ArrayList<ModelMatch> items = new ArrayList<>();
    private Context context;
    LinearLayout layVisibilityUser;

    private Boolean active;

    Dialog dialog;
    SlidingUpPanelLayout panelLayout;

    ImageView ivUser;
    TextView tvNameAge, tvFlert;
    CardView cvCenter;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    public SwipeCardAdapter(List<ModelUser> values, Context context, Boolean active) {
        this.values = values;
        this.context = context;
        this.active = active;
    }

    @NonNull
    @Override
    public SwipeCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_circle, parent,
                false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final SwipeCardAdapter.ViewHolder holder, final int position) {

        final ModelUser user = values.get(position);
        final String imgUrl = values.get(position).getUrlImage();
        final String uid = values.get(position).getId();
        final String name = values.get(position).getName();
        final String location = values.get(position).getLocalidade();
        final double latitude = values.get(position).getLatitude();
        final double longitude = values.get(position).getLongitude();
        final String sex = values.get(position).getSex();

        SharedPreferences preferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);

        final String mName = preferences.getString("name", "");
        final String mId = preferences.getString("id", "");
        final String mUrl = preferences.getString("url", "");

        ModelFilter filter = new ModelFilter();
        String sexo = filter.getSex();

        SharedPreferences pref = context.getSharedPreferences("filter", Context.MODE_PRIVATE);

        final String region = pref.getString("region", "");
        final String sexy = pref.getString("sex", "");
        final int age = pref.getInt("age", 0);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference data = ref.child("user").child(mId).child("location");
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String data = dataSnapshot.child("state").getValue(String.class);
                if (Objects.equals(data, user.getLocalidade())) {
                    holder.cvPerson.setVisibility(View.VISIBLE);
                } else if (data != user.getLocalidade() ){
                    holder.cvPerson.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


       // Glide.with(context).load(user.getUrlImage()).into(holder.ivPerson);

        Glide.with(context)
                .load(user.getUrlImage())
                .asBitmap()
                .fitCenter()
                .into(holder.ivPerson);

        holder.tvNome.setText(user.getName());

        if (active){
            if (user.getStatus().equals("online")){
                holder.img_status.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.online_ic_sized));
            } else {
                holder.img_status.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.offline_ic_sized));
            }
        } else {
            holder.img_status.setVisibility(View.GONE);
        }


        // Picasso.get().load(user.getUrlImage()).into(holder.ivPerson);

        //Picasso.get().load(user.getUrlImage()).into(holder.ivPerson);

       /* if (Objects.equals(mId, user.getId())) {
            holder.cvPerson.setVisibility(View.GONE);
            return;
        } */

        holder.cvPerson.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongViewCast")
            @Override
            public void onClick(View v) {

              /*  dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_user);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

                ivUser = dialog.findViewById(R.id.ivUser);
                tvNameAge = dialog.findViewById(R.id.tvNameAge);
                cvCenter = dialog.findViewById(R.id.cvTotal);
                tvFlert = dialog.findViewById(R.id.tvFlert);


                tvNameAge.setText(user.getName() + ", ");
                Picasso.get().load(imgUrl).into(ivUser); */

                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("user", user.getId());
                context.startActivity(intent);


            }
        });

    }


    /*private void task (String uid, String name, String imgUrl) {

        SharedPreferences preferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);

        final String mName = preferences.getString("name", "");
        final String mId = preferences.getString("id", "");
        final String mUrl = preferences.getString("image", "");
        String state = "sim";

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("user");

        ModelMatch match = new ModelMatch();

        match.setName(mName);
        match.setId(mId);
        match.setImgUrl(mUrl);
        match.setStateMatch(state);

        databaseReference.child(mId).child("match").setValue(match);

        String st = databaseReference.child(uid).child("match").child("StateMatch").toString();

        if (st != null){
            FragmentManager ft = ((AppCompatActivity) context).getSupportFragmentManager();

            ModalMath modalMath = new ModalMath(mName, mUrl);

            modalMath.show(ft, modalMath.getTag());
        } else {

        }

    }*/

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNome;
        private CircleImageView ivPerson;
        private CardView cvPerson;
        private ImageView ivUser;
        private ImageView img_status;

        private LinearLayout layVisibilityUser;


        @SuppressLint("ResourceType")
        public ViewHolder(View itemView) {
            super(itemView);

            tvNome = itemView.findViewById(R.id.tvUser);
            ivPerson = itemView.findViewById(R.id.ivPerson);
            cvPerson = itemView.findViewById(R.id.cvUser);
            ivUser = itemView.findViewById(R.id.ivUser);
            img_status = itemView.findViewById(R.id.img_status);

            layVisibilityUser = itemView.findViewById(R.id.layIndicatorVisibilty);

        }
    }

}