package com.example.bitirme_projesi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class qr extends AppCompatActivity {

    private ImageView qrImage;
    private String inputValue;

    private Bitmap bitmap;
    private QRGEncoder qrgEncoder;
    TextView textView;
    private AppCompatActivity activity;
    DatabaseReference databaseReference;
    public final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        qrImage = findViewById(R.id.qr_image);
        textView = findViewById(R.id.textViewqr);
        textView.setVisibility(View.INVISIBLE);
        activity = this;



                final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                   databaseReference = FirebaseDatabase.getInstance().getReference("admins").child(firebaseUser.getUid());
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot:snapshot.getChildren()){

                                inputValue = dataSnapshot.getValue().toString();
                                textView.setText("Katalog Kodu: "+inputValue);
                                textView.setVisibility(View.VISIBLE);
                                if (inputValue.length() > 0) {
                                    WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                                    Display display = manager.getDefaultDisplay();
                                    Point point = new Point();
                                    display.getSize(point);
                                    int width = point.x;
                                    int height = point.y;
                                    int smallerDimension = width < height ? width : height;
                                    smallerDimension = smallerDimension * 3 / 4;

                                    qrgEncoder = new QRGEncoder(
                                            inputValue, null,
                                            QRGContents.Type.TEXT,
                                            smallerDimension);
                                    qrgEncoder.setColorBlack(Color.BLACK);
                                    qrgEncoder.setColorWhite(Color.WHITE);
                                    try {
                                        bitmap = qrgEncoder.getBitmap();
                                        qrImage.setImageBitmap(bitmap);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
















    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.share :

             İmageshare();


                break;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.qr_menu, menu);

        MenuItem share=menu.findItem(R.id.share);









        return super.onCreateOptionsMenu(menu);
    }
    private  void İmageshare(){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        BitmapDrawable drawable = (BitmapDrawable) qrImage.getDrawable();
        Bitmap bitmap1 = drawable.getBitmap();
        File file = new File(getExternalCacheDir()+"/"+"Katalog Bilgileri.png");
           Intent shareintent;
           try {
               FileOutputStream outputStream = new FileOutputStream(file);
               bitmap1.compress(Bitmap.CompressFormat.PNG,100,outputStream);
               outputStream.flush();
               outputStream.close();
               shareintent = new Intent();

               shareintent.setAction(Intent.ACTION_SEND);
               shareintent.setType("image/text");
               shareintent.putExtra(Intent.EXTRA_TEXT,inputValue);
               shareintent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(file));
               shareintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

           }
           catch (Exception exception){
               throw new RuntimeException(exception);
           }
              startActivity(Intent.createChooser(shareintent,"share image"));
    }
}