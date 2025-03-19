package com.example.drivenow.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.drivenow.R;
import com.example.drivenow.utils.helper.DateTimePickerUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BookingProcesActivity extends AppCompatActivity {

    private int korisnikId;
    private EditText ime, prezime, brojTelefona, email, datumPreuzimanja, vremePreuzimanja, brojDana;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_korisnik_automobil_bukiraj);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.korisnikAutomobilBukiraj), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int autoId = getIntent().getIntExtra("autoId", -1);
        int racObjekatId = getIntent().getIntExtra("objekatId", -1);

        ime = findViewById(R.id.imeInput);
        prezime = findViewById(R.id.prezimeInput);
        brojTelefona = findViewById(R.id.telefonInput);
        email = findViewById(R.id.emailInput);
        datumPreuzimanja = findViewById(R.id.datumInput);
        vremePreuzimanja = findViewById(R.id.vremeInput);
        brojDana = findViewById(R.id.brDanaInupt);

        DateTimePickerUtil dateTimePickerUtil1 = new DateTimePickerUtil(this, datumPreuzimanja);
        datumPreuzimanja.setOnClickListener(v -> dateTimePickerUtil1.openDatePicker());

        DateTimePickerUtil dateTimePickerUtil2 = new DateTimePickerUtil(this, vremePreuzimanja);
        vremePreuzimanja.setOnClickListener(v -> dateTimePickerUtil2.openTimePicker());

        Button nastaviBukiranje = findViewById(R.id.nastaviBukiranje);

        nastaviBukiranje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String imeValue = ime.getText().toString().trim();
                String prezimeValue = prezime.getText().toString().trim();
                String brojTelefonaValue = brojTelefona.getText().toString().trim();
                String emailValue = email.getText().toString().trim();
                String datumPreuzimanjaValue = datumPreuzimanja.getText().toString().trim();
                String vremePreuzimanjaValue = vremePreuzimanja.getText().toString().trim();
                String brojDanaValue = brojDana.getText().toString().trim();

                // Provera da li su uneti datum preuzimanja, vreme preuzimanja i broj dana
                if (datumPreuzimanjaValue.isEmpty() || vremePreuzimanjaValue.isEmpty() || brojDanaValue.isEmpty()) {
                    Toast.makeText(BookingProcesActivity.this, "Molimo unesite datum, vreme preuzimanja i broj dana!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Integer.parseInt(brojDanaValue) <= 0){
                    Toast.makeText(BookingProcesActivity.this, "Broj dana bukiranja mora biti veci od nula!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Provera autenticnosti podataka
                proveriPodatke(imeValue, prezimeValue, brojTelefonaValue, emailValue, isValid -> {
                    if (isValid) {
                        Intent intent = new Intent(BookingProcesActivity.this, BookingDetailsActivity.class);
                        intent.putExtra("autoId", autoId);
                        intent.putExtra("racObjekatId", racObjekatId);
                        intent.putExtra("korisnikId", korisnikId);
                        intent.putExtra("ime", imeValue);
                        intent.putExtra("prezime", prezimeValue);
                        intent.putExtra("brojTele", brojTelefonaValue);
                        intent.putExtra("email", emailValue);
                        intent.putExtra("datumPreuzimanja", datumPreuzimanjaValue);
                        intent.putExtra("vremePreuzimanja", vremePreuzimanjaValue);
                        intent.putExtra("brojDana", brojDanaValue);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(BookingProcesActivity.this, "Podaci nisu validni!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void proveriPodatke(String ime, String prezime, String brojTelefona, String email, ValidationCallback callback){

        String url = "http://192.168.13.112:5000/api/users";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            boolean isValid = false;
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject korisnik = response.getJSONObject(i);

                                int id = korisnik.getInt("id");
                                String imee = korisnik.getString("ime");
                                String prezimee = korisnik.getString("prezime");
                                String brojTelefonaa = korisnik.getString("brojTelefona");
                                String emaill = korisnik.getString("email");

                                // Provera da li se uneti podaci podudaraju sa bilo kojim korisnikom iz baze
                                if (imee.equals(ime) && prezimee.equals(prezime) &&
                                        brojTelefonaa.equals(brojTelefona) && emaill.equals(email)) {
                                    korisnikId = id;
                                    isValid = true;
                                    break;
                                }
                            }
                            callback.onValidationResult(isValid);

                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(BookingProcesActivity.this, "Greška u parsiranju podataka!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(BookingProcesActivity.this, "Greška u vezi sa serverom: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    public interface ValidationCallback {
        void onValidationResult(boolean isValid);
    }
}
