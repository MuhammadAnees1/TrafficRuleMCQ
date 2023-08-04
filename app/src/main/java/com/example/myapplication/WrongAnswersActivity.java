package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

import java.util.ArrayList;

public class WrongAnswersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong_answers);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            Drawable actionBarBackground = getResources().getDrawable(R.drawable.view_background);
            actionBar.setBackgroundDrawable(actionBarBackground);
            // Create a SpannableString to apply custom styles
            SpannableString spannableString = new SpannableString("Traffic Rules");
            // Apply color to the title text
            int titleColor = Color.parseColor("#FFFFFFFF");
            spannableString.setSpan(new ForegroundColorSpan(titleColor), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // You can apply other styles as well if needed, for example, bold the text
            spannableString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            // Set the styled SpannableString as the title
            actionBar.setTitle(spannableString);
        }

        ArrayList<WrongAnswer> wrongAnswersList = getIntent().getParcelableArrayListExtra("wrongAnswersList");

        Log.d("DEBUG", "No option selected by the user." + wrongAnswersList);

        if (wrongAnswersList != null && !wrongAnswersList.isEmpty()) {
            LinearLayout containerLayout = findViewById(R.id.containerLayout);
            LayoutInflater inflater = LayoutInflater.from(this);

            for (WrongAnswer wrongAnswer : wrongAnswersList) {

                View itemView = inflater.inflate(R.layout.item_wrong_answer, containerLayout, false);

                TextView questionTextView = itemView.findViewById(R.id.questionTextView4);
                RadioGroup choicesRadioGroup = itemView.findViewById(R.id.choicesRadioGroup5);
                TextView correctAnswerTextView = itemView.findViewById(R.id.correctAnswerTextView4);

                questionTextView.setText(wrongAnswer.getQuestion());

                String[] choices = wrongAnswer.getChoices();
                for (int i = 1; i <= 4; i++) {
                    AppCompatRadioButton radioButton = (AppCompatRadioButton) inflater.inflate(R.layout.custom_radio_button, choicesRadioGroup, false);
                    radioButton.setText(choices[i]);
                    radioButton.setEnabled(false);
                    choicesRadioGroup.addView(radioButton);
                }

                int wrongOptionIndex = wrongAnswer.getWrongOptionIndex();
                if (wrongOptionIndex >= 0 && wrongOptionIndex < choicesRadioGroup.getChildCount()) {
                    choicesRadioGroup.check(choicesRadioGroup.getChildAt(wrongOptionIndex).getId());
                }
                String correctAnswer = choices[5];
                correctAnswerTextView.setText("Correct Answer: " + correctAnswer);
                containerLayout.addView(itemView);
            }
        } else {
            Toast.makeText(this, "Congratulations", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(WrongAnswersActivity.this, MainActivity3.class);
            startActivity(intent);
            finish();
        }

        Button finishButton = findViewById(R.id.finishButton);
        finishButton.setOnClickListener(view -> {
            finish();
        });
    }
}