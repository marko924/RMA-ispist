package com.example.drivenow.views;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailsActivity extends AppCompatActivity {

    private TextView dostupan, cena, godiste, proizvodjac, model, kilometraza, brojSedista, tip;
    private ImageView slika;
    private String url = "http://192.168.13.112:5000";
    private String pictureUrl = "/static/slike/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_korisnik_automobil_detalji);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.korisnikAutomobilDetalji), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int autoId = getIntent().getIntExtra("autoId", -1);

        slika = findViewById(R.id.imageView);
        dostupan = findViewById(R.id.tvDostupnost);
        cena = findViewById(R.id.tvCenaPoDanu);
        godiste = findViewById(R.id.tvGodiste);
        proizvodjac = findViewById(R.id.tvProizvodjac);
        model = findViewById(R.id.tvModel);
        kilometraza = findViewById(R.id.tvKilometraza);
        brojSedista = findViewById(R.id.tvBrojSedista);
        tip = findViewById(R.id.tvTip);

        dobaviAuto(autoId);
    }

    private void dobaviAuto(int autoId) {
        // URL tvog API-ja koji vraća automobil po ID-u
        String autoUrl = url + "/api/automobiles_by_id/" + autoId;

        // Kreiranje GET zahteva
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, autoUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parsiranje JSON objekta
                            String proizvodjacc = response.getString("proizvodjac");
                            String modell = response.getString("model");
                            String tipp = response.getString("tip");
                            int godistee = response.getInt("godiste");
                            int brojSedistaa = response.getInt("brojSedista");
                            int kilometrazaa = response.getInt("kilometraza");
                            double cenaa = response.getDouble("cenaPoDanu");
                            boolean dostupnostt = response.getBoolean("dostupnost");
                            String putanja = response.isNull("putanja") ? null : response.getString("putanja");

                            // Postavljanje vrednosti u TextView i ImageView
                            proizvodjac.setText(proizvodjacc);
                            model.setText(modell);
                            tip.setText(tipp);
                            godiste.setText(String.valueOf(godistee));
                            brojSedista.setText(String.valueOf(brojSedistaa));
                            kilometraza.setText(String.valueOf(kilometrazaa));
                            String tekstCena = String.valueOf(cenaa) + " €/dan";
                            cena.setText(tekstCena);
                            dostupan.setText(dostupnostt ? "Dostupan" : "Ne dostupan");

                            if (dostupnostt){
                                dostupan.setTextColor(Color.GREEN);
                            } else {
                                dostupan.setTextColor(Color.RED);
                            }

                            if (putanja != null) {
                                Picasso.get()
                                        .load(url+pictureUrl+putanja)
                                        .placeholder(R.drawable.car_placeholder)
                                        .error(R.drawable.ic_launcher_background)
                                        .into(slika);
                            } else {
                                slika.setImageResource(R.drawable.car_placeholder); // Postavite placeholder ako putanja nije dostupna
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DetailsActivity.this, "Greška u parsiranju podataka: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Obrada greške u slučaju neuspešnog poziva
                        Toast.makeText(DetailsActivity.this, "Greška u vezi sa serverom: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Dodajemo zahtev u Volley red za obradu
        Volley.newRequestQueue(getApplicationContext()).add(request);
    }
}
