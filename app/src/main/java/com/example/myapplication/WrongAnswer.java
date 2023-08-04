package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class WrongAnswer implements Parcelable {
    private String question;
    private String[] choices;
    private int wrongOptionIndex;

    public WrongAnswer(String question, String[] choices, int wrongOptionIndex) {
        this.question = question;
        this.choices = choices;
        this.wrongOptionIndex = wrongOptionIndex;
    }

    protected WrongAnswer(Parcel in) {
        question = in.readString();
        choices = in.createStringArray();
        wrongOptionIndex = in.readInt();
    }

    public String getQuestion() {
        return question;
    }

    public String[] getChoices() {
        return choices;
    }

    public int getWrongOptionIndex() {
        return wrongOptionIndex;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
        dest.writeStringArray(choices);
        dest.writeInt(wrongOptionIndex);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WrongAnswer> CREATOR = new Creator<WrongAnswer>() {
        @Override
        public WrongAnswer createFromParcel(Parcel in) {
            return new WrongAnswer(in);
        }

        @Override
        public WrongAnswer[] newArray(int size) {
            return new WrongAnswer[size];
        }
    };
}