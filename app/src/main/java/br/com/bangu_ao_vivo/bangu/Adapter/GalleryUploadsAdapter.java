package br.com.bangu_ao_vivo.bangu.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import br.com.bangu_ao_vivo.bangu.Activity.GalleryActivity;
import br.com.bangu_ao_vivo.bangu.Model.ModelNewItemGallery;
import br.com.bangu_ao_vivo.bangu.R;

public class GalleryUploadsAdapter extends RecyclerView.Adapter<GalleryUploadsAdapter.ViewHolder> {

    List<ModelNewItemGallery> values;
    Context context;

    FirebaseUser fuser;

    public GalleryUploadsAdapter(List<ModelNewItemGallery> values, Context context) {
        this.values = values;
        this.context = context;
    }

    @NonNull
    @Override
    public GalleryUploadsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_gallery, parent,
                false);
        return new GalleryUploadsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ModelNewItemGallery gallery = values.get(position);

        Glide.with(context)
                .load(gallery.getImageUrl())
                .asBitmap()
                .into(holder.ivImageGallery);

        if (gallery.isStatus()) {
            holder.lock.setVisibility(View.VISIBLE);
            holder.viewLock.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, holder.itemView);
                //inflating menu from xml resource
                popup.inflate(R.menu.menu_gallery);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.private_menu:
                                //handle menu1 click
                                String itemidone = gallery.getUid();
                                dialogPrivate(itemidone);
                                return true;
                            case R.id.eclude_menu:
                                //handle menu2 click
                                String itemid = gallery.getUid();
                                dialogDelete(itemid);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();
                return false;
            }
        });

    }

    private void dialogDelete(String uid) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create(); //Read Update
        alertDialog.setTitle("Quer excluir a foto?");
        alertDialog.setMessage("você vai mesmo excluir essa linda foto da sua galeria Bangu :(");
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteItem(uid);
            }
        });

        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }

    private void dialogPrivate(String uid) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create(); //Read Update
        alertDialog.setTitle("Quer privar a foto?");
        alertDialog.setMessage("essa ação é permanente, então não poderá ser modificada.");
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finalizePrivate(uid);
            }
        });

        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }

    private void finalizePrivate(String uid) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference refTwo = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference dataTwo = refTwo.child("galeries-geral").child(fuser.getUid());
        dataTwo.child(uid).child("status").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(context, GalleryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });
    }

    private void deleteItem(String item) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference()
                .child("galeries-geral").child(fuser.getUid()).child(item).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(context, GalleryActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            context.startActivity(intent);
                            ((Activity) context).finish();
                        } else {

                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImageGallery;
        private ImageView lock;
        private View viewLock;

        public ViewHolder(View itemView) {
            super(itemView);

            ivImageGallery = itemView.findViewById(R.id.ivImageGallery);
            lock = itemView.findViewById(R.id.ivLock);
            viewLock = itemView.findViewById(R.id.viewGray);

        }
    }
}
