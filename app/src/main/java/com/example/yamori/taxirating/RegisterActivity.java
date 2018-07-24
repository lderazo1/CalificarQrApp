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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private Button registerBtn;
    private EditText emailBox, passwordBox;
    //declaramos objeto firebase auth
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //iniciamos el objeto firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        //referenciamos los views
        emailBox = findViewById(R.id.email);
        passwordBox = findViewById(R.id.password);
        registerBtn = findViewById(R.id.register);

        progressDialog = new ProgressDialog(this);

        registerBtn.setOnClickListener(this);
    }
    private void registrarUsuario() {
        String email = emailBox.getText().toString().trim();
        String password = passwordBox.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Se debe ingresar un email", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Se debe ingresar una contraseña", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Realizando registro en linea...");
        progressDialog.show();
        //creando el nuevo usuario
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Se ha registrado el usuario "+emailBox.getText(), Toast.LENGTH_SHORT).show();
                            //redireccionamos a la pagina de login
                            Intent LoginView = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(LoginView);
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) { //si se presenta una colisión
                                Toast.makeText(RegisterActivity.this, "Ese usuario ya existe ", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "No se pudo registrar el usuario ", Toast.LENGTH_SHORT).show();
                            }
                        }
                        progressDialog.dismiss();
                    }
                });
    }
    public void onClick(View view) {
        registrarUsuario();
    }
}
