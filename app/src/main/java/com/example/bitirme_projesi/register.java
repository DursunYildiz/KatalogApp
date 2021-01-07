package com.example.bitirme_projesi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.core.Tag;
import com.subhrajyoti.passwordview.PasswordView;

public class register extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText editemail,editşifre,editşifreonay;
    PasswordView editTextşifre,editTextşifreonay;
    Button button;
    FirebaseUser firebaseUser;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        internet_check_alert();
        setContentView(R.layout.register);
        mAuth = FirebaseAuth.getInstance();
        editemail=(EditText)findViewById(R.id.etEmail);
        editşifre=(PasswordView) findViewById(R.id.passwordView);
        editşifreonay=(PasswordView) findViewById(R.id.passwordView);
        button=(Button)findViewById(R.id.button4);
       progressDialog = new ProgressDialog(register.this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                internet_check_alert();
                String email =editemail.getText().toString().trim();
                String şifre =editşifre.getText().toString().trim();
                String şifreonay=editşifreonay.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(register.this, "Lütfen email giriniz!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(şifre)){
                    Toast.makeText(register.this, "Lütfen şifre giriniz!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(şifreonay)){
                    Toast.makeText(register.this, "Lütfen şifre onayı giriniz!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (şifre.length()<8){
                    Toast.makeText(register.this, "Şifre minimum 8 basamaklı olmalıdır!", Toast.LENGTH_SHORT).show();
                }
                progressDialog.setMessage("Kullanıcı Kayıt Ediliyor");
                progressDialog.show();
                if (şifre.equals(şifreonay)){

                    mAuth.createUserWithEmailAndPassword(email, şifre)
                            .addOnCompleteListener(register.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        //  startActivity(new Intent(getApplicationContext(), register.class));
                                        Toast.makeText(register.this, "Kayıt başarılı!", Toast.LENGTH_SHORT).show();
                                        firebaseUser=mAuth.getCurrentUser();
                                        firebaseUser.sendEmailVerification();
                                        Intent intent =new Intent(getApplicationContext(),giris.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(register.this, "Kayıt başarısız!", Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });
                }

            }
        });
    }
    public boolean isconnected (){
        ConnectivityManager connectivityManager = ( ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo!=null && networkInfo.isConnected() ;
    }

    public  void  internet_check_alert (){

        if (!isconnected()){
            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("İnternet Bağlantısı Bulunamadı!!")
                    .setMessage("Uygulama İnternet Bağlantısı Olmadan Kullanılamaz!! Lütfen Bağlantınızı Kontrol Edin!! ")
                    .setPositiveButton("Tekrar Deneyin!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            internet_check_alert();
                            // finish();
                        }
                    }).show();
        }
    }


}