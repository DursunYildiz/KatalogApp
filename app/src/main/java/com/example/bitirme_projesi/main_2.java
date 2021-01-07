package com.example.bitirme_projesi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGSaver;

public class main_2 extends AppCompatActivity {
    EditText editText;
    Button giris,gallery;
    private CodeScanner mCodeScanner;
    FirebaseDatabase firebaseDatabase;
    List<String> childlist;
    DatabaseReference dinamicref;
    ProgressDialog progressDialog;
    private AppCompatActivity activity;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);

        SwitchCompat switchCompat;
        switchCompat=findViewById(R.id.switch1);


        progressDialog = new ProgressDialog(main_2.this);
       giris = findViewById(R.id.button5);
       gallery = findViewById(R.id.gallery);
        editText=(EditText) findViewById(R.id.editTextt);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);


        firebaseDatabase = FirebaseDatabase.getInstance();

        activity=this;
        childlist = new ArrayList<>();
        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchCompat.isChecked()) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        scannerView.setVisibility(View.VISIBLE);
                        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                            gallery.setVisibility(View.VISIBLE);
                        }
                        else {
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

                        }

                    } else
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 0);


                }
                else scannerView.setVisibility(View.INVISIBLE);
            }
        });


        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1000);
            }
        });





        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(main_2.this, result.getText(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), naw.class);
                        intent.putExtra("id", result.getText());
                        progressDialog.setMessage("Giriş yapılıyor");
                        progressDialog.show();









                        childlist.clear();


                        dinamicref=FirebaseDatabase.getInstance().getReference(result.getText());


                        dinamicref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot:snapshot.getChildren()){

                                     
                                    childlist.add(dataSnapshot.getKey());
                                    intent.putExtra("category1", childlist.get(0));

                                    startActivity(intent);
                                    progressDialog.dismiss();



                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });





                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });

        giris.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String id =editText.getText().toString().trim();

          Intent intent = new Intent(v.getContext(), naw.class);
            intent.putExtra("id", id);


               progressDialog.setMessage("Giriş yapılıyor");
               progressDialog.show();
               dinamicref=FirebaseDatabase.getInstance().getReference(id);
               childlist.clear();
                     if (dinamicref==null){
                         Toast.makeText(main_2.this, "hata", Toast.LENGTH_SHORT).show();
                         progressDialog.dismiss();
                     }


               dinamicref.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                           childlist.add(dataSnapshot.getKey());

                           intent.putExtra("category1", childlist.get(0));

                           startActivity(intent);
                            progressDialog.dismiss();
                       }



                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });












           }
       });

    }
    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }


    @Override

    protected void onActivityResult(int reqCode, int resultCode, Intent data) {

        super.onActivityResult(reqCode, resultCode, data);





        if (resultCode == RESULT_OK) {

            try {

                final Uri imageUri = data.getData();

                final InputStream imageStream = getContentResolver().openInputStream(imageUri);

                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                try {

                    Bitmap bMap = selectedImage;

                    String contents = null;



                    int[] intArray = new int[bMap.getWidth()*bMap.getHeight()];

                    bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());



                    LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);

                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));



                    Reader reader = new MultiFormatReader();

                    Result result = reader.decode(bitmap);

                    contents = result.getText();

                    childlist.clear();

                    Intent intent = new Intent(getApplicationContext(), naw.class);
                    intent.putExtra("id", contents);
                    progressDialog.setMessage("Giriş yapılıyor");
                    progressDialog.show();
                    dinamicref=FirebaseDatabase.getInstance().getReference(contents);


                    dinamicref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot:snapshot.getChildren()){


                                childlist.add(dataSnapshot.getKey());
                                intent.putExtra("category1", childlist.get(0));

                                startActivity(intent);
                                progressDialog.dismiss();



                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                }catch (Exception e){

                    e.printStackTrace();

                }

                //  image_view.setImageBitmap(selectedImage);

            } catch (FileNotFoundException e) {

                e.printStackTrace();



            }



        }else {

            Toast.makeText(main_2.this, "Fotoğraf Seçmediniz!",Toast.LENGTH_LONG).show();

        }

    }







}