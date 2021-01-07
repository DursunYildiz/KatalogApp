package com.example.bitirme_projesi;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class detail extends AppCompatActivity {
  TextView misim , maçıklama,textarih;
  ImageView ımageView;
  Button önce,sonra;
  ArrayList<String>arrayList;
  int posizyon = 0 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Detaylar");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
         misim =(TextView)findViewById(R.id.misim);
        maçıklama =(TextView)findViewById(R.id.textView4);
        ımageView =(ImageView)findViewById(R.id.image51);
        textarih=(TextView) findViewById(R.id.textView2);
        önce=findViewById(R.id.buttonönce);
        sonra = findViewById(R.id.buttonsonra);
        arrayList = new ArrayList<>();
        String isim = getIntent().getStringExtra("isim");

        String tarih=getIntent().getStringExtra("tarih");
        String image= getIntent().getStringExtra("image");

         String açıklama=getIntent().getStringExtra("açıklama");
         String image2=getIntent().getStringExtra("image2");
         String image3=getIntent().getStringExtra("image3");
         String image4=getIntent().getStringExtra("image4");
         String image5=getIntent().getStringExtra("image5");
          arrayList.add(image);
         if (image2!=null){
             arrayList.add(image2);

         }
        if (image3!=null){
            arrayList.add(image3);

        }
        if (image4!=null){
            arrayList.add(image4);

        }
        if (image5!=null){
            arrayList.add(image5);

        }


        misim.setText("İsim:  "+isim);

        maçıklama.setText("Açıklama: "+açıklama);
        textarih.setText("Eklenme Zamanı: "+tarih);

        Picasso.get().load(image).into(ımageView);


        önce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (posizyon>0){
                    posizyon -- ;

                    Picasso.get().load(arrayList.get(posizyon)).into(ımageView);
                }
                else Toast.makeText(detail.this, "Önceki Fotoğraf Yok", Toast.LENGTH_SHORT).show();
            }
        });


        sonra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (posizyon<arrayList.size()-1){
                  posizyon++;
                    Picasso.get().load(arrayList.get(posizyon)).into(ımageView);


                }
                else Toast.makeText(detail.this, "Başka Fotoğraf Yok", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
