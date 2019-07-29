package br.com.bangu_ao_vivo.bangu.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import br.com.bangu_ao_vivo.bangu.Model.ModelGalleryUserDisplay;
import br.com.bangu_ao_vivo.bangu.R;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    List<ModelGalleryUserDisplay> values;
    private Context context;

    public GalleryAdapter(List<ModelGalleryUserDisplay> values, Context context) {
        this.values = values;
        this.context = context;
    }

    @NonNull
    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_gallery_user, parent,
                false);
        return new GalleryAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryAdapter.ViewHolder holder, int position) {
        final ModelGalleryUserDisplay gallery = values.get(position);
        Glide.with(context)
                .load(gallery.getUrlImage())
                .asBitmap()
                .centerCrop()
                .into(holder.ivPersonGallery);
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivPersonGallery;

        public ViewHolder(View itemView) {
            super(itemView);

            ivPersonGallery = itemView.findViewById(R.id.ivPersonGallery);
        }
    }
}
