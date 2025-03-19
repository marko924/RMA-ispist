package com.example.drivenow.utils.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public abstract class GalleryHandler {
    private final ActivityResultLauncher<Intent> galleryLauncher;
    private final Activity activity;
    private final Fragment fragment;

    // Flag koji čuva da li je korisnik već odobrio pristup galeriji
    private boolean permissionGranted = false;

    // Konstruktor za aktivnosti
    public GalleryHandler(@NonNull AppCompatActivity activity) {
        this.activity = activity;
        this.fragment = null;
        galleryLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> handleGalleryResult(result.getResultCode(), result.getData())
        );
    }

    // Konstruktor za fragmente
    public GalleryHandler(@NonNull Fragment fragment) {
        this.fragment = fragment;
        this.activity = null;
        galleryLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> handleGalleryResult(result.getResultCode(), result.getData())
        );
    }

    /**
     * Postavlja klik listener na prosleđeni ImageView.
     * Ako je permissionGranted true, odmah se otvara galerija.
     * Ako nije, prikazuje se dijalog sa pitanjem.
     */
    public void attachGalleryClickListener(final ImageView imageView) {
        imageView.setOnClickListener(v -> {
            if (permissionGranted) {
                openGallery();
            } else {
                Context context = getContext();
                if (context != null) {
                    new AlertDialog.Builder(context)
                            .setTitle("Dozvola za pristup galeriji")
                            .setMessage("Da li dozvoljavate aplikaciji pristup vašim slikama?")
                            .setPositiveButton("Da", (dialog, which) -> {
                                permissionGranted = true; // Zapamti da je dozvola data
                                openGallery();
                            })
                            .setNegativeButton("Ne", (dialog, which) -> dialog.dismiss())
                            .create()
                            .show();
                } else {
                    throw new IllegalStateException("Context je null. Proveri inicijalizaciju GalleryHandler-a.");
                }
            }
        });
    }

    // Otvara galeriju koristeći ACTION_OPEN_DOCUMENT
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        galleryLauncher.launch(intent);
    }

    // Obrada rezultata iz galerije
    private void handleGalleryResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            Context context = getContext();
            if (context != null) {
                // Trajno odobrenje za URI (ako je potrebno)
                context.getContentResolver().takePersistableUriPermission(
                        imageUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );
                onImageSelected(imageUri);
            } else {
                throw new IllegalStateException("Context je null. Ne mogu obraditi rezultat galerije.");
            }
        }
    }

    // Apstraktna metoda koju implementira klasa koja nasleđuje
    protected abstract void onImageSelected(Uri imageUri);

    // Pomoćna metoda za dobijanje konteksta
    private Context getContext() {
        if (activity != null) {
            return activity;
        } else if (fragment != null && fragment.getContext() != null) {
            return fragment.getContext();
        } else {
            return null;
        }
    }
}
