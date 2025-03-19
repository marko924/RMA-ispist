package com.example.drivenow.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.drivenow.R;
import com.example.drivenow.utils.helper.DateTimePickerUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private EditText imeField, prezimeField, emailField, lozinkaField, potvrdaLozinkeField, brojVozackeDozvoleField,
            datumIstekaDozvoleField, datumRodjenjaField, brojTelefonaField, ulicaField, gradField,
            postanskiBrojField;
    private Button registerButton, odustaniButton;
    private CheckBox checkBox1, checkBox2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imeField = findViewById(R.id.imeInput);
        prezimeField = findViewById(R.id.prezimeInput);
        emailField = findViewById(R.id.emailInput2);
        lozinkaField = findViewById(R.id.lozinkaInput2);
        potvrdaLozinkeField = findViewById(R.id.confirmPasswordEditText);
        brojVozackeDozvoleField = findViewById(R.id.brojVozackeDozvoleInput);
        datumIstekaDozvoleField = findViewById(R.id.datumIstekaDozvoleInput);
        datumRodjenjaField = findViewById(R.id.datumRodjenjaInput);
        brojTelefonaField = findViewById(R.id.brojTelefonaInput);
        ulicaField = findViewById(R.id.ulicaInput);
        gradField = findViewById(R.id.gradInput);
        postanskiBrojField = findViewById(R.id.postanskiBrojInput);

        DateTimePickerUtil dateTimePickerUtil1 = new DateTimePickerUtil(this, datumIstekaDozvoleField);
        datumIstekaDozvoleField.setOnClickListener(v -> dateTimePickerUtil1.openDatePicker());

        DateTimePickerUtil dateTimePickerUtil2 = new DateTimePickerUtil(this, datumRodjenjaField);
        datumRodjenjaField.setOnClickListener(v -> dateTimePickerUtil2.openDatePicker());

        registerButton = findViewById(R.id.registerButton);
        odustaniButton = findViewById(R.id.cancelButton);

        checkBox1 = findViewById(R.id.checkBox4);
        checkBox2 = findViewById(R.id.checkBox3);

        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    lozinkaField.setTransformationMethod(null);
                }else {
                    lozinkaField.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    potvrdaLozinkeField.setTransformationMethod(null);
                }else {
                    potvrdaLozinkeField.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        registerButton.setOnClickListener(v -> {
            String ime = imeField.getText().toString().trim();
            String prezime = prezimeField.getText().toString().trim();
            String email = emailField.getText().toString().trim();
            String lozinka = lozinkaField.getText().toString().trim();
            String potvrdaLozinke = potvrdaLozinkeField.getText().toString().trim();
            String brojVozackeDozvole = brojVozackeDozvoleField.getText().toString().trim();
            String datumIstekaDozvole = datumIstekaDozvoleField.getText().toString().trim();
            String datumRodjenja = datumRodjenjaField.getText().toString().trim();
            String brojTelefona = brojTelefonaField.getText().toString().trim();
            String ulica = ulicaField.getText().toString().trim();
            String grad = gradField.getText().toString().trim();
            String postanskiBroj = postanskiBrojField.getText().toString().trim();

            if (TextUtils.isEmpty(ime) || TextUtils.isEmpty(prezime) || TextUtils.isEmpty(email) ||
                    TextUtils.isEmpty(lozinka) || TextUtils.isEmpty(brojVozackeDozvole) ||
                    TextUtils.isEmpty(datumIstekaDozvole) || TextUtils.isEmpty(datumRodjenja) ||
                    TextUtils.isEmpty(brojTelefona) || TextUtils.isEmpty(ulica) ||
                    TextUtils.isEmpty(grad) || TextUtils.isEmpty(postanskiBroj)) {
                Toast.makeText(RegistrationActivity.this, "Molimo unesite sve podatke", Toast.LENGTH_SHORT).show();
            } else if (!lozinka.equals(potvrdaLozinke)) {
                Toast.makeText(RegistrationActivity.this, "Molimo unesite istu lozinku u potvrdi", Toast.LENGTH_SHORT).show();
            } else {
                handleRegistration(ime, prezime, email, lozinka, brojVozackeDozvole, datumIstekaDozvole,
                        datumRodjenja, brojTelefona, ulica, grad, postanskiBroj);
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        odustaniButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void handleRegistration(String ime, String prezime, String email, String lozinka,
                                    String brojVozackeDozvole, String datumIstekaDozvole, String datumRodjenja,
                                    String brojTelefona, String ulica, String grad, String postanskiBroj) {

        String url = "http://192.168.13.112:5000/api/add_user";  // Zameni sa tačnim URL-om servera

        RequestQueue queue = Volley.newRequestQueue(this);

        try {
            // Kreiranje JSON objekta sa korisničkim podacima
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("ime", ime);
            jsonBody.put("prezime", prezime);
            jsonBody.put("email", email);
            jsonBody.put("lozinka", lozinka);
            jsonBody.put("brojDozvole", brojVozackeDozvole);
            jsonBody.put("datumIstekaDozvole", datumIstekaDozvole);
            jsonBody.put("datumRodjenja", datumRodjenja);
            jsonBody.put("brojTelefona", brojTelefona);
            jsonBody.put("ulica", ulica);
            jsonBody.put("grad", grad);
            jsonBody.put("postanskiBroj", postanskiBroj);
            jsonBody.put("putanja", null);

            // Kreiranje POST zahteva
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    response -> {
                        Toast.makeText(getApplicationContext(), "Registracija uspešna!", Toast.LENGTH_SHORT).show();
                    },
                    error -> {
                        Toast.makeText(getApplicationContext(), "Greška pri registraciji!", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json"); // JSON format podataka
                    return headers;
                }
            };

            queue.add(postRequest);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Greška pri kreiranju JSON-a!", Toast.LENGTH_SHORT).show();
        }
    }
}
