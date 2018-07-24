package com.example.yamori.taxirating;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button loginBtn, redirectToRegisterBtn;
    private EditText emailBox, passwordBox;
    //declaramos objeto firebase auth
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //iniciamos el objeto firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        //referenciamos los views
        emailBox = findViewById(R.id.email);
        passwordBox = findViewById(R.id.password);
        loginBtn = findViewById(R.id.login);
        redirectToRegisterBtn = findViewById(R.id.redirectToRegister);

        progressDialog = new ProgressDialog(this);

        loginBtn.setOnClickListener(this);
        redirectToRegisterBtn.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                loguearUsuario();
                break;
            case R.id.redirectToRegister:
                Intent RegisterView = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(RegisterView);
                break;
        }
    }

    private void loguearUsuario() {
        final String email = emailBox.getText().toString().trim();
        String password = passwordBox.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Se debe ingresar un email", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Se debe ingresar una contraseña", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Verificando credenciales...");
        progressDialog.show();
        //logueando al usuario
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            int pos = email.indexOf("@");
                            String user = email.substring(0, pos);
                            Toast.makeText(LoginActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                            //redireccionamos a la pagina de rating
                            Intent RatingView = new Intent(getApplicationContext(), RatingActivity.class);
                            RatingView.putExtra(RatingActivity.user, user);
                            startActivity(RatingView);
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) { //si se presenta una colisión
                                Toast.makeText(LoginActivity.this, "Ese usuario ya existe ", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "No se pudo registrar el usuario ", Toast.LENGTH_SHORT).show();
                            }
                        }
                        progressDialog.dismiss();
                    }
                });
    }
}
