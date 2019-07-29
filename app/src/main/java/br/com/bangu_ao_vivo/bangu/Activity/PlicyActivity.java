package br.com.bangu_ao_vivo.bangu.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import br.com.bangu_ao_vivo.bangu.R;

public class PlicyActivity extends AppCompatActivity {

    private WebView wbView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plicy);

        wbView = findViewById(R.id.webviewPolcy);

        wbView.loadUrl("https://deus-70ab8.firebaseapp.com");
        wbView.setBackgroundColor(Color.TRANSPARENT);
        wbView.getSettings().setJavaScriptEnabled(true);

        wbView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
    }
}
