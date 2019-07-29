package br.com.bangu_ao_vivo.bangu.Dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import br.com.bangu_ao_vivo.bangu.Model.ModelMatch;
import br.com.bangu_ao_vivo.bangu.R;

/**
 * Created by Toshiba pc on 25/03/2019.
 */

@SuppressLint("ValidFragment")
public class ModalPerson extends BottomSheetDialogFragment {

    private ImageView ivPeople;
    private TextView tvNameAge, tvLocal;
    CardView cvMatch;
    Context context = getContext();

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ImageView ivLike;

    String name, url, uid, location;



    public ModalPerson (String name, String url, String uid, String location) {
        this.name = name;
        this.url = url;
        this.uid = uid;
        this.location = location;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        String state = "nao";

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("user");

        SharedPreferences preferences = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);

        final String mId = preferences.getString("id", "");

       /* ModelMatch match = new ModelMatch();

        match.setStateMatch(state); */

        databaseReference.child(mId).child("match").child("StateMatch").setValue(state);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = getLayoutInflater().inflate(R.layout.lay_bottom_details, null);

        this.getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ivPeople = view.findViewById(R.id.ivPeople);
        tvNameAge = view.findViewById(R.id.tvNomeAge);
        tvLocal = view.findViewById(R.id.tvLocal);
        cvMatch = view.findViewById(R.id.cvMatch);
        ivLike = view.findViewById(R.id.ivHeartLike);


        Picasso.get().load(url).into(ivPeople);
        tvNameAge.setText(name + ", 19");
        tvLocal.setText(location);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("user");


        cvMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);

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

                databaseReference.child(mId).child("match").setValue(match);

                String status =  databaseReference.child(mId).child("match").child("StateMatch").toString();

                if (status == "sim"){
                    Toast.makeText(getContext(), "Tetse" + status, Toast.LENGTH_LONG).show();

                }

            }
        });

        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivLike.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.heart_color_ic));

                //metodos de insercao
                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference("user");

                SharedPreferences preferences = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);

                final String mName = preferences.getString("name", "");
                final String mId = preferences.getString("id", "");
                final String mUrl = preferences.getString("image", "");

                ModelMatch match = new ModelMatch();

                match.setName(mName);
                match.setImgUrl(mUrl);
                match.setId(mId);

                databaseReference.child(uid).child("match").setValue(match);


            }
        });


        return view;
    }
}
