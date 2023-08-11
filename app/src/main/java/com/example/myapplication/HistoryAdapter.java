package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<HistoryItem> historyItems;
    public HistoryAdapter(List<HistoryItem> historyItems) {
        this.historyItems = historyItems;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryItem historyItem = historyItems.get(position);
        holder.scoreTextView.setText("Score: " + historyItem.getScore());
        holder.timestampTextView.setText(historyItem.getFormattedTimestamp());
    }
    @Override
    public int getItemCount() {
        return historyItems.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView scoreTextView;
        TextView timestampTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            scoreTextView = itemView.findViewById(R.id.HistoryScore1);
            timestampTextView = itemView.findViewById(R.id.Date);
        }
    }
}