package br.com.bangu_ao_vivo.bangu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import br.com.bangu_ao_vivo.bangu.Activity.ChatProfileActivity;
import br.com.bangu_ao_vivo.bangu.Model.ModelVisits;
import br.com.bangu_ao_vivo.bangu.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class VisitsAdapter extends RecyclerView.Adapter<VisitsAdapter.ViewHolder> {

    Context context;
    List<ModelVisits> visits;

    public VisitsAdapter(Context context, List<ModelVisits> visits) {
        this.context = context;
        this.visits = visits;
    }

    @NonNull
    @Override
    public VisitsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_view_profile, parent,
                false);
        return new VisitsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VisitsAdapter.ViewHolder holder, int position) {

        ModelVisits vis = visits.get(position);
        holder.tvName.setText(vis.getName());
        Glide.with(context)
                .load(vis.getImageUrl())
                .asBitmap()
                .into(holder.ivPerson);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatProfileActivity.class);
                intent.putExtra("user", vis.getUid());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return visits.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private CircleImageView ivPerson;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvNameUserVisit);
            ivPerson = itemView.findViewById(R.id.ivProfileVisits);
        }
    }
}
