package com.example.yamori.taxirating;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;

import java.util.Calendar;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class RatingActivity extends AppCompatActivity {

    public static final String user = "names";
    ImageView taxi;
    TextView nombre, pregunta, opcional, usuario;
    RatingBar calificacion;
    EditText comentario;
    Button enviar;
    private FirebaseAuth firebaseAuth;
    private ZXingScannerView vistaEscaner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        //iniciamos el objeto firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        usuario = findViewById(R.id.user);
        String user = getIntent().getStringExtra("names");
        usuario.setText("Estas usando la app como: "+user);

    }
    // metodo que activa la cámara para capturar QR
    public void Escanear(View view) {
        vistaEscaner = new ZXingScannerView(this);
        vistaEscaner.setResultHandler(new zxingscanner());
        setContentView(vistaEscaner);
        vistaEscaner.startCamera();
    }

    // clase que nos permite capturar el dato del codigo QR
    class zxingscanner implements ZXingScannerView.ResultHandler {

        @Override
        public void handleResult(Result result) {
            String dato = result.getText();
            setContentView(R.layout.activity_rating);
            vistaEscaner.stopCamera();
            // asociamos el objeto con su correspondiente en la vista
            nombre = findViewById(R.id.nombre);
            taxi = findViewById(R.id.imgTaxi);
            calificacion = findViewById(R.id.calificacion);
            pregunta = findViewById(R.id.pregunta);
            opcional = findViewById(R.id.opcional);
            comentario = findViewById(R.id.comentario);
            enviar = findViewById(R.id.btnEnviar);
            // seteamos el dato en un TextView: nombre
            nombre.setText(dato);
            // le damos visibilidad al resto del formulario
            setVisibilidad(View.VISIBLE);
        }
    }

    public void SendRating(View view) {
        // asociamos el objeto con su correspondiente en la vista

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("rating/"+nombre.getText().toString());
        myRef.child(Calendar.getInstance().getTime()+"").setValue(new Taxista(
                calificacion.getProgress(),
                comentario.getText().toString()
        ));
        //toast que indica el envio exitoso
        Toast.makeText(this, "¡Se han enviado los datos correctamente!", Toast.LENGTH_LONG).show();
        //reiniciar visibilidad de objetos
        setVisibilidad(View.INVISIBLE);
    }

    public void setVisibilidad(int visibility) {
        taxi.setVisibility(visibility);
        nombre.setVisibility(visibility);
        pregunta.setVisibility(visibility);
        calificacion.setVisibility(visibility);
        opcional.setVisibility(visibility);
        comentario.setVisibility(visibility);
        enviar.setVisibility(visibility);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.rating_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.logout_session:
                LogOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void LogOut() {
        try {
            firebaseAuth.signOut();
            Toast.makeText(this, "Se cerró la sesión exitosamente", Toast.LENGTH_SHORT).show();
            //redireccionamos a la pagina de rating
            Intent LoginView = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(LoginView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
