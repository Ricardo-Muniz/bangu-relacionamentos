package br.com.bangu_ao_vivo.bangu.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.bangu_ao_vivo.bangu.ComunityActivity;
import br.com.bangu_ao_vivo.bangu.R;

public class NewSessionLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView cvEntrarLogin;
    private EditText edtEmailSig, edtPassSig;

    private Context context = this;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_session_login);

        //inicializaçao de widgets, layouts e etc...
        initializeIDS();

        //inicalizaçao do login. especificamente a comunicaçao entre o servidor
        mAuth = FirebaseAuth.getInstance();

        //incializaçao de clique de componente visual
        cvEntrarLogin.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        Intent it = new Intent(NewSessionLoginActivity.this, LoginActivity.class);
        startActivity(it);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        NewSessionLoginActivity.super.finish();
    }

    public void initializeIDS() {
        cvEntrarLogin = findViewById(R.id.cvEntrarLogin);
        edtEmailSig = findViewById(R.id.edtEmailSig);
        edtPassSig = findViewById(R.id.edtPassSig);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Entrando...");
        progressDialog.setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        String emailValidate = edtEmailSig.getText().toString();
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher m = p.matcher(emailValidate);
        boolean matchFound = m.matches();

        switch (v.getId()) {
            case R.id.cvEntrarLogin:
                String email = edtEmailSig.getText().toString();
                String password = edtPassSig.getText().toString();

                if (!verifyConexectivity()) {
                    View view = findViewById(R.id.layNewSession);
                    Snackbar snackbar = Snackbar.make(view, "Erro na conexão.", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else if (email.equals("")) {
                    edtEmailSig.requestFocus();
                    edtEmailSig.setError("Campo Obritaorio.");
                } else if (password.equals("")) {
                    edtPassSig.requestFocus();
                    edtPassSig.setError("Campo Obritaorio.");
                } else if (!matchFound) {
                    edtPassSig.requestFocus();
                    edtPassSig.setError("Email invalido.");
                } else {
                    signInUser(email, password);
                }

                break;
        }
    }

    public void signInUser(String email, String password) {
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = user.getUid();

                            SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();

                            String status = "complete";

                            editor.putString("id", uid);
                            editor.putString("statusLogin", status);
                            editor.apply();

                            progressDialog.dismiss();

                            Intent intent = new Intent(NewSessionLoginActivity.this,
                                    ComunityActivity.class);
                            startActivity(intent);
                            NewSessionLoginActivity.super.finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(context, "Falha na autenticação.",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
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
}
