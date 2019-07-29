package br.com.bangu_ao_vivo.bangu.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.com.bangu_ao_vivo.bangu.R;

public class SliderAdapterTags extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    private ImageView ivItens;
    private TextView tvTextTitle, tvTextDescription;

    public SliderAdapterTags(Context context) {
        this.context = context;
    }

    public int[] slideImages = {
            //iamgens que devo colocar
            R.drawable.personality,
            R.drawable.searching_tw,
            R.drawable.airplane_ic

    };

    public  String[] slideTextTop = {
            "Sua personalidade",
            "Pessoas vão te achar",
            "Crie a sua"
    };

    public  String[] slideTextDesc = {
            "Cada pessoa pode criar uma tag individualmente, dando oportunidade para iniciar uma nova era de buscas baseada nelas.",
            "Seu perfil vai conter todas as tags que um dia criou e as pessoas que tem algo em comum podem eventualmente pesquisar por uma tag como a sua.",
            "Em breve você poderá criar a sua propria tag e ser utilizada dentro da plataforma. Podem ser coisas como seu time, exemplo: 'flamengo'."
    };

    @Override
    public int getCount() {
        return slideTextTop.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.lay_slider_tags, container, false);

        ivItens = view.findViewById(R.id.ivTagsLearn);
        tvTextTitle = view.findViewById(R.id.tvTextTitleTags);
        tvTextDescription = view.findViewById(R.id.tvTexTagsLearn);

        ivItens.setImageResource(slideImages[position]);
        tvTextTitle.setText(slideTextTop[position]);
        tvTextDescription.setText(slideTextDesc[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
