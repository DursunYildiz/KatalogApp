package com.example.bitirme_projesi;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.firebase.storage.FirebaseStorage.getInstance;


public class naw extends AppCompatActivity  {
    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    FirebaseDatabase firebaseDatabase;
    RecyclerView recyclerView;
    DatabaseReference mref,dinamicref,newref;


    LinearLayoutManager mlayoutmanager;
    SharedPreferences sharedPreferences;
    List<String> stringList,childlist,adminslist,adminslist2;

    public final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naw);
        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String id = getIntent().getStringExtra("id");
        nav= findViewById(R.id.navmenu);
        drawerLayout= findViewById(R.id.drawer);

        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Drs_project");



        firebaseDatabase = FirebaseDatabase.getInstance();

         stringList = new ArrayList<>();
         childlist = new ArrayList<>();
         adminslist = new ArrayList<>();
         adminslist2 = new ArrayList<>();



        dinamicref=firebaseDatabase.getReference(id);

        mref = firebaseDatabase.getReference(id);
        sharedPreferences= getSharedPreferences("sortsetting",MODE_PRIVATE);
        String msortig = sharedPreferences.getString("Sırala","Yeni");

        if (msortig.equals("Yeni")){
            mlayoutmanager = new LinearLayoutManager(this);
            mlayoutmanager.setReverseLayout(true);
            mlayoutmanager.setStackFromEnd(true);
        }
        else if (msortig.equals("Eski")){
            mlayoutmanager = new LinearLayoutManager(this);
            mlayoutmanager.setReverseLayout(false);
            mlayoutmanager.setStackFromEnd(false);
        }
        recyclerView = findViewById(R.id.recleview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mlayoutmanager);

    }





    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.ekleme :


                startActivity(new Intent(getApplicationContext(), ekleme2.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.sıralama :
                showShortdialog();
                break;
            case R.id.qr_image :
                startActivity(new Intent(getApplicationContext(), qr.class));
                break;
            case R.id.info :

                new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("Bu uygulama Dursun Yıldız tarafından yapılmıştır")
                        .setMessage("Görüş,öneri ve sorularınız için dursuny14@gmail adresine mail atabilirsiniz")
                        .setPositiveButton("Mail gönder", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Intent mail = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:dursuny14@gmail.com"));
                                startActivity(mail);

                            }
                        }).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteDataDialog(final String currenisim, final String currentimage) {

        AlertDialog.Builder builder = new AlertDialog.Builder(naw.this);
        builder.setTitle("Kayıt Sil!");
        builder.setMessage("Kayıtı Silmek İstediğinize Eminmisiniz?");
        builder.setPositiveButton("Evet", (dialogInterface, i) -> {

            Query query = newref.orderByChild("isim").equalTo(currenisim);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds:dataSnapshot.getChildren()){
                        ds.getRef().removeValue();
                    }
                    Toast.makeText(naw.this, "Kayıt Başarı İle Silindi...", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(naw.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            StorageReference mPictureref = getInstance().getReferenceFromUrl(currentimage);
            mPictureref.delete().addOnSuccessListener(aVoid -> Toast.makeText(naw.this, "Fotoğraf Başarı İle Silindi...", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> {

            });
        });
        builder.setNegativeButton("Hayır", (dialogInterface, i) -> {

        });
        builder.create().show();
    }
    public void Search(String searchtext) {
        String quary=searchtext.toLowerCase();
        Query firebasequary = newref.orderByChild("isim").startAt(quary).endAt(quary + "\uf0ff");
        FirebaseRecyclerAdapter<Model, Göster> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Model, Göster>(
                Model.class,
                R.layout.row,
                Göster.class,
                firebasequary
        ) {
            @Override
            protected void populateViewHolder(Göster goster, Model model, int i) {
                goster.setdateail(getApplicationContext(), model.getIsim(), model.getId(), model.getImage(),model.getAçıklama(),model.getTarih(),model.getImage2(),
                        model.getImage3(),model.getImage4(),model.getImage5());
            }

            @Override
            public Göster onCreateViewHolder(ViewGroup parent, int viewType) {
                Göster goster = super.onCreateViewHolder(parent, viewType);
                goster.setonclick(new Göster.onclickListener() {
                    @Override
                    public void Onitemclick(View view, int position) {

                        String misim =getItem(position).getIsim();
                        String mid =getItem(position).getId();
                        String img =getItem(position).getImage();
                        String aciklama = getItem(position).getAçıklama();
                        String tarih=getItem(position).getTarih();
                        String img2 =getItem(position).getImage2();
                        String img3 =getItem(position).getImage3();
                        String img4 =getItem(position).getImage4();
                        String img5 =getItem(position).getImage5();

                        Intent intent = new Intent(view.getContext(), detail.class);

                        intent.putExtra("isim", misim);
                        intent.putExtra("id", mid);
                        intent.putExtra("image", img);
                        intent.putExtra("açıklama", aciklama);
                        intent.putExtra("tarih",tarih);
                        intent.putExtra("image2", img2);
                        intent.putExtra("image3", img3);
                        intent.putExtra("image4", img4);
                        intent.putExtra("image5", img5);
                        startActivity(intent);
                    }

                    @Override
                    public void onitemlongclick(View view, int position) {
                        String currenisim = getItem(position).getIsim();
                        String currentimage = getItem(position).getImage();
                        Admins(currenisim, currentimage);


                    }
                });
                return goster;
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        adminslist.clear();
        MenuItem menuItem1=menu.findItem(R.id.ekleme);
        MenuItem qr=menu.findItem(R.id.qr_image);

        Adminsmenuitem(menuItem1, qr);


        MenuItem menuItem = menu.findItem(R.id.arama_butonu);


        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newtext) {
                Search(newtext);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void Adminsmenuitem(MenuItem menuItem1, MenuItem qr) {
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        dinamicref= FirebaseDatabase.getInstance().getReference("admins");

        assert firebaseUser != null;
        String uid = firebaseUser.getUid();

        dinamicref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){

                    adminslist.add(dataSnapshot.getKey());

                }
                for (String object : adminslist){


                    if (object.equals(uid)){

                       menuItem1.setVisible(true);
                       qr.setVisible(true);
                       qr.setEnabled(true);
                      menuItem1.setEnabled(true);

                    }




                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void listele(String text,String id) {
         newref = FirebaseDatabase.getInstance().getReference(id).child(text);

        FirebaseRecyclerAdapter<Model, Göster> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Model, Göster>(
                Model.class,
                R.layout.row,
                Göster.class,
                newref
        ) {
            @Override
            protected void populateViewHolder(Göster goster, Model model, int i) {
                goster.setdateail(getApplicationContext(), model.getIsim(), model.getId(), model.getImage(),model.getAçıklama(),model.getTarih(),model.getImage2(),model.getImage3()
                        ,model.getImage4(),model.getImage5());
            }

            @Override
            public Göster onCreateViewHolder(ViewGroup parent, int viewType) {
                Göster goster = super.onCreateViewHolder(parent, viewType);
                goster.setonclick(new Göster.onclickListener() {
                    @Override
                    public void Onitemclick(View view, int position) {

                        String misim =getItem(position).getIsim();
                        String mid =getItem(position).getId();
                        String img =getItem(position).getImage();
                        String aciklama = getItem(position).getAçıklama();
                        String tarih=getItem(position).getTarih();
                        String img2 =getItem(position).getImage2();
                        String img3 =getItem(position).getImage3();
                        String img4 =getItem(position).getImage4();
                        String img5 =getItem(position).getImage5();

                        Intent intent = new Intent(view.getContext(), detail.class);

                        intent.putExtra("isim", misim);
                        intent.putExtra("id", mid);
                        intent.putExtra("image", img);
                        intent.putExtra("açıklama", aciklama);
                        intent.putExtra("tarih",tarih);
                        intent.putExtra("image2", img2);
                        intent.putExtra("image3", img3);
                        intent.putExtra("image4", img4);
                        intent.putExtra("image5", img5);

                        startActivity(intent);
                    }

                    @Override
                    public void onitemlongclick(View view, int position) {
                        String currenisim = getItem(position).getIsim();
                        String currentimage = getItem(position).getImage();

                        Admins(currenisim, currentimage);


                    }
                });
                return goster;
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);


    }

    private void Admins(String currenisim, String currentimage) {
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        dinamicref= FirebaseDatabase.getInstance().getReference("admins");
        assert firebaseUser != null;

        String uid = firebaseUser.getUid();
        String id ="{id="+ getIntent().getStringExtra("id")+"}";
        dinamicref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){

                    adminslist.add(dataSnapshot.getKey());
                    adminslist2.add(dataSnapshot.getValue().toString());

                }
                for (String object : adminslist){


                    if (object.equals(uid)){
                        for (String object2 : adminslist2){

                            if (object2.equals(id)){

                                showDeleteDataDialog(currenisim,currentimage);
                            }
                        }




                    }






                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void showShortdialog() {
        String [] showoptions = {"Yeni" , "Eski"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sırala").setIcon(R.drawable.ic_action_sort).setItems(showoptions, (dialogInterface, i) -> {
            if (i==0){
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Sırala","Yeni");
                editor.apply();
                recreate();
            }
            else if (i==1) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Sırala","Eski");
                editor.apply();
                recreate();
            }
        });
        builder.show();

    }
    @Override
    public void onStart(){

        childlist.clear();
        Menu menu2 = nav.getMenu();
        menu2.clear();
        SubMenu subMenu2 = menu2.addSubMenu("KATEGORİ");
      super.onStart();
        subMenu2.clear();
        String id = getIntent().getStringExtra("id");
        dinamicref=FirebaseDatabase.getInstance().getReference(id);


        dinamicref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){


                    childlist.add(dataSnapshot.getKey());
                   Collections.sort(childlist);



                }

                        listele(childlist.get(0),id);
                for (String object: childlist) {
                    subMenu2.add(object).setOnMenuItemClickListener(item -> {
                        menu2.clear();
                        subMenu2.clear();

                       listele(object,id);
                        drawerLayout.closeDrawers();


                        return false;
                    });

                }
                drawerLayout.closeDrawers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

}

