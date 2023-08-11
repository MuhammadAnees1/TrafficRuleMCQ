package com.example.myapplication;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HistoryFragment extends Fragment {

     RecyclerView recyclerView;
     HistoryAdapter historyAdapter;
     List<HistoryItem> historyItemList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = rootView.findViewById(R.id.historyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize historyItemList and populate it from SharedPreferences
        historyItemList = loadScoresFromSharedPreferences();

        historyAdapter = new HistoryAdapter(historyItemList);
        recyclerView.setAdapter(historyAdapter);

        return rootView;
    }

    private List<HistoryItem> loadScoresFromSharedPreferences() {
        List<HistoryItem> itemList = new ArrayList<>();
        SharedPreferences preferences = requireContext().getSharedPreferences("ScoreHistory", MODE_PRIVATE);
        Map<String, ?> scoreMap = preferences.getAll();

        for (Map.Entry<String, ?> entry : scoreMap.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("score_")) {
                int score = preferences.getInt(key, 0);
                long timestamp = preferences.getLong("timestamp_" + key.substring(6), 0);
                itemList.add(new HistoryItem(score, timestamp));
            }
        }

        // Sort the list by timestamp in descending order
        Collections.sort(itemList, (item1, item2) -> Long.compare(item2.getTimestamp(), item1.getTimestamp()));

        return itemList;
    }
}