package com.example.drivenow.views;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.drivenow.R;
import com.example.drivenow.utils.helper.GalleryHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileIzmeniActivity extends AppCompatActivity {

    private EditText ime, prezime, brojTel, email, lozinka;
    private ImageView slika;
    private Button izmeniProfile, odustani;
    private String url = "http://192.168.13.112:5000";
    private String pictureUrl = "/static/slike/";

    // Čuva originalnu putanju i novu putanju ako je slika promenjena
    private String putanjaValue;
    private String selectedImagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_korisnik_profile_izmeni);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.korisnikProfilIzmeni), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int korisnikId = getIntent().getIntExtra("id", -1);
        String imeIPrezimeValue = getIntent().getStringExtra("imeIPrezime");
        String brojTelValue = getIntent().getStringExtra("brojTel");
        String emailValue = getIntent().getStringExtra("email");
        String lozinkaValue = getIntent().getStringExtra("lozinka");
        putanjaValue = getIntent().getStringExtra("slika");

        ime = findViewById(R.id.imeProfilIzmena);
        prezime = findViewById(R.id.prezimeProfilIzmena);
        brojTel = findViewById(R.id.brTelProfilIzmena);
        email = findViewById(R.id.emailProfilIzmena);
        lozinka = findViewById(R.id.lozinkaProfilIzmeni);
        slika = findViewById(R.id.profileImageView4);

        izmeniProfile = findViewById(R.id.izmeniButton);
        odustani = findViewById(R.id.buttonOdustani);

        String[] imeIPrezime = imeIPrezimeValue.split("\\s+");

        ime.setText(imeIPrezime[0]);
        prezime.setText(imeIPrezime[1]);
        brojTel.setText(brojTelValue);
        email.setText(emailValue);
        lozinka.setText(lozinkaValue);

        // Učitavanje početne slike pomoću Picasso (koristi originalnu putanju)
        Picasso.get()
                .load(url+pictureUrl+putanjaValue)
                .error(R.drawable.baseline_account_circle_24)
                .into(slika);

        // Kreiranje instance GalleryHandler (kao anonimna klasa)
        GalleryHandler galleryHandler = new GalleryHandler(ProfileIzmeniActivity.this) {
            @Override
            protected void onImageSelected(Uri imageUri) {
                // Kada korisnik odabere novu sliku, sačuvaj URI kao string i učitaj sliku pomoću Picasso
                selectedImagePath = imageUri.toString();
                Picasso.get()
                        .load(imageUri)
                        .error(R.drawable.baseline_account_circle_24)
                        .into(slika);
            }
        };

        // Poveži GalleryHandler sa ImageView-om (dodaje klik listener)
        galleryHandler.attachGalleryClickListener(slika);

        izmeniProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject updatedUserData = new JSONObject();
                try {
                    updatedUserData.put("ime", ime.getText().toString());
                    updatedUserData.put("prezime", prezime.getText().toString());
                    updatedUserData.put("brojTelefona", brojTel.getText().toString());
                    updatedUserData.put("email", email.getText().toString());
                    updatedUserData.put("lozinka", lozinka.getText().toString());

                    // Ako je korisnik izabrao novu sliku, koristi njen URL; inače koristi originalnu putanju
                    if (selectedImagePath != null) {
                        updatedUserData.put("putanja", selectedImagePath);
                    } else {
                        updatedUserData.put("putanja", putanjaValue);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                updateUser(korisnikId, updatedUserData);
                finish();
            }
        });

        odustani.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateUser(int userId, JSONObject updatedData) {
        String izmeniKorisnikaUrl = url + "/api/users_izmeni/" + userId; // Zameni sa stvarnim URL-om

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, izmeniKorisnikaUrl, updatedData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(ProfileIzmeniActivity.this, "Korisnik uspešno ažuriran!", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProfileIzmeniActivity.this, "Greška pri ažuriranju korisnika: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Dodajemo zahtev u Volley red
        Volley.newRequestQueue(getApplicationContext()).add(request);
    }
}
