package com.example.bitirme_projesi;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.subhrajyoti.passwordview.PasswordView;

public class giris extends AppCompatActivity {
    Button gir,kayıt;
    EditText email ,editemail;
    PasswordView passwordView;
    ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;

    private int RC_SIGN_IN = 1;
    FirebaseUser firebaseUser;
    private AppCompatActivity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);
        passwordView = (PasswordView) findViewById(R.id.passwordView);
        gir = (Button) findViewById(R.id.button3);
        progressDialog = new ProgressDialog(giris.this);

        kayıt = (Button) findViewById(R.id.button4);
        firebaseAuth = FirebaseAuth.getInstance();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Giriş Ekranı");
        editemail = (EditText) findViewById(R.id.etEmail);

        internet_check_alert();
        signInButton = findViewById(R.id.sign_in_button);
        firebaseAuth = FirebaseAuth.getInstance();

        Giris();
        register();

        google_sing_in();

        activity=this;
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

        } else
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 0);









    }

    private void register() {
        kayıt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), register.class));
            }
        });
    }

    private void google_sing_in() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(view -> {
            internet_check_alert();
            signIn();
            progressDialog.setMessage("Giriş yapılıyor");
            progressDialog.show();
        });
    }

    private void Giris() {
        gir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                internet_check_alert();
                progressDialog.setMessage("Giriş yapılıyor");
                progressDialog.show();
                String email = editemail.getText().toString().trim();
                String şifre = passwordView.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    progressDialog.dismiss();
                    Toast.makeText(giris.this, "Lütfen email giriniz!", Toast.LENGTH_SHORT).show();

                    return;
                }
                if (TextUtils.isEmpty(şifre)) {
                    progressDialog.dismiss();
                    Toast.makeText(giris.this, "Lütfen şifre giriniz!", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(email, şifre)
                        .addOnCompleteListener(giris.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    firebaseUser=firebaseAuth.getCurrentUser();
                                    if (firebaseUser.isEmailVerified())
                                    {
                                        progressDialog.dismiss();
                                        startActivity(new Intent(getApplicationContext(), main_2.class)); }
                                    else{
                                        Toast.makeText(giris.this, "Email onaylanmamış lütfen onaylıyın", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                } else {
                                    Toast.makeText(giris.this, "Giriş başarısız! Email veya Şifre Hatalı!", Toast.LENGTH_SHORT).show();

                                }

                                // ...
                            }
                        });

            }
        });
    }

    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{

            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText(giris.this,"Giriş Başarılı",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        }
        catch (ApiException e){
            Toast.makeText(giris.this,"Giriş Başarısız",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {

        if (acct != null) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        updateUI(user);
                    } else {

                        updateUI(null);
                    }
                }
            });
        }
        else{

        }
    }

    private void updateUI(FirebaseUser fUser){


        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account !=  null){
            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();
            progressDialog.dismiss();

            Intent intent = new Intent(getApplicationContext(),main_2.class);
            startActivity(intent);
        }

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
