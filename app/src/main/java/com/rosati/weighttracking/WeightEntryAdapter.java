package com.rosati.weighttracking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weighttracking.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeightEntryAdapter extends RecyclerView.Adapter<WeightEntryAdapter.ViewHolder> {

    private final List<WeightEntry> entries;
    private final OnDeleteClickListener deleteListener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public interface OnDeleteClickListener {
        void onDeleteClick(WeightEntry entry);
    }

    public WeightEntryAdapter(List<WeightEntry> entries, OnDeleteClickListener deleteListener) {
        this.entries = entries;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weight_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeightEntry entry = entries.get(position);
        holder.dateText.setText(dateFormat.format(new Date(entry.timestamp)));
        holder.weightText.setText(String.format(Locale.getDefault(), "%.1f", entry.weight));
        holder.deleteButton.setOnClickListener(v -> deleteListener.onDeleteClick(entry));
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView dateText;
        public final TextView weightText;
        public final ImageButton deleteButton;

        public ViewHolder(View view) {
            super(view);
            dateText = view.findViewById(R.id.date_text);
            weightText = view.findViewById(R.id.weight_text);
            deleteButton = view.findViewById(R.id.button_delete);
        }
    }
}
