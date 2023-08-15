package com.example.myapplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class HistoryItem {
    private int score, totalScore;
    private long timestamp;

    public HistoryItem(int score, long timestamp, int totalScore) {
        this.score = score;
        this.timestamp = timestamp;
        this.totalScore = totalScore;
    }

    public int getScore() {
        return score;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}
