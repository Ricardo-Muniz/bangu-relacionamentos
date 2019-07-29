package br.com.bangu_ao_vivo.bangu.Dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.bangu_ao_vivo.bangu.Model.ModelMatch;
import br.com.bangu_ao_vivo.bangu.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Toshiba pc on 20/03/2019.
 */

@SuppressLint("ValidFragment")
public class ModalMath extends BottomSheetDialogFragment {

    TextView tvNameInfo;
    CircleImageView cvUserMatch;

    ImageView ivLike;

    String name, url, uid;
    Context context = getActivity();


    public ModalMath(String name, String url, String uid) {
        this.name = name;
        this.url = url;
        this.uid = uid;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference mostafa = ref.child("user").child(uid).child("match");

        ModelMatch match = new ModelMatch();
        String mName = "";
        String mUid = "";
        String mUrl = "";
        String mState = "";

        match.setName(mName);
        match.setId(mUid);
        match.setImgUrl(mUrl);

        mostafa.setValue(match);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = getLayoutInflater().inflate(R.layout.lay_bottom_modal, null);

        tvNameInfo = view.findViewById(R.id.tvInfoModal);
        cvUserMatch = view.findViewById(R.id.cvModal);

        tvNameInfo.setText(name + ", quer se conectar com você. Agora vocês podem conversar.");
        Glide.with(getContext())
                .load(url)
                .asBitmap()
                .centerCrop()
                .into(cvUserMatch);




        return view;
    }
}

