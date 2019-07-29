package br.com.bangu_ao_vivo.bangu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import br.com.bangu_ao_vivo.bangu.Activity.PhotoViewerActivity;
import br.com.bangu_ao_vivo.bangu.Model.ModelNewItemGallery;
import br.com.bangu_ao_vivo.bangu.Model.ModelRequest;
import br.com.bangu_ao_vivo.bangu.R;

public class VisitGalleryAdapter extends PagerAdapter {

    Context context;
    List<ModelNewItemGallery> galleryList;
    LayoutInflater inflater;

    FirebaseUser fuser;

    public VisitGalleryAdapter() {
    }

    public VisitGalleryAdapter(Context context, List<ModelNewItemGallery> galleryList) {
        this.context = context;
        this.galleryList = galleryList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return galleryList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager)container).removeView((View)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        //inflate view
        View view = inflater.inflate(R.layout.line_gallery_user, container, false);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        //view
        ImageView galleryImage = view.findViewById(R.id.ivPersonGallery);
        ImageView imageBlur = view.findViewById(R.id.ivBlr);
        LinearLayout layOver = view.findViewById(R.id.layOverlayLock);
        View viewOver = view.findViewById(R.id.viewGray);
        LinearLayout layRequestAcess = view.findViewById(R.id.layRequestAcess);

        if (galleryList.get(position).isStatus()) {
            layOver.setVisibility(View.VISIBLE);
            viewOver.setVisibility(View.VISIBLE);
            imageBlur.setVisibility(View.VISIBLE);
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference data = ref.child("user").child(fuser.getUid()).child("permited");
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    if (dataSnapshot.child(galleryList.get(position).getUserID()).exists()) {
                        layOver.setVisibility(View.GONE);
                        viewOver.setVisibility(View.GONE);
                        imageBlur.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //set date
        Glide.with(context).load(galleryList.get(position).getImageUrl()).into(galleryImage);

        container.addView(view);

        galleryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PhotoViewerActivity.class);
                intent.putExtra("image", galleryList.get(position).getImageUrl());
                context.startActivity(intent);
            }
        });

        layRequestAcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                final DatabaseReference data = ref.child("user").child(galleryList.get(position).getUserID()).child("requests");

                ModelRequest request = new ModelRequest();
                request.setUid(fuser.getUid());
                request.setState(true);

                data.child(fuser.getUid()).setValue(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Solicitação enviada", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        return view;
    }

    private void sendRequest () {

    }
}
