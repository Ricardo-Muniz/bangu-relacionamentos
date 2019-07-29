package br.com.bangu_ao_vivo.bangu.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.SecureRandom;

import br.com.bangu_ao_vivo.bangu.Model.ModelReportUser;
import br.com.bangu_ao_vivo.bangu.R;
import br.com.bangu_ao_vivo.bangu.Utils.RandomString;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener {

    private static String STRING_VALUE = "";
    private static boolean VALUE_BOOLEAN_BLOCK = false;
    private static String KEY_USER = "";
    private CheckBox cbBlockUser;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private LinearLayout layOk, layReturn;

    private Context context = this;
    private Intent intent;

    private FirebaseUser fuser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        intent = getIntent();
        KEY_USER = intent.getStringExtra("userReport");

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        inicializeIDS();
        inicializeFirebase();

        layOk.setOnClickListener(this);
        layReturn.setOnClickListener(this);

    }

    private void inicializeIDS() {
        layOk = findViewById(R.id.layOk);
        radioGroup = findViewById(R.id.radioGroupReport);
        cbBlockUser = findViewById(R.id.cbBlock);
        layReturn = findViewById(R.id.layReturnReport);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layOk:
                reportUser();
                break;
            case R.id.layReturnReport:
                ReportActivity.super.finish();
                break;
        }
    }

    private void inicializeFirebase () {
        FirebaseApp.initializeApp(context);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();
        reference.keepSynced(true);
    }

    private void reportUser () {
        int radioID = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioID);

        int checkedId = ((RadioGroup) findViewById(R.id.radioGroupReport)).getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.radioOne:
               STRING_VALUE = "sem denuncia";
                break;
            case R.id.radioTwo:
                STRING_VALUE = "imagens improprias";
                break;
            case R.id.radioThree:
                STRING_VALUE = "utilização de perfil copiado/fake";
               break;
            case R.id.radioFour:
                STRING_VALUE = "venda ou propostas que infrigem a politica";
                break;
            case R.id.radioFive:
                STRING_VALUE = "denunciar spam";
                break;

        }

        if (checkedId != 0) {
            if (cbBlockUser.isChecked()){
                VALUE_BOOLEAN_BLOCK = true;
            } else {
                VALUE_BOOLEAN_BLOCK = false;
            }
            String easy = RandomString.digits + "ACEFGHJKLMNPQRUVWXYabcdefhijkprstuvwx";
            RandomString tks = new RandomString(23, new SecureRandom(), easy);
            String rand = tks.nextString();

            ModelReportUser report = new ModelReportUser();
            report.setUidReported(KEY_USER);
            report.setUidSender(fuser.getUid());
            report.setBlockOption(VALUE_BOOLEAN_BLOCK);
            report.setMotivation(STRING_VALUE);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference reportDatabase = reference.child("reported");

            reportDatabase.child(rand).setValue(report).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ReportActivity.this.finish();
                    Toast.makeText(context, "Tivemos um problema ao receber dados.", Toast.LENGTH_LONG).show();
                }
            });

            if (VALUE_BOOLEAN_BLOCK) {
                ModelReportUser user = new ModelReportUser();
                user.setUidReported(KEY_USER);
                user.setBlockOption(VALUE_BOOLEAN_BLOCK);

                DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference();
                final DatabaseReference reportDatabaseUser = referenceUser.child("user").child(fuser.getUid());

                reportDatabaseUser.child("blackList").child(KEY_USER).setValue(user).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ReportActivity.this.finish();
                        Toast.makeText(context, "Tivemos um problema ao receber dados.", Toast.LENGTH_LONG).show();
                    }
                });
            }
            ReportActivity.this.finish();
            Toast.makeText(context, "Recebemos sua denuncia, em breve tomaremos providencias sobre.", Toast.LENGTH_LONG).show();


        } else {
            Toast.makeText(context, "necessario", Toast.LENGTH_LONG).show();
        }

    }



}
