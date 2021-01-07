package com.example.bitirme_projesi;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class Göster extends RecyclerView.ViewHolder {
    View mview;
    public Göster(@NonNull View itemView) {
        super(itemView);
        mview= itemView;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mclick.Onitemclick(view,getAdapterPosition());
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mclick.onitemlongclick(view,getAdapterPosition());
                return true;
            }
        });
    }
    public void  setdateail (Context ctx,String İsim ,String İd,String image,String Açıklama , String Tarih,String image2,String image3,String image4,
                             String image5  ){
        TextView isim = mview.findViewById(R.id.isim);
        TextView açıklama = mview.findViewById(R.id.açıklama);
        ImageView  ımageView = mview.findViewById(R.id.image50);
        isim.setText(İsim);
        açıklama.setText(Açıklama);
        Picasso.get().load(image).into(ımageView);
    }
    private onclickListener mclick;
    public interface onclickListener{
        void Onitemclick(View view, int position);
        void onitemlongclick(View view, int position);
    }
    public void setonclick(onclickListener onclickListener){
        mclick = onclickListener;

    }
}
