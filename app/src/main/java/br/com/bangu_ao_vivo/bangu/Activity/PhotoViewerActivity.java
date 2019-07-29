package br.com.bangu_ao_vivo.bangu.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import br.com.bangu_ao_vivo.bangu.R;

public class PhotoViewerActivity extends AppCompatActivity {

    private Context context = this;
    private ImageView ivImageDetail;
    private LinearLayout layExitImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        inicializeIDS();

        String url = getIntent().getStringExtra("image");
        Glide.with(context)
                .load(url)
                .asBitmap()
                .centerCrop()
                .into(ivImageDetail);

        layExitImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoViewerActivity.super.finish();
            }
        });

    }

    private void inicializeIDS () {
        ivImageDetail = findViewById(R.id.ivImageDetail);
        layExitImage = findViewById(R.id.layExitImage);
    }
}
