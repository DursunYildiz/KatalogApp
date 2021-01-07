package com.example.bitirme_projesi;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class ekleme2 extends AppCompatActivity {

       private ImageView foto;


    String imgurl,imgurl2,imgurl3,imgurl4,imgurl5;
       private EditText editTextisim,editTextaciklama,editTextkategory;
    Uri FilePathUri;
    StorageReference storageReference;
    DatabaseReference databaseReference,adminref;

    ProgressDialog progressDialog ;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private int upload_count = 0;
    ArrayList<Uri> FileList = new ArrayList<Uri>();
    private static final int PICK_IMG = 1;
    private Uri FileUri;
    ArrayList<String> arrayList= new ArrayList<>();
    ArrayList<String>arrayListara=new ArrayList<>();
    Button önce,sonra;
    int posizyon = 0 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.urun_ekleme);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Kayıt Ekranı");
        Button butonkaydet = (Button) findViewById(R.id.button7);
         önce = (Button) findViewById(R.id.buttonönce2);
         sonra = (Button) findViewById(R.id.buttonsonra2);

        foto=(ImageView)findViewById(R.id.imageView2);
        editTextaciklama=(EditText)findViewById(R.id.editText3);

        editTextisim=(EditText) findViewById(R.id.editTextisim);
        editTextkategory=(EditText) findViewById(R.id.editTextTextPersonName);
        storageReference = FirebaseStorage.getInstance().getReference("fotoğraflar");

        int count =0;

        önce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (posizyon>0){
                    posizyon -- ;
                    foto.setImageURI(FileList.get(posizyon));

                }
                else Toast.makeText(ekleme2.this, "Önceki Fotoğraf Yok", Toast.LENGTH_SHORT).show();
            }
        });
        sonra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (posizyon<FileList.size()-1){
                    posizyon++;
                    foto.setImageURI(FileList.get(posizyon));


                }
                else Toast.makeText(ekleme2.this, "Başka Fotoğraf Yok", Toast.LENGTH_SHORT).show();
            }
        });
        progressDialog = new ProgressDialog(ekleme2.this);

foto.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_IMG);
    }
});
butonkaydet.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        upld();
    }
});

           }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMG){

            if(resultCode == RESULT_OK){

                if(data.getClipData() != null){



                    int countClipData = data.getClipData().getItemCount();



                    int currentImageSelect = 0;

                    while (currentImageSelect < countClipData){


                        FileUri = data.getClipData().getItemAt(currentImageSelect).getUri();

                        FileList.add(FileUri);

                        foto.setImageURI(FileList.get(0));
                        currentImageSelect = currentImageSelect +1;


                    }


                    Toast.makeText(this, "Fotoğraflar Seçildi", Toast.LENGTH_SHORT).show();
                }else{


                 Toast.makeText(this, "Lütfen Birden Fazla Fotoğraf Seçiniz", Toast.LENGTH_SHORT).show();
                }


            }


        }

    }
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }
    public void UploadImage(String url,int upload_count) {
        if (FilePathUri != null) {

            progressDialog.setTitle("Kayıt Ediliyor...!");
            progressDialog.show();
            StorageReference storageReference2 = storageReference.child(System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));
            storageReference2.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           String açıklama=editTextaciklama.getText().toString().trim();
                            String isim = editTextisim.getText().toString().trim();
                            final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            adminref = FirebaseDatabase.getInstance().getReference("admins").child(firebaseUser.getUid());
                            assert firebaseUser != null;
                           adminref.addValueEventListener(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot snapshot) {
                                   for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                       String uid= dataSnapshot.getValue().toString();
                                       Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                                       while (!task.isComplete());
                                       Uri dwl= task.getResult();
                                       String imgurl = dwl.toString();
                                       String category = editTextkategory.getText().toString().trim();
                                       category=category.toLowerCase();
                                       category=category.substring(0,1).toUpperCase()+category.substring(1).toLowerCase();
                                       String  de=DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                                       databaseReference=FirebaseDatabase.getInstance().getReference(uid);
                                   //    Model imageUploadInfo = new Model(isim,uid, imgurl,açıklama,de,category);

                                     //  databaseReference.child(category).child(isim).setValue(imageUploadInfo);
                                       if (task.isSuccessful()){
                                           if (isim==null){
                                               Toast.makeText(ekleme2.this, "İsim boş bırakılamaz!", Toast.LENGTH_SHORT).show();

                                           }
                                           else  if (açıklama==null){
                                               Toast.makeText(ekleme2.this, "Boşlukları Doldurun!", Toast.LENGTH_SHORT).show();
                                           }
                                           else    Toast.makeText(ekleme2.this, "Kayıt Eklendi", Toast.LENGTH_SHORT).show();



                                       }
                                   }
                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError error) {

                               }
                           });



                            progressDialog.dismiss();




                        }
                    });
        }
        else {

            Toast.makeText(ekleme2.this, "Lütfen Boş Alanları Doldurun!", Toast.LENGTH_LONG).show();

        }
    }
    public void upld(){
        StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("FileFolder");
        progressDialog.setTitle("Kayıt Ediliyor...!");
        progressDialog.show();
         arrayList.clear();

        for(upload_count = 0; upload_count < FileList.size(); upload_count++){


            Uri IndividualFile = FileList.get(upload_count);
            final StorageReference ImageName = ImageFolder.child("Image"+IndividualFile.getLastPathSegment());



            ImageName.putFile(IndividualFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String url = String.valueOf(uri);
                            arrayList.add(url);
                            for(int i =0 ;i<arrayList.size();i++)
                            Arabulucu(arrayList.get(i).toString(),FileList.size());


                        }
                    });







                }
            });



        }


    }
    public  void Arabulucu(String text,int size){

        arrayListara.add(text);

      if (arrayList.size()==size){
          Realupload(arrayList,size);

      }


    }
    public void Realupload(ArrayList arrayList,int size){
        String açıklama=editTextaciklama.getText().toString().trim();
        String isim = editTextisim.getText().toString().trim();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        adminref = FirebaseDatabase.getInstance().getReference("admins").child(firebaseUser.getUid());
        assert firebaseUser != null;




        adminref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String uid= Objects.requireNonNull(dataSnapshot.getValue()).toString();




                    if (size==1){
                         imgurl= arrayList.get(0).toString();


                    }

                   else if (size==2){

                        imgurl= arrayList.get(0).toString();
                        imgurl2= arrayList.get(1).toString();
                        Picasso.get().load(imgurl).into(foto);
                    }

                   else if (size==3){
                        imgurl= arrayList.get(0).toString();
                        imgurl2= arrayList.get(1).toString();
                        imgurl3 =arrayList.get(2).toString();
                        foto.setImageURI(Uri.parse(imgurl));

                    }
                   else if (size==4){
                        imgurl= arrayList.get(0).toString();
                        imgurl2= arrayList.get(1).toString();
                        imgurl3 =arrayList.get(2).toString();
                        imgurl4 =arrayList.get(3).toString();
                    }

                   else if (size==5){
                        imgurl= arrayList.get(0).toString();
                        imgurl2= arrayList.get(1).toString();
                        imgurl3 =arrayList.get(2).toString();
                        imgurl4 =arrayList.get(3).toString();
                        imgurl5 =arrayList.get(4).toString();
                    }





                    String category = editTextkategory.getText().toString().trim();
                    category=category.toLowerCase();
                    category=category.substring(0,1).toUpperCase()+category.substring(1).toLowerCase();
                    String  de=DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                    databaseReference=FirebaseDatabase.getInstance().getReference(uid);

                    Model imageUploadInfo = new Model(isim,uid, imgurl,açıklama,de,category,imgurl2,imgurl3,imgurl4,imgurl5);

                    databaseReference.child(category).child(isim).setValue(imageUploadInfo);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        progressDialog.dismiss();


    }
}
