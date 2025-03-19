package com.example.drivenow.utils.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drivenow.R;
import com.example.drivenow.utils.model.RacObjekat;
import com.example.drivenow.views.AutomobiliActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RacObjekatAdapter extends RecyclerView.Adapter<RacObjekatAdapter.RacObjekatHolder>{

    private Context context;
    private List<RacObjekat> racObjekti;

    public RacObjekatAdapter(Context context, List<RacObjekat> racObjekti) {
        this.context = context;
        this.racObjekti = racObjekti;
    }

    public void setRacObjekti(List<RacObjekat> racObjekti) {
        this.racObjekti = racObjekti;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RacObjekatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_korisnik_rac_obj_element, parent, false);
        return new RacObjekatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RacObjekatAdapter.RacObjekatHolder holder, int position) {
        RacObjekat racObjekat = racObjekti.get(position);
        if (holder.slika.getDrawable() == null){
            holder.slika.setImageResource(R.drawable.ic_launcher_background);
        } else{
            Picasso.get()
                    .load(racObjekat.getSlikaUrl())
                    .placeholder(R.drawable.car_placeholder)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.slika);
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AutomobiliActivity.class);
            intent.putExtra("objekatId", racObjekat.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (racObjekti == null){
            return 0;
        } else{
            return racObjekti.size();
        }
    }

    public class RacObjekatHolder extends RecyclerView.ViewHolder{

        ImageView slika;

        public RacObjekatHolder(@NonNull View itemView) {
            super(itemView);

            slika = itemView.findViewById(R.id.objekatImage);
        }
    }
}
