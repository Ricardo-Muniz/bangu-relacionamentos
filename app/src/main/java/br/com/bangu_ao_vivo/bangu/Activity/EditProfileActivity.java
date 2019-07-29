package br.com.bangu_ao_vivo.bangu.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import br.com.bangu_ao_vivo.bangu.R;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static String CONSTANT_SEX = "";
    private static String BIRTHDAY_CONSTANT = "";
    private static int AGE = 0;
    private EditText edtName, edtDesc, edtProfissional, edtFun;
    private TextView tvWelcome, tvAgeUser;
    private CardView cvCreateTagUser, cvChangeAge;
    private RadioGroup radioGroupSex;
    private RadioButton radioButton;
    private FirebaseUser fuser;
    private RadioButton radioButtonMan, radioButtonGirl;

    private LinearLayout fabSave;
    private ProgressDialog pd;

    private DatePickerDialog datePicker;
    private Calendar calendar;

    CoordinatorLayout coordinatorLayout;

    private Context context = this;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //initializeidUser
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        //inicializa ids
        incializeIDS();

        //incializao da leitura do texto de nome inputado
        textRealTime();

        //inicializa firebaeapp
        incializeFirebaseApp();

        //inicializa texto com o nome do usuario
        inicializeTextNameUser();

        pd = new ProgressDialog(this);
        pd.setMessage("enviando...");

        radioGroupSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton button = (RadioButton) group.findViewById(checkedId);
                if (button != null) {
                    if (button.getId() == R.id.radioButtonMan) {
                        if (!CONSTANT_SEX.equals("Homem")) {
                            updateSex("Homem");
                        }
                    } else if (button.getId() == R.id.radioButtonGirl) {
                        if (!CONSTANT_SEX.equals("Mulher")) {
                            updateSex("Mulher");
                        }
                    }
                }
            }
        });

        loadRadios();

        fabSave.setOnClickListener(this);
        cvCreateTagUser.setOnClickListener(this);
        cvChangeAge.setOnClickListener(this);

    }

    private void loadRadios () {
        SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        String sex = preferences.getString("sex", "");

        if (!sex.isEmpty()) {
            if (sex.equals("Homem")) {
             radioButtonMan.setChecked(true);
            }
        }
        if (!sex.isEmpty()) {
            if (sex.equals("Mulher")) {
            radioButtonGirl.setChecked(true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent it = new Intent(EditProfileActivity.this, ProfileUserActivity.class);
        startActivity(it);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        EditProfileActivity.super.finish();
    }

    private void updateSex(String newSex) {

        SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference childExtern = ref.child("user").child(fuser.getUid()).child("sex");

        childExtern.setValue(newSex).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                editor.putString("sex", newSex);
                editor.apply();
                View view = findViewById(R.id.layMainEditProfile);
                Snackbar snackbar = Snackbar.make(view, "Sexo alterado.", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                editor.putString("sex", newSex);
                editor.apply();
                View view = findViewById(R.id.layMainEditProfile);
                Snackbar snackbar = Snackbar.make(view, "Ops! algo deu errado, tente mais tarde.", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });
    }

    private void changeYearBirth() {
        calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        datePicker = new DatePickerDialog(EditProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {
                //date
                tvAgeUser.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
                BIRTHDAY_CONSTANT = mDay + "/" + (mMonth + 1) + "/" + mYear;
                AGE = mYear;

                Calendar cal = Calendar.getInstance();
                int yearOne = cal.get(Calendar.YEAR);
                int age = yearOne - AGE;

                SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                SharedPreferences preferencesResult = context.getSharedPreferences("login", Context.MODE_PRIVATE);
                final String uid = preferencesResult.getString("id", "");

                editor.putString("birthday", BIRTHDAY_CONSTANT);
                editor.apply();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                final DatabaseReference childExtern = ref.child("user").child(uid).child("age");

                childExtern.setValue(age).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        View view = findViewById(R.id.layMainEditProfile);
                        Snackbar snackbar = Snackbar.make(view, "Aniversário alterado.", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        View view = findViewById(R.id.layMainEditProfile);
                        Snackbar snackbar = Snackbar.make(view, "Ops! algo deu errado, tente mais tarde.", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                });
            }

        }, day, month, year);
        datePicker.show();

    }

    private void inicializeTextNameUser() {

        SharedPreferences preferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        final String uid = preferences.getString("id", "");
        final String birthday = preferences.getString("birthday", "");
        if (birthday.equals("")) {
            tvAgeUser.setText("DD/MM/YY");
        } else {
            tvAgeUser.setText(birthday);
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference child = ref.child("user").child(uid).child("name");
        child.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name;
                name = (String) dataSnapshot.getValue();
                int count = name.length();
                Spannable spannable = new SpannableString(name + ", agora você está querendo editar seu perfil, vê se capricha em!");
                spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorMain)), 0,
                        count, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvWelcome.setText(spannable);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference reftwo = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference childExtern = reftwo.child("user").child(fuser.getUid()).child("sex");

        childExtern.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CONSTANT_SEX = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void incializeFirebaseApp() {
        FirebaseApp.initializeApp(context);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void incializeIDS() {
        edtName = findViewById(R.id.edtEditProfName);
        tvWelcome = findViewById(R.id.tvEditProfWelcome);
        fabSave = findViewById(R.id.fabSaveDataEditProfile);
        edtDesc = findViewById(R.id.edtDesc);
        edtProfissional = findViewById(R.id.edtProfissional);
        edtFun = findViewById(R.id.edtFun);
        cvCreateTagUser = findViewById(R.id.cvCreateTag);
        tvAgeUser = findViewById(R.id.tvAgeUser);
        cvChangeAge = findViewById(R.id.cvChangeAge);
        radioGroupSex = findViewById(R.id.radioGroupSex);
        radioButtonGirl = findViewById(R.id.radioButtonGirl);
        radioButtonMan = findViewById(R.id.radioButtonMan);
    }

    private void textRealTime() {
        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
               /* String inputText = edtName.getText().toString();
                int cont = edtName.length();

                Spannable spannable = new SpannableString(inputText + ", agora você está querendo editar seu perfil, vê se capricha em!");
                spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorMain)), 0,
                        cont, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvWelcome.setText(spannable); */
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtName.length() != 0) {
                    String inputText = edtName.getText().toString();
                    int cont = edtName.length();

                    Spannable spannable = new SpannableString(inputText + ", agora você está querendo editar seu perfil, vê se capricha em!");
                    spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorMain)), 0,
                            cont, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvWelcome.setText(spannable);

                } else {
                    inicializeTextNameUser();
                }
            }
        });

    }

    private void validateInputsAndSave(View view) {

        SharedPreferences preferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        final String uid = preferences.getString("id", "");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference childExtern = ref.child("user").child(uid).child("name");
        final DatabaseReference childDesc = ref.child("user").child(uid).child("extra").child("description");
        final DatabaseReference childProfissional = ref.child("user").child(uid).child("extra").child("profissional");
        final DatabaseReference childFun = ref.child("user").child(uid).child("extra").child("fun");


        String newName = edtName.getText().toString();
        String newDesc = edtDesc.getText().toString();
        String newProfissional = edtProfissional.getText().toString();
        String newFun = edtFun.getText().toString();

        pd.show();
        if (!childExtern.toString().equals(newName) && !newName.equals("")) {
            final DatabaseReference childEx = ref.child("user").child(uid).child("name");

            childEx.setValue(newName).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    pd.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                }
            });
            // ModelEditProfile editProfile = new ModelEditProfile();
            final DatabaseReference child = ref.child("user").child(uid).child("extra").child("name");
            // editProfile.setName(newName);
            child.setValue(newName).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    pd.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                }
            });
        }
        if (!childDesc.toString().equals(newDesc) && !newDesc.equals("")) {
            // ModelEditProfile editProfile = new ModelEditProfile();
            final DatabaseReference child = ref.child("user").child(uid).child("extra").child("description");
            // editProfile.setDescription(newDesc);
            child.setValue(newDesc).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    pd.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                }
            });
        }
        if (!childProfissional.toString().equals(newProfissional) && !newProfissional.equals("")) {
            // ModelEditProfile editProfile = new ModelEditProfile();
            final DatabaseReference child = ref.child("user").child(uid).child("extra").child("profissional");
            // editProfile.setDescription(newDesc);
            child.setValue(newProfissional).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    pd.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                }
            });
        }
        if (!childFun.toString().equals(newFun) && !newFun.equals("")) {
            // ModelEditProfile editProfile = new ModelEditProfile();
            final DatabaseReference child = ref.child("user").child(uid).child("extra").child("fun");
            // editProfile.setDescription(newDesc);
            child.setValue(newFun).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    pd.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                }
            });
        }


        SharedPreferences prf = getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prf.edit();

        editor.putString("name", newName);
        editor.apply();

        snackSaveControler(view, newName, newDesc, newProfissional, newFun);

        pd.dismiss();

    }

    private boolean verifyConexectivity() {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null && conectivtyManager
                .getActiveNetworkInfo().isAvailable() && conectivtyManager
                .getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }

    private void snackSaveControler(View view, String name, String desc, String profissional, String fun) {

        if (!verifyConexectivity()) {
            Snackbar snackbar = Snackbar.make(view, "Erro na conexão.", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else if (!name.equals("") || !desc.equals("") || !profissional.equals("") || !fun.equals("")) {
            Snackbar snackbar = Snackbar.make(view, "Novos dados salvos.", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else {
            Snackbar snackbar = Snackbar.make(view, "Sem dados a salvar.", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabSaveDataEditProfile:
                View view = findViewById(R.id.layMainEditProfile);
                validateInputsAndSave(view);
                break;
            case R.id.cvCreateTag:
                Intent it = new Intent(EditProfileActivity.this, LearnTagsActivity.class);
                startActivity(it);
                break;

            case R.id.cvChangeAge:
                changeYearBirth();
                break;
        }
    }
}
