package com.example.drivenow.views;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.drivenow.R;
import com.example.drivenow.utils.adapter.BookingAdapter;
import com.example.drivenow.utils.model.Automobil;
import com.example.drivenow.utils.model.Booking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BookingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Booking> bukiranjaKorisnika;
    private Automobil auto;
    private BookingAdapter bookingAdapter;
    private TextView cenaRacuna;
    private double ukupanIznos = 0;
    String url = "http://192.168.13.112:5000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_korisnik_bookings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.korisnikBukiranja), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int korisnikId = getIntent().getIntExtra("id", -1);

        cenaRacuna = findViewById(R.id.textView12);

        bukiranjaKorisnika = new ArrayList<>();
        loadBookings(korisnikId);
        bookingAdapter = new BookingAdapter(this, bukiranjaKorisnika, new BookingAdapter.ButtonClickedListeners4() {
            @Override
            public void onOtkaziClicked(Booking booking) {

                String otkaziUrl = url + "/api/rentals_by_id/" + booking.getId(); // Zameni sa stvarnim URL-om

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, otkaziUrl, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(BookingActivity.this, "Rezervacija uspešno otkazana!", Toast.LENGTH_SHORT).show();

                                ukupanIznos -= booking.getCena();
                                cenaRacuna.setText(String.valueOf(ukupanIznos));

                                // Uklanjanje iz liste i adaptera
                                bukiranjaKorisnika.remove(booking);
                                bookingAdapter.notifyDataSetChanged();

                                // Ažuriraj dostupnost automobila
                                updateAutomobileAvailability(booking.getAutoId());  //dodati auto id u Booking
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(BookingActivity.this, "Greška u vezi sa serverom: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                // Dodajemo zahtev u Volley red za obradu
                Volley.newRequestQueue(BookingActivity.this).add(request);
            }
        });

        recyclerView = findViewById(R.id.recyclerView3);
        recyclerView.setAdapter(bookingAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadBookings(int korisnikId) {
        // URL tvog API-ja koji vraća listu rental locations
        String ucitavanjeUrl = url + "/api/rentals_search/" + korisnikId; // Zameni sa stvarnim URL-om

        // Kreiranje GET zahteva
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, ucitavanjeUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        bukiranjaKorisnika.clear();
                        ukupanIznos = 0;

                        if (response == null || response.length() == 0) {
                            cenaRacuna.setText(String.valueOf(ukupanIznos));
                            // Ako je odgovor null ili prazan, prikažite poruku i izađite iz metode
                            Toast.makeText(BookingActivity.this, "Nemate ni jednu rezervaciju.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject locationObject = response.getJSONObject(i);

                                int id = locationObject.getInt("id");
                                String datumPreuzimanja = locationObject.getString("datumPreuzimanja");
                                String datumVracanja = locationObject.getString("datumVracanja");
                                int brojDana = locationObject.getInt("brojDana");
                                int autoId = locationObject.getInt("autoId");

                                // Koristite callback za dobavljanje automobila
                                getAutomobileById(autoId, new AutomobileCallback() {
                                    @Override
                                    public void onAutomobileLoaded(Automobil automobil) {
                                        String proizvodjac = automobil.getProizvodjac();
                                        String model = automobil.getModel();
                                        int godiste = automobil.getGodiste();
                                        double cena = automobil.getCena();

                                        String name = proizvodjac + " " + model + " " + godiste;

                                        ukupanIznos += brojDana * cena;

                                        bukiranjaKorisnika.add(new Booking(name, id, datumPreuzimanja, datumVracanja, brojDana*cena, autoId));
                                        bookingAdapter.notifyDataSetChanged();
                                        cenaRacuna.setText(String.valueOf(ukupanIznos));
                                    }

                                    @Override
                                    public void onError(String errorMessage) {
                                        Toast.makeText(BookingActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(BookingActivity.this, "Greška u parsiranju podataka!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Obrada greške u slučaju neuspešnog poziva
                        Toast.makeText(BookingActivity.this, "Greška u vezi sa serverom: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Dodajemo zahtev u Volley red za obradu
        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    private void getAutomobileById(int autoId, AutomobileCallback callback){
        // URL tvog API-ja koji vraća listu rental locations
        String automobilUrl = url + "/api/automobiles_by_id/" + autoId;

        // Kreiranje GET zahteva
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, automobilUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            int id = response.getInt("id");
                            String proizvodjac = response.getString("proizvodjac");
                            String model = response.getString("model");
                            String tip = response.getString("tip");
                            int godiste = response.getInt("godiste");
                            int brojSedista = response.getInt("brojSedista");
                            int kilometraza = response.getInt("kilometraza");
                            double cena = response.getDouble("cenaPoDanu");
                            boolean dostupnost = response.getBoolean("dostupnost");
                            String putanja = response.getString("putanja");

                            auto = new Automobil(id, proizvodjac, model, tip, godiste, brojSedista, kilometraza, cena, dostupnost, putanja);
                            callback.onAutomobileLoaded(auto);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError("Greška u parsiranju podataka!");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Obrada greške u slučaju neuspešnog poziva
                        Toast.makeText(BookingActivity.this, "Greška u vezi sa serverom: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Dodajemo zahtev u Volley red za obradu
        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    public interface AutomobileCallback {
        void onAutomobileLoaded(Automobil automobil);
        void onError(String errorMessage);
    }

    private void updateAutomobileAvailability(int autoId) {
        String updateUrl = url + "/api/automobiles_izmeni/" + autoId;

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("dostupnost", true);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, updateUrl, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(BookingActivity.this, "Automobil sada dostupan!", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(BookingActivity.this, "Greška prilikom ažuriranja automobila: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(BookingActivity.this).add(request);
    }
}
