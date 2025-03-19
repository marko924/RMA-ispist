package com.example.drivenow.utils.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drivenow.R;
import com.example.drivenow.utils.model.Automobil;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AutomobilAdapter extends RecyclerView.Adapter<AutomobilAdapter.AutomobilHolder> {

    private Context context;
    private List<Automobil> automobili;
    private ButtonClickedListeners2 buttonClickedListeners2;

    public AutomobilAdapter(Context context, List<Automobil> automobili, ButtonClickedListeners2 buttonClickedListeners2) {
        this.context = context;
        this.automobili = automobili;
        this.buttonClickedListeners2 = buttonClickedListeners2;
    }

    public void setRacObjekti(List<Automobil> automobili) {
        this.automobili = automobili;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public AutomobilHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_korisnik_automobil, parent, false);
        return new AutomobilHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AutomobilHolder holder, int position) {
        Automobil auto = automobili.get(position);
        String nazivAuta = auto.getGodiste() + " " + auto.getProizvodjac() + " " + auto.getModel();
        holder.naziv.setText(nazivAuta);
        String cenaAuta = auto.getCena() + "€/dan";
        holder.cena.setText(cenaAuta);
        if (holder.slika.getDrawable() == null){
            holder.slika.setImageResource(R.drawable.ic_launcher_background);
        } else{
            // Učitavanje slike koristeći Picasso
            Picasso.get()
                    .load(auto.getSlikaUrl())  // URL do slike sa servera
                    .placeholder(R.drawable.car_placeholder)  // Slika koja se prikazuje dok se učitava
                    .error(R.drawable.ic_launcher_background)  // Slika ako dođe do greške
                    .into(holder.slika);
        }
        holder.detalji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickedListeners2.onDetaljiClicked(auto);
            }
        });
        holder.bukiraj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickedListeners2.onBukirajClicked(auto);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (automobili == null){
            return 0;
        } else{
            return automobili.size();
        }
    }

    public class AutomobilHolder extends RecyclerView.ViewHolder{

        private TextView naziv, cena;
        private ImageView slika;
        private Button detalji, bukiraj;

        public AutomobilHolder(@NonNull View itemView) {
            super(itemView);

            naziv = itemView.findViewById(R.id.textView32);
            cena = itemView.findViewById(R.id.textView33);
            slika = itemView.findViewById(R.id.imageView);
            detalji = itemView.findViewById(R.id.detaljiVozila);
            bukiraj = itemView.findViewById(R.id.bukirajVozilo);

        }
    }

    public interface ButtonClickedListeners2{
        void onDetaljiClicked(Automobil auto);
        void onBukirajClicked(Automobil auto);
    }
}
