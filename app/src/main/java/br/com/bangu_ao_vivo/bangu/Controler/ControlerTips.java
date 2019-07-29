package br.com.bangu_ao_vivo.bangu.Controler;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ControlerTips extends Activity {


    public void verifyPositionClickedAndConstructor(Context context, int dataPosition, ImageView ivBackground,
                                                    TextView tvTitleTop, TextView tvNameEditor,
                                                    TextView tvWeek, TextView tvTitleMain, TextView tvTextMain,
                                                    CircleImageView ivEditorImagePofile) {

        if (dataPosition == 0) {
            Toast.makeText(context, "Você teve um problema para se conectar a tela", Toast.LENGTH_LONG).show();
        } else if (dataPosition == 1) {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference child = ref.child("tips").child("tipsOne");
            child.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String urlImageBackground = (String) dataSnapshot.child("imageBackground").getValue();
                    String urlImageProfileEditor = (String) dataSnapshot.child("profileEditor").getValue();
                    String titleTop = (String) dataSnapshot.child("titleOne").getValue();
                    String nameEditor = (String) dataSnapshot.child("nameEditor").getValue();
                    String dataWeek = (String) dataSnapshot.child("week").getValue();
                    String titleMain = (String) dataSnapshot.child("titleMain").getValue();
                    String textMain = (String) dataSnapshot.child("textMain").getValue();


                    Glide.with(context)
                            .load(urlImageBackground)
                            .asBitmap()
                            .centerCrop()
                            .into(ivBackground);

                    Glide.with(context)
                            .load(urlImageProfileEditor)
                            .asBitmap()
                            .centerCrop()
                            .into(ivEditorImagePofile);

                    tvTitleTop.setText(titleTop);

                    tvNameEditor.setText(nameEditor);

                    tvWeek.setText(dataWeek);

                    tvTitleMain.setText(titleMain);

                    tvTextMain.setText(textMain);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else if (dataPosition == 2) {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference child = ref.child("tips").child("tipsTwo");
            child.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String urlImageBackground = (String) dataSnapshot.child("imageBackground").getValue();
                    String urlImageProfileEditor = (String) dataSnapshot.child("profileEditor").getValue();
                    String titleTop = (String) dataSnapshot.child("titleOne").getValue();
                    String nameEditor = (String) dataSnapshot.child("nameEditor").getValue();
                    String dataWeek = (String) dataSnapshot.child("week").getValue();
                    String titleMain = (String) dataSnapshot.child("titleMain").getValue();
                    String textMain = (String) dataSnapshot.child("textMain").getValue();


                    Glide.with(context)
                            .load(urlImageBackground)
                            .asBitmap()
                            .centerCrop()
                            .into(ivBackground);

                    Glide.with(context)
                            .load(urlImageProfileEditor)
                            .asBitmap()
                            .centerCrop()
                            .into(ivEditorImagePofile);

                    tvTitleTop.setText(titleTop);

                    tvNameEditor.setText(nameEditor);

                    tvWeek.setText(dataWeek);

                    tvTitleMain.setText(titleMain);

                    tvTextMain.setText(textMain);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        } else if (dataPosition == 3) {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference child = ref.child("tips").child("tipsThree");
            child.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String urlImageBackground = (String) dataSnapshot.child("imageBackground").getValue();
                    String urlImageProfileEditor = (String) dataSnapshot.child("profileEditor").getValue();
                    String titleTop = (String) dataSnapshot.child("titleOne").getValue();
                    String nameEditor = (String) dataSnapshot.child("nameEditor").getValue();
                    String dataWeek = (String) dataSnapshot.child("week").getValue();
                    String titleMain = (String) dataSnapshot.child("titleMain").getValue();
                    String textMain = (String) dataSnapshot.child("textMain").getValue();

                    Glide.with(context)
                            .load(urlImageBackground)
                            .asBitmap()
                            .centerCrop()
                            .into(ivBackground);

                    Glide.with(context)
                            .load(urlImageProfileEditor)
                            .asBitmap()
                            .centerCrop()
                            .into(ivEditorImagePofile);

                    tvTitleTop.setText(titleTop);

                    tvNameEditor.setText(nameEditor);

                    tvWeek.setText(dataWeek);

                    tvTitleMain.setText(titleMain);

                    tvTextMain.setText(textMain);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            Toast.makeText(context, "Você teve um problema para se conectar a tela", Toast.LENGTH_LONG).show();
        }

    }


    public void constructorTextCards (TextView tvTextMainOne, TextView tvTextMainTwo, TextView tvTextMainThree) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference child = ref.child("tips").child("tipsOne");
        child.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String textMain = dataSnapshot.child("textMain").getValue(String.class);
                tvTextMainOne.setText(textMain);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference refTwo = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference childTwo = refTwo.child("tips").child("tipsTwo");
        childTwo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String textMain = dataSnapshot.child("textMain").getValue(String.class);
                tvTextMainTwo.setText(textMain);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DatabaseReference refThree = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference childThree = refThree.child("tips").child("tipsThree");
        childThree.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String textMain = dataSnapshot.child("textMain").getValue(String.class);
                tvTextMainThree.setText(textMain);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref.keepSynced(true);
        refTwo.keepSynced(true);
        refThree.keepSynced(true);

    }


}
