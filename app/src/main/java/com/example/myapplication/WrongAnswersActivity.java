package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class WrongAnswersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong_answers);

        ArrayList<WrongAnswer> wrongAnswersList = getIntent().getParcelableArrayListExtra("wrongAnswersList");
        if (wrongAnswersList != null && !wrongAnswersList.isEmpty()) {
            LinearLayout containerLayout = findViewById(R.id.containerLayout);
            LayoutInflater inflater = LayoutInflater.from(this);

            for (WrongAnswer wrongAnswer : wrongAnswersList) {
                View itemView = inflater.inflate(R.layout.item_wrong_answer, containerLayout, false);

                TextView questionTextView = itemView.findViewById(R.id.questionTextView);
                RadioGroup choicesRadioGroup = itemView.findViewById(R.id.choicesRadioGroup);
                TextView correctAnswerTextView = itemView.findViewById(R.id.correctAnswerTextView);

                questionTextView.setText(wrongAnswer.getQuestion());

                String[] choices = wrongAnswer.getChoices();
                for (int i = 1; i <= 4; i++) {
                    AppCompatRadioButton radioButton = (AppCompatRadioButton) inflater.inflate(R.layout.custom_radio_button, choicesRadioGroup, false);
                    radioButton.setText(choices[i]);
                    radioButton.setEnabled(false);
                    choicesRadioGroup.addView(radioButton);
                }

                choicesRadioGroup.check(choicesRadioGroup.getChildAt(wrongAnswer.getWrongOptionIndex()).getId());

                String correctAnswer = choices[5];
                correctAnswerTextView.setText("Correct Answer: " + correctAnswer);

                containerLayout.addView(itemView);
            }
        }

        Button finishButton = findViewById(R.id.finishButton);
        finishButton.setOnClickListener(view -> {
            finish();
        });
    }
}

