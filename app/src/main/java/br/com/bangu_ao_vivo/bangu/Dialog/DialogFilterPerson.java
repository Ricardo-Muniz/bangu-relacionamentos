package br.com.bangu_ao_vivo.bangu.Dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.bangu_ao_vivo.bangu.ComunityActivity;
import br.com.bangu_ao_vivo.bangu.R;

public class DialogFilterPerson extends DialogFragment {

    private static Number FLOAT_VALUE_MAX = 1;
    private static Number FLOAT_VALUE_MIN = 1;
    private LinearLayout layFiltrarBtn;
   // private SeekBar seekBarIdade;
    private TextView tvIdade, tvRegiao, tvSexo, tvProximoRegiao, tvOutraRegiao, tvMulherSexo, tvHomemSexo, tvLgbtSexo;
    private LinearLayout cvProximoRegiao, cvOutraRegiao, cvMulherSexo, cvHomemSexo, cvLgbtSexo;
    private CrystalRangeSeekbar rangeSeekbar;

    private static String TAG_CATEGORY_REGION = "";
    private static String TAG_CATEGORY_CURRENT = "";
    private static String TAG_CATEGORY_SEX = "";
    private static long TAG_CATEGORY_AGE_MIN = 0;
    private static long TAG_CATEGORY_AGE_MAX = 0;

    private FirebaseUser fuser;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_filter, null);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        //inicializacao de ids
        tvIdade = view.findViewById(R.id.tvIdade);
        //retirado
        //seekBarIdade = view.findViewById(R.id.seekBar);
        layFiltrarBtn = view.findViewById(R.id.layFiltrarBtn);
        cvProximoRegiao = view.findViewById(R.id.cvProximoRegiao);
        cvOutraRegiao = view.findViewById(R.id.cvOutraRegiao);
        tvProximoRegiao = view.findViewById(R.id.tvProximoRegiao);
        tvOutraRegiao = view.findViewById(R.id.tvOutraRegiao);
        tvMulherSexo = view.findViewById(R.id.tvMulherSexo);
        tvHomemSexo = view.findViewById(R.id.tvHomemSexo);
        tvLgbtSexo = view.findViewById(R.id.tvLgbtSexo);
        cvMulherSexo = view.findViewById(R.id.cvMulherSexo);
        cvHomemSexo = view.findViewById(R.id.cvHomemSexo);
        cvLgbtSexo = view.findViewById(R.id.cvLgbtSexo);
        tvRegiao = view.findViewById(R.id.tvRegiao);
        tvSexo = view.findViewById(R.id.tvSexo);
        rangeSeekbar = view.findViewById(R.id.rangeSeekbar1);


        cvProximoRegiao.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_cv_filter_clicked));
        tvProximoRegiao.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        TAG_CATEGORY_REGION = "";
        TAG_CATEGORY_CURRENT = "";
        //database response
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference region = reference.child("user").child(fuser.getUid()).child("location");
        region.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String city = (String) dataSnapshot.child("city").getValue();
                TAG_CATEGORY_REGION = city;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        tvRegiao.setText("Região - proximo");

        cvMulherSexo.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_cv_filter_clicked));
        tvMulherSexo.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        TAG_CATEGORY_SEX = "";
        TAG_CATEGORY_SEX = "Mulher";
        tvSexo.setText("Sexo - mulher");


        cvProximoRegiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvProximoRegiao.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_cv_filter_clicked));
                tvProximoRegiao.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                TAG_CATEGORY_REGION = "";
                TAG_CATEGORY_CURRENT = "";
                //database response
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                final DatabaseReference region = reference.child("user").child(fuser.getUid()).child("location");
                region.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String city = (String) dataSnapshot.child("city").getValue();
                        TAG_CATEGORY_REGION = city;

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                tvRegiao.setText("Região - proximo");

                cvOutraRegiao.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_cv_tags));
                tvOutraRegiao.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray));

                SharedPreferences preferences = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);

                final String mId = preferences.getString("id", "");

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                final DatabaseReference mostafa = ref.child("user").child(mId).child("location");

            }
        });

        cvOutraRegiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvOutraRegiao.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_cv_filter_clicked));
                tvOutraRegiao.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                TAG_CATEGORY_REGION = "";
                //database response
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                final DatabaseReference region = reference.child("user").child(fuser.getUid()).child("location");
                region.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String state = (String) dataSnapshot.child("state").getValue();
                        TAG_CATEGORY_REGION = state;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                tvRegiao.setText("Região - distante");

                cvProximoRegiao.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_cv_tags));
                tvProximoRegiao.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray));

            }
        });

        cvMulherSexo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvMulherSexo.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_cv_filter_clicked));
                tvMulherSexo.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                TAG_CATEGORY_SEX = "";
                TAG_CATEGORY_SEX = "Mulher";
                tvSexo.setText("Sexo - mulher");

                cvHomemSexo.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_cv_tags));
                tvHomemSexo.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray));
                cvLgbtSexo.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_cv_tags));
                tvLgbtSexo.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray));

            }
        });

        cvHomemSexo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvHomemSexo.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_cv_filter_clicked));
                tvHomemSexo.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                TAG_CATEGORY_SEX = "";
                TAG_CATEGORY_SEX = "Homem";
                tvSexo.setText("Sexo - homem");


                cvMulherSexo.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_cv_tags));
                tvMulherSexo.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray));
                cvLgbtSexo.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_cv_tags));
                tvLgbtSexo.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray));

            }
        });

        cvLgbtSexo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvLgbtSexo.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_cv_filter_clicked));
                tvLgbtSexo.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                TAG_CATEGORY_SEX = "";
                TAG_CATEGORY_SEX = "Todos";
                tvSexo.setText("Sexo - ambos");


                cvHomemSexo.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_cv_tags));
                tvHomemSexo.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray));
                cvMulherSexo.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_cv_tags));
                tvMulherSexo.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGray));

            }
        });

        //tvIdade.setText("Idade - ate " + seekBarIdade.getProgress() + " anos");
        layFiltrarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Toast.makeText(getContext(), "Filtro: " + TAG_CATEGORY_REGION + ", " +TAG_CATEGORY_SEX + ", "
                        + TAG_CATEGORY_AGE, Toast.LENGTH_LONG).show(); */

                dismiss();

                if (!TAG_CATEGORY_REGION.isEmpty() || !TAG_CATEGORY_SEX.isEmpty()) {

                    SharedPreferences preferences = getContext().getSharedPreferences("filter", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();

                    Intent intent = new Intent(getContext(), ComunityActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra("responseLocal", TAG_CATEGORY_REGION);
                    intent.putExtra("responseSex", TAG_CATEGORY_SEX);
                    intent.putExtra("responseAgeMin", TAG_CATEGORY_AGE_MIN);
                    intent.putExtra("responseAgeMax", TAG_CATEGORY_AGE_MAX);
                    intent.putExtra("responseCurrent", TAG_CATEGORY_CURRENT);
                    intent.putExtra("person", "person");

                    editor.putString("countryRegionPerson", TAG_CATEGORY_REGION);
                    editor.putBoolean("filterActivePerson", true);
                    editor.putString("sexPersonPerson", TAG_CATEGORY_SEX);
                    editor.putLong("ageMinPerson", TAG_CATEGORY_AGE_MIN);
                    editor.putLong("ageMaxPerson", TAG_CATEGORY_AGE_MAX);

                    editor.apply();

                    ((Activity) getContext()).finish();
                    ((Activity) getContext()).overridePendingTransition(0, android.R.anim.fade_out);
                    startActivity(intent);

                }
            }
        });
        tvIdade.setText("de " + 18 + " a " +80);
        TAG_CATEGORY_AGE_MIN = 18;
        TAG_CATEGORY_AGE_MAX = 80;

// set listener
        rangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                tvIdade.setText("de " + String.valueOf(minValue) + " a " + String.valueOf(maxValue));
                TAG_CATEGORY_AGE_MIN = (long) minValue;
                TAG_CATEGORY_AGE_MAX = (long) maxValue;
            }
        });

// set final value listener
        rangeSeekbar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                Log.d("CRS=>", String.valueOf(minValue) + " : " + String.valueOf(maxValue));
            }
        });

        /*
        seekBarIdade.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 18;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue , boolean fromUser) {
                progress = progressValue + 18;
                tvIdade.setText("Idade - ate " + progress + " anos");
                TAG_CATEGORY_AGE = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        }); */
        return view;
    }


}
