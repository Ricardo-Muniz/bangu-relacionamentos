package br.com.bangu_ao_vivo.bangu.Controler;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class ControlerSignOut extends Activity {


    public void signOutUser (TextView tvExit, Context context) {
        tvExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
