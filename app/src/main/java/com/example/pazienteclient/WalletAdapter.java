package com.example.pazienteclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.WalletViewHolder> {

    private final Context context;
    private final List<Prescription> prescriptions;
    private final OnItemClickListener listener;

    // Interfaccia per il click
    public interface OnItemClickListener {
        void onItemClick(int position, Prescription prescription);
    }

    public WalletAdapter(Context context, List<Prescription> prescriptions, OnItemClickListener listener) {
        this.context = context;
        this.prescriptions = prescriptions;
        this.listener = listener;
    }

    // ViewHolder per la card
    static class WalletViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNumber;
        TextView textViewDate;
        TextView textViewidIPFS;
        CardView cardView;

        public WalletViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNumber = itemView.findViewById(R.id.textViewNumber);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewidIPFS = itemView.findViewById(R.id.idIPFS);
            cardView = (CardView) itemView;
        }
    }

    @NonNull
    @Override
    public WalletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_wallet_card, parent, false);
        return new WalletViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletViewHolder holder, int position) {
        // Numero (posizione + 1)
        holder.textViewNumber.setText(String.format("%d", position + 1));

        // Data dates.get(position)
        String isoDate = prescriptions.get(position).getDate();
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(isoDate);
        LocalDateTime localDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");
        String formattedDate = localDateTime.format(formatter);
        holder.textViewDate.setText(String.format("Data: %s", formattedDate));

        // ID Paziente
        holder.textViewidIPFS.setText(prescriptions.get(position).getIpfsHash());

        // Listener di click
        holder.cardView.setOnClickListener(v -> listener.onItemClick(position, prescriptions.get(position)));
    }

    @Override
    public int getItemCount() {
        return prescriptions.size();
    }
}
