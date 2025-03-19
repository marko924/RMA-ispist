package com.example.drivenow.utils.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drivenow.R;
import com.example.drivenow.utils.model.Booking;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingHolder> {

    private Context context;
    private List<Booking> bookings;
    private ButtonClickedListeners4 buttonClickedListeners4;

    public BookingAdapter(Context context, List<Booking> bookings, ButtonClickedListeners4 buttonClickedListeners4) {
        this.context = context;
        this.bookings = bookings;
        this.buttonClickedListeners4 = buttonClickedListeners4;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_korisnik_booking, parent, false);
        return new BookingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingHolder holder, int position) {
        Booking booking = bookings.get(position);
        holder.idTextView.setText(String.valueOf(booking.getId()));
        holder.pickupDateTextView.setText(booking.getPickupDate());
        holder.returnDateTextView.setText(booking.getReturnDate());
        holder.nameTextView.setText(booking.getName());
        holder.otkaziButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickedListeners4.onOtkaziClicked(booking);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (bookings == null){
            return 0;
        } else{
            return bookings.size();
        }
    }

    public class BookingHolder extends RecyclerView.ViewHolder{

        EditText idTextView, pickupDateTextView, returnDateTextView;
        TextView nameTextView;
        Button otkaziButton;

        public BookingHolder(@NonNull View itemView) {
            super(itemView);

            idTextView = itemView.findViewById(R.id.editTextText23);
            pickupDateTextView = itemView.findViewById(R.id.editTextText10);
            returnDateTextView = itemView.findViewById(R.id.editTextText3);
            nameTextView = itemView.findViewById(R.id.textView36);
            otkaziButton = itemView.findViewById(R.id.otkazi);
        }
    }

    public interface ButtonClickedListeners4{
        void onOtkaziClicked(Booking booking);
    }
}
