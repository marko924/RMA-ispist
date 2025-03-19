package com.example.drivenow.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.android.volley.toolbox.Volley;
import com.example.drivenow.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private TextView imeIPrezime, email, brojTel, lozinka;
    private ImageView slika;
    private String slikaUrl;
    private String url = "http://192.168.13.112:5000";
    private String pictureUrl = "/static/slike/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_korisnik_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.korisnikProfil), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int korisnikId = getIntent().getIntExtra("id", -1);

        imeIPrezime = findViewById(R.id.imeIPrezime);
        email = findViewById(R.id.mail);
        brojTel = findViewById(R.id.brTel);
        lozinka = findViewById(R.id.lozinka);
        slika = findViewById(R.id.profileImageView);

        getKorisnikById(korisnikId);

        Button izmeniProfil = findViewById(R.id.buttonIzmeniProfil);

        izmeniProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileIzmeniActivity.class);
                intent.putExtra("id", korisnikId);
                intent.putExtra("imeIPrezime", imeIPrezime.getText().toString().trim());
                intent.putExtra("email", email.getText().toString().trim());
                intent.putExtra("brojTel", brojTel.getText().toString().trim());
                intent.putExtra("lozinka", lozinka.getText().toString().trim());
                intent.putExtra("slika", slikaUrl);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        int korisnikId = getIntent().getIntExtra("id", -1);
        getKorisnikById(korisnikId);
    }

    private void getKorisnikById(int korisnikId){
        // URL tvog API-ja koji vraća listu rental locations
        String korisnikUrl = url + "/api/users"; // Zameni sa stvarnim URL-om

        // Kreiranje GET zahteva
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, korisnikUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject locationObject = response.getJSONObject(i);

                                int id = locationObject.getInt("id");
                                if(id == korisnikId){
                                    String ime = locationObject.getString("ime");
                                    String prezime = locationObject.getString("prezime");
                                    String mail = locationObject.getString("email");
                                    String brTelefona = locationObject.getString("brojTelefona");
                                    String lozinkaa = locationObject.getString("lozinka");
                                    String putanja = locationObject.getString("putanja");

                                    String ip = ime + " " + prezime;
                                    imeIPrezime.setText(ip);
                                    email.setText(mail);
                                    brojTel.setText(brTelefona);
                                    lozinka.setText(lozinkaa);
                                    if (putanja.contains(pictureUrl)) {
                                        Picasso.get()
                                                .load(url + pictureUrl + putanja)
                                                .error(R.drawable.baseline_account_circle_24)
                                                .into(slika);
                                    } else {
                                        slikaUrl = putanja;
                                        Picasso.get()
                                                .load(slikaUrl)
                                                .error(R.drawable.baseline_account_circle_24)
                                                .into(slika);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ProfileActivity.this, "Greška u parsiranju podataka!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Obrada greške u slučaju neuspešnog poziva
                        Toast.makeText(ProfileActivity.this, "Greška u vezi sa serverom: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Dodajemo zahtev u Volley red za obradu
        Volley.newRequestQueue(getApplicationContext()).add(request);
    }
}