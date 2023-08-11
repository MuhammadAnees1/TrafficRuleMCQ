package com.example.myapplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryItem {
    private int score;
    private long timestamp;

    public HistoryItem(int score, long timestamp) {
        this.score = score;
        this.timestamp = timestamp;
    }

    public int getScore() {
        return score;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}
