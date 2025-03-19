package com.example.drivenow.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.drivenow.R;
import com.example.drivenow.utils.adapter.AutomobilAdapter;
import com.example.drivenow.utils.model.Automobil;
import com.example.drivenow.utils.model.RacObjekat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AutomobiliActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Automobil> automobili;
    private AutomobilAdapter automobilAdapter;
    private String url = "http://192.168.13.112:5000";
    private String pictureUrl = "/static/slike/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_korisnik_automobili);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.korisnikAutomobili), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int objekatId = getIntent().getIntExtra("objekatId", 0);

        automobili = new ArrayList<>();
        automobilAdapter = new AutomobilAdapter(this, automobili, new AutomobilAdapter.ButtonClickedListeners2() {
            @Override
            public void onDetaljiClicked(Automobil auto) {
                Intent intent = new Intent(AutomobiliActivity.this, DetailsActivity.class);
                int id = auto.getId();
                intent.putExtra("autoId", id);
                startActivity(intent);
            }

            @Override
            public void onBukirajClicked(Automobil auto) {
                if (auto.isDostupnost()) {
                    Intent intent = new Intent(AutomobiliActivity.this, BookingProcesActivity.class);
                    int id = auto.getId();
                    intent.putExtra("autoId", id);
                    int racObjekatId = getIntent().getIntExtra("objekatId", -1);
                    intent.putExtra("objekatId", racObjekatId);
                    startActivity(intent);
                } else {
                    Toast.makeText(AutomobiliActivity.this, "Ovaj auto nije dostupan za bukiranje", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView = findViewById(R.id.rcyclerView2);
        recyclerView.setAdapter(automobilAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getAllAutomobiles(objekatId);
    }

    @Override
    public void onResume() {
        super.onResume();

        int objekatId = getIntent().getIntExtra("objekatId", 0);
        getAllAutomobiles(objekatId);

    }

    private void getAllAutomobiles(int objekatId){
        // URL tvog API-ja koji vraća listu rental locations
        String automobilesUrl = url + "/api/automobiles"; // Zameni sa stvarnim URL-om

        // Kreiranje GET zahteva
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, automobilesUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        automobili.clear();
                        try {

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject locationObject = response.getJSONObject(i);

                                int id = locationObject.getInt("id");
                                String proizvodjac = locationObject.getString("proizvodjac");
                                String model = locationObject.getString("model");
                                String tip = locationObject.getString("tip");
                                int godiste = locationObject.getInt("godiste");
                                int brojSedista = locationObject.getInt("brojSedista");
                                int kilometraza = locationObject.getInt("kilometraza");
                                double cena = locationObject.getDouble("cenaPoDanu");
                                boolean dostupnost = locationObject.getBoolean("dostupnost");
                                String putanja = locationObject.getString("putanja");
                                int racObjekatId = locationObject.getInt("racObjekatId");

                                if (racObjekatId == objekatId)
                                    automobili.add(new Automobil(id, proizvodjac, model, tip, godiste, brojSedista, kilometraza, cena, dostupnost, url+pictureUrl+putanja));
                            }
                            automobilAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AutomobiliActivity.this, "Greška u parsiranju podataka!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Obrada greške u slučaju neuspešnog poziva
                        Toast.makeText(AutomobiliActivity.this, "Greška u vezi sa serverom: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Dodajemo zahtev u Volley red za obradu
        Volley.newRequestQueue(getApplicationContext()).add(request);
    }
}
