package com.example.drivenow.views;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.drivenow.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BookingDetailsActivity extends AppCompatActivity {

    private TextView imeIPrezime, email, brojTelefona, vozilo, cena, brojDana, datumPreuzimanja, datumVracanja, ukupnaCena;
    private EditText komentar;
    private RatingBar ratingBar;
    private ImageView slika;
    private String url = "http://192.168.13.112:5000";
    private String pictureUrl = "/static/slike/";
    private float ocena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_korisnik_automobil_opis_bukiranja);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.korisnikAutomobilOpisBukiranja), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int autoId = getIntent().getIntExtra("autoId", -1);
        int korisnikId = getIntent().getIntExtra("korisnikId", -1);
        int racObjekatId = getIntent().getIntExtra("racObjekatId", -1);
        String ime = getIntent().getStringExtra("ime");
        String prezime = getIntent().getStringExtra("prezime");
        String brTel = getIntent().getStringExtra("brojTele");
        String mail = getIntent().getStringExtra("email");
        String datumPreuz = getIntent().getStringExtra("datumPreuzimanja");
        String vremePreuzimanja = getIntent().getStringExtra("vremePreuzimanja");
        String brDana = getIntent().getStringExtra("brojDana");

        imeIPrezime = findViewById(R.id.imeIPrezimeText);
        email = findViewById(R.id.emailText);
        brojTelefona = findViewById(R.id.brTelText);
        vozilo = findViewById(R.id.voziloText);
        cena = findViewById(R.id.cenaText);
        brojDana = findViewById(R.id.brDanaText);
        datumPreuzimanja = findViewById(R.id.preuzimanjeText);
        datumVracanja = findViewById(R.id.vracanjeText);
        ukupnaCena = findViewById(R.id.ukupnaCenaText);
        slika = findViewById(R.id.imageView3);
        komentar = findViewById(R.id.komentarObjekta);
        ratingBar = findViewById(R.id.ratingBar);

        dobaviAuto(autoId, Integer.parseInt(brDana));

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ocena = rating;
            }
        });

        String imeIPrezimeKorisnika = ime + " " + prezime;
        imeIPrezime.setText(imeIPrezimeKorisnika);
        email.setText(mail);
        brojTelefona.setText(brTel);
        brojDana.setText(brDana);

        // Definisanje formata
        String ulazniFormat = "dd/MM/yyyy"; // Format koji korisnik koristi
        String izlazniFormat = "dd/MM/yyyy"; // Format koji očekujemo
        SimpleDateFormat ulazniSdf = new SimpleDateFormat(ulazniFormat, Locale.getDefault());
        SimpleDateFormat izlazniSdf = new SimpleDateFormat(izlazniFormat, Locale.getDefault());

        String datumVracanjaValue = "";
        try {
            // Parsiranje i konvertovanje datuma u očekivani format
            Date parsedDate = ulazniSdf.parse(datumPreuz);
            datumPreuz = izlazniSdf.format(parsedDate);

            // Izračunavanje datuma vraćanja
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(izlazniSdf.parse(datumPreuz));
            calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(brDana));

            datumVracanjaValue = izlazniSdf.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            datumPreuz = "Nevalidan datum";
            datumVracanjaValue = "Nevalidan datum";
        }

        String finalDatumPreuzimanja = vremePreuzimanja + " " + datumPreuz;
        String finalDatumVracanja = vremePreuzimanja + " " + datumVracanjaValue;

        datumPreuzimanja.setText(finalDatumPreuzimanja);
        datumVracanja.setText(finalDatumVracanja);

        Button bukiraj = findViewById(R.id.potvrdiBukiranje);
        bukiraj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodajRezervaciju(finalDatumPreuzimanja, finalDatumVracanja, Integer.parseInt(brDana), autoId, korisnikId);
                dodajOcenu(ocena, komentar.getText().toString().trim(), racObjekatId, korisnikId);
                izmeniAuto(autoId);

                showDialogAndFinish();
            }
        });
    }

    private void dobaviAuto(int autoId, int brDana){
        String autoUrl = url + "/api/automobiles_by_id/" + autoId; // Zameni sa stvarnim URL-om

        // Kreiranje GET zahteva
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, autoUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            String proizvodjac = response.getString("proizvodjac");
                            String model = response.getString("model");
                            int godiste = response.getInt("godiste");
                            double cenaa = response.getDouble("cenaPoDanu");
                            String putanja = response.getString("putanja");

                            double ukupnaCenaAuta = cenaa * brDana;
                            cena.setText(String.valueOf(cenaa));
                            ukupnaCena.setText(String.valueOf(ukupnaCenaAuta));
                            String voziloNaziv = proizvodjac + " " + model + " " + godiste;
                            vozilo.setText(voziloNaziv);
                            Picasso.get()
                                    .load(url+pictureUrl+putanja)
                                    .placeholder(R.drawable.car_placeholder)
                                    .error(R.drawable.ic_launcher_background)
                                    .into(slika);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(BookingDetailsActivity.this, "Greška u parsiranju podataka!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Obrada greške u slučaju neuspešnog poziva
                        Toast.makeText(BookingDetailsActivity.this, "Greška u vezi sa serverom: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Dodajemo zahtev u Volley red za obradu
        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    private void izmeniAuto(int autoId){
        String izmenAutoUrl = url + "/api/automobiles_izmeni/" + autoId;

        // Kreiranje JSON objekta sa novim podacima
        JSONObject podaci = new JSONObject();
        try {
            podaci.put("dostupnost", false);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Greška pri formiranju podataka!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kreiranje PUT zahteva pomoću Volley-a
        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, izmenAutoUrl, podaci,
                response -> {
                    Log.d("AutomobilIzmena", "Automobil uspešno ažuriran!");
                },
                error -> {
                    // Greška u vezi
                    Toast.makeText(BookingDetailsActivity.this, "Greška prilikom ažuriranja: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Dodavanje zahteva u Volley red
        Volley.newRequestQueue(getApplicationContext()).add(putRequest);
    }

    private void dodajRezervaciju(String datumPreuzimanja, String datumVracanja, int brDana, int autoId, int korisnikId){

        String rezervacijaUrl = url + "/api/add_rentals";

        // Kreiranje JSON objekta sa podacima rezervacije
        JSONObject podaci = new JSONObject();
        try {
            podaci.put("datumPreuzimanja", datumPreuzimanja);
            podaci.put("datumVracanja", datumVracanja);
            podaci.put("brojDana", brDana);
            podaci.put("autoId", autoId);
            podaci.put("userId", korisnikId);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Greška pri kreiranju JSON podataka!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kreiranje POST zahteva pomoću Volley-a
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, rezervacijaUrl, podaci,
                response -> {
                    // Uspešan odgovor
                    Log.d("DodajRezervaciju", "Rezervacija uspešno dodata!");
                },
                error -> {
                    // Greška u vezi
                    Toast.makeText(this, "Greška prilikom dodavanja rezervacije: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Dodavanje zahteva u Volley red
        Volley.newRequestQueue(getApplicationContext()).add(postRequest);
    }

    private void dodajOcenu(double ocena, String komentar, int racObjekatId, int korisnikId){

        String ocenaUrl = url + "/api/reviews";

        // Kreiranje JSON objekta sa podacima rezervacije
        JSONObject podaci = new JSONObject();
        try {
            podaci.put("ocena", ocena);
            podaci.put("komentar", komentar);
            podaci.put("racObjekatId", racObjekatId);
            podaci.put("userId", korisnikId);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Greška pri kreiranju JSON podataka!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kreiranje POST zahteva pomoću Volley-a
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, ocenaUrl, podaci,
                response -> {
                    // Uspešan odgovor
                    Log.d("DodajOcenu", "Ocena uspešno dodata!");
                },
                error -> {
                    // Greška u vezi
                    Toast.makeText(this, "Greška prilikom dodavanja ocene: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Dodavanje zahteva u Volley red
        Volley.newRequestQueue(getApplicationContext()).add(postRequest);
    }

    private void showDialogAndFinish() {
        // Kreiraj dijalog
        AlertDialog.Builder builder = new AlertDialog.Builder(BookingDetailsActivity.this);
        builder.setView(R.layout.activity_korisnik_loading_dialog)  // Koristi svoj layout dijaloga
                .setCancelable(false);

        final AlertDialog dialog = builder.create();
        dialog.show();

        // Handler za zatvaranje aktivnosti posle 3 sekunde
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Zatvori dijalog i aktivnost
                dialog.dismiss();
                finish();  // Završava aktivnost
            }
        }, 3000);  // 3000 milisekundi = 3 sekunde
    }
}
