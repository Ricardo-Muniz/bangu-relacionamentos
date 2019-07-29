package br.com.bangu_ao_vivo.bangu.Controler;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.com.bangu_ao_vivo.bangu.Model.ModelUser;
import br.com.bangu_ao_vivo.bangu.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ControlerRelease {

    List<ModelUser> list;

    public void textColoredCount (TextView tv, Context context) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("user");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int childsCount = (int) dataSnapshot.getChildrenCount();

                    ValueAnimator animator = new ValueAnimator();
                    animator.setObjectValues(0, childsCount);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animation) {
                            tv.setText(String.valueOf(animation.getAnimatedValue() + " novos matches"));
                        }
                    });
                    animator.setEvaluator(new TypeEvaluator<Integer>() {
                        public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                            return Math.round(startValue + (endValue - startValue) * fraction);
                        }
                    });
                    animator.setDuration(3000);
                    animator.start();
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void textNameColored (TextView tvName, Context context) {

        SharedPreferences preferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        final String uid = preferences.getString("id", "");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference child = ref.child("user").child(uid).child("name");

        child.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name;
                int size;
                name = dataSnapshot.getValue(String.class);
                size = name.length();

                Spannable spannableName = new SpannableString(name + ", Bem vindo(a)!");
                spannableName.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorMain)),0, size,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvName.setText(spannableName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void clickLinkSite (CardView card, Context context) {
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.banguaovivo.com.br/";
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(context, Uri.parse(url));
                builder.setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left);
                builder.setExitAnimations(context, R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    public void randomImagesProfileHome (CircleImageView ivProfileOne, CircleImageView ivProfileTwo,
                                         CircleImageView ivProfileCenter, Context context) {

        list = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("user");
        databaseReference.keepSynced(true);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*
                * Este codigo abaixo vale ouro...OURO
                * */
                List<String> lst = new ArrayList<String>(); // Result will be holded Here
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                   // String url = ds.child("urlImage").getValue(String.class);
                    lst.add(String.valueOf(ds.child("urlImage").getValue()));

                    String ramdomCenter = lst.get(new Random().nextInt(lst.size()));
                    String ramdomOne = lst.get(new Random().nextInt(lst.size()));
                    String ramdomTwo = lst.get(new Random().nextInt(lst.size()));

                    Glide.with(context)
                            .load(ramdomCenter)
                            .asBitmap()
                            .centerCrop()
                            .into(ivProfileCenter);

                    Glide.with(context)
                            .load(ramdomOne)
                            .asBitmap()
                            .centerCrop()
                            .into(ivProfileOne);

                    Glide.with(context)
                            .load(ramdomTwo)
                            .asBitmap()
                            .centerCrop()
                            .into(ivProfileTwo);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
