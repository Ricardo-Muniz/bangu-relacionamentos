package br.com.bangu_ao_vivo.bangu.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.bangu_ao_vivo.bangu.Controler.ControlerTips;
import br.com.bangu_ao_vivo.bangu.Dialog.DialogTips;
import br.com.bangu_ao_vivo.bangu.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class TipsActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context = this;
    private static int KEY_RESULT_POSITION_CONSTANT = 0;
    private ImageView ivBackground;
    private TextView tvTitleTop, tvNameEditor, tvWeek, tvTitleMain, tvTextMain;
    private CircleImageView ivEditorImagePofile;
    private CardView cvCurtir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        //inicializaçao de ids
        initializeIDS();

        int dataPosition = getIntent().getExtras().getInt("position", 0);
        KEY_RESULT_POSITION_CONSTANT = dataPosition;

        ControlerTips tips = new ControlerTips();
        tips.verifyPositionClickedAndConstructor(context, KEY_RESULT_POSITION_CONSTANT,
                ivBackground, tvTitleTop, tvNameEditor, tvWeek, tvTitleMain, tvTextMain, ivEditorImagePofile);

        cvCurtir.setOnClickListener(this);

    }

    public void initializeIDS () {
        ivBackground = findViewById(R.id.ivBackgorundTips);
        tvTitleTop = findViewById(R.id.tvTitleTopTips);
        tvNameEditor = findViewById(R.id.tvNameEditorTips);
        tvWeek = findViewById(R.id.tvWeekTips);
        tvTitleMain = findViewById(R.id.tvTitleTips);
        tvTextMain = findViewById(R.id.tvTextTips);
        ivEditorImagePofile = findViewById(R.id.ivEditorProfileTips);
        cvCurtir = findViewById(R.id.cvCurtir);
    }

    @Override
    public void onBackPressed() {
        Intent it = new Intent(TipsActivity.this, ProfileUserActivity.class);
        startActivity(it);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        TipsActivity.super.finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cvCurtir:
                SharedPreferences preferences = context.getSharedPreferences("intern", Context.MODE_PRIVATE);
                final int intern = preferences.getInt("tipsDialogResult", 0);
                if (intern == 0) {
                    DialogTips tips = new DialogTips(context);
                    tips.setCancelable(false);
                    tips.show();
                } else {

                }
                break;
        }
    }
}
