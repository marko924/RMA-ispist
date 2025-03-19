package com.example.drivenow.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.drivenow.R;
import com.example.drivenow.utils.adapter.RacObjekatAdapter;
import com.example.drivenow.utils.model.RacObjekat;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class KorisnikActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<RacObjekat> objekti;
    private RacObjekatAdapter racObjekatAdapter;
    private TextView userNameTextView, userEmailTextView;
    private ImageView profileImageView;
    private String url = "http://192.168.13.112:5000";
    private String pictureUrl = "/static/slike/";
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_korisnik_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.korisnik), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int korisnikId = getIntent().getIntExtra("id", 0);

        // Inicijalizacija Toolbar-a
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        objekti = new ArrayList<>();
        racObjekatAdapter = new RacObjekatAdapter(this, objekti);

        recyclerView = findViewById(R.id.korisnikRV);
        recyclerView.setAdapter(racObjekatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getALLRacObjekti();

        // Inicijalizacija DrawerLayout-a i NavigationView-a
        drawerLayout = findViewById(R.id.korisnik);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        View headerView = navigationView.getHeaderView(0);
        userNameTextView = headerView.findViewById(R.id.user_name);
        userEmailTextView = headerView.findViewById(R.id.user_email);
        profileImageView = headerView.findViewById(R.id.profile_image);

        getUsersFromApi(korisnikId);

        // Povezivanje Toolbar-a sa NavigationView-om
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Obrada događaja za klik na stavke u NavigationView-u
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_logOut) {
                finish();
                Toast.makeText(this, "Log Out selected", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.menu_bookings) {
                Intent intent = new Intent(KorisnikActivity.this, BookingActivity.class);
                intent.putExtra("id", korisnikId);
                startActivity(intent);
                Toast.makeText(this, "Bookings selected", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.menu_profile) {
                Intent intent = new Intent(KorisnikActivity.this, ProfileActivity.class);
                intent.putExtra("id", korisnikId);
                startActivity(intent);
                Toast.makeText(this, "Profile selected", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.korisnik_menu, menu);

        MenuItem.OnActionExpandListener onActionExpandListener = new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(@NonNull MenuItem item) {
                Toast.makeText(KorisnikActivity.this, "Pretraga se prosirila", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(@NonNull MenuItem item) {
                Toast.makeText(KorisnikActivity.this, "Pretraga se smanjila", Toast.LENGTH_SHORT).show();
                return true;
            }
        };
        menu.findItem(R.id.search).setOnActionExpandListener(onActionExpandListener);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Pretrazi ovde...");

        // Dodajte pretragu sa listenerom
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterResults(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // Ako je upit prazan, vratite originalnu listu
                    racObjekatAdapter.setRacObjekti(objekti);
                    racObjekatAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        int korisnikId = getIntent().getIntExtra("id", 0);
        getUsersFromApi(korisnikId);

    }

    private void filterResults(String query) {
        // URL za direktno filtriranje rent-a-car objekata
        String filterUrl = url + "/api/search_rental_locations?query=" + query;

        // Kreiranje GET zahteva
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, filterUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<RacObjekat> filteredObjekti = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject locationObject = response.getJSONObject(i);

                                int id = locationObject.getInt("id");
                                String putanja = locationObject.getString("putanja");

                                filteredObjekti.add(new RacObjekat(id, url+pictureUrl+putanja));
                            }

                            // Ažuriranje adaptera sa filtriranim rezultatima
                            if (!filteredObjekti.isEmpty()){
                                racObjekatAdapter.setRacObjekti(filteredObjekti);
                                racObjekatAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(KorisnikActivity.this, "Nema ni jednog podudaranja!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(KorisnikActivity.this, "Greška u parsiranju podataka!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(KorisnikActivity.this, "Greška u vezi sa serverom: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Dodajemo zahtev u Volley red za obradu
        Volley.newRequestQueue(getApplicationContext()).add(request);
    }


    private void getUsersFromApi(int userId) {
        // URL tvog API-ja koji vraća listu korisnika
        String usersUrl = url + "/api/users"; // Zameni sa stvarnim URL-om

        // Kreiranje GET zahteva
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, usersUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Parsiranje JSON odgovora i traženje korisnika sa odgovarajućim ID-jem
                            boolean userFound = false;

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject userObject = response.getJSONObject(i);

                                int id = userObject.getInt("id");
                                if (id == userId) {
                                    // Ako je ID korisnika isti kao prosleđeni ID, izvući podatke
                                    String ime = userObject.getString("ime");
                                    String prezime = userObject.getString("prezime");
                                    String email = userObject.getString("email");
                                    String putanja = userObject.getString("putanja");

                                    // Prikazivanje podataka u TextView poljima
                                    userNameTextView.setText(ime+ " " + prezime);
                                    userEmailTextView.setText(email);
                                    if (putanja.contains(pictureUrl)) {
                                        Picasso.get()
                                                .load(url + pictureUrl + putanja)
                                                .error(R.drawable.baseline_account_circle_24)
                                                .into(profileImageView);
                                    } else {
                                        Picasso.get()
                                                .load(putanja)
                                                .error(R.drawable.baseline_account_circle_24)
                                                .into(profileImageView);
                                    }

                                    userFound = true;
                                    break; // Prekini petlju nakon što pronađeš korisnika
                                }
                            }

                            if (!userFound) {
                                // Ako korisnik nije pronađen
                                Toast.makeText(KorisnikActivity.this, "Korisnik nije pronađen", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(KorisnikActivity.this, "Greška u parsiranju podataka!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Obrada greške u slučaju neuspešnog poziva
                        Toast.makeText(KorisnikActivity.this, "Greška u vezi sa serverom: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Dodajemo zahtev u Volley red za obradu
        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    private void getALLRacObjekti(){
        // URL tvog API-ja koji vraća listu rental locations
        String objektiUrl = url + "/api/rental_locations"; // Zameni sa stvarnim URL-om

        // Kreiranje GET zahteva
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, objektiUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        objekti.clear();
                        try {

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject locationObject = response.getJSONObject(i);

                                int id = locationObject.getInt("id");
                                String putanja = locationObject.getString("putanja");

                                objekti.add(new RacObjekat(id, url+pictureUrl+putanja));
                            }
                            racObjekatAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(KorisnikActivity.this, "Greška u parsiranju podataka!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Obrada greške u slučaju neuspešnog poziva
                        Toast.makeText(KorisnikActivity.this, "Greška u vezi sa serverom: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Dodajemo zahtev u Volley red za obradu
        Volley.newRequestQueue(getApplicationContext()).add(request);
    }
}
