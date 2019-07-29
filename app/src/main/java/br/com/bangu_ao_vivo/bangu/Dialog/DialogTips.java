package br.com.bangu_ao_vivo.bangu.Dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import br.com.bangu_ao_vivo.bangu.R;

public class DialogTips extends AppCompatDialog {

    private TextView tvDialogConfirm, tvDialogCancel;
    private Switch sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_tips_support);

        tvDialogConfirm = findViewById(R.id.tvDialogConfirm);
        tvDialogCancel = findViewById(R.id.tvDialogCancel);
        sw = findViewById(R.id.swNaoExibir);

        tvDialogConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw.isChecked()) {
                    SharedPreferences prf = getContext().getSharedPreferences("intern", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prf.edit();
                    editor.putInt("tipsDialogResult", 2);
                    editor.apply();
                    dismiss();
                } else {
                    dismiss();
                }
            }
        });

        tvDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


    }

    public DialogTips(Context context) {
        super(context);
    }

    public DialogTips(Context context, int theme) {
        super(context, theme);
    }

    protected DialogTips(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
