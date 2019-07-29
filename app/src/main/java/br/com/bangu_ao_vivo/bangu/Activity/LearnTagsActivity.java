package br.com.bangu_ao_vivo.bangu.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.bangu_ao_vivo.bangu.Adapter.SliderAdapterTags;
import br.com.bangu_ao_vivo.bangu.R;

public class LearnTagsActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout mLayoutMain;
    private SliderAdapterTags sliderAdapterTags;
    private CheckBox cbxLembrarTags;
    private Context context = this;

    private TextView[] mDots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_tags);

        viewPager = findViewById(R.id.vpTags);
        mLayoutMain = findViewById(R.id.dotsLayout);

        sliderAdapterTags = new SliderAdapterTags(this);

        viewPager.setAdapter(sliderAdapterTags);

        addDotsIndicator(0);

        viewPager.addOnPageChangeListener(viewListener);


    }

    public void addDotsIndicator(int position) {

        mDots = new TextView[3];
        mLayoutMain.removeAllViews();

        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorGrayBg));

            mLayoutMain.addView(mDots[i]);

        }

        if (mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.colorMain));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
