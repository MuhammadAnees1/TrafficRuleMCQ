package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {
    private TextView questionTextView;
    private RadioButton option1RadioButton, option2RadioButton, option3RadioButton, option4RadioButton;
    Button submitButton, nextButton;
    private TextView timerTextView;
    QuestionBank questionBank;
    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private CountDownTimer countDownTimer;
    private static final long COUNTDOWN_DURATION = 30000; // 30 seconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RadioGroup answerOptionsContainer = findViewById(R.id.optionRadioGroup);

        List<View> answerOptions = new ArrayList<>();

        for (int i = 0; i < answerOptionsContainer.getChildCount(); i++) {
            RadioButton answerOptionView = (RadioButton) answerOptionsContainer.getChildAt(i);
            answerOptions.add(answerOptionView);
        }
        // Initialize the views
        questionTextView = findViewById(R.id.questionTextView);
        option1RadioButton = findViewById(R.id.option1RadioButton);
        option2RadioButton = findViewById(R.id.option2RadioButton);
        option3RadioButton = findViewById(R.id.option3RadioButton);
        option4RadioButton = findViewById(R.id.option4RadioButton);
        submitButton = findViewById(R.id.submitButton);
        nextButton = findViewById(R.id.nextButton);
        timerTextView = findViewById(R.id.timerTextView);


        questionBank = new QuestionBank();
        questionList = questionBank.getQuestionList();

        displayQuestion();


        submitButton.setOnClickListener(view -> {

            disableOptions();

            int selectedAnswer = getSelectedAnswer();
            Question currentQuestion = questionList.get(currentQuestionIndex);
            int correctAnswerIndex = currentQuestion.getCorrectAnswerIndex();

            for (int i = 0; i < answerOptions.size(); i++) {

                View answerOptionView = answerOptions.get(i);

                if (i == correctAnswerIndex) {

                    answerOptionView.setBackgroundColor(Color.GREEN);
                } else {

                    answerOptionView.setBackgroundColor(Color.RED);
                }
            }
            if (selectedAnswer == correctAnswerIndex) {
                score++;
            }
            submitButton.setVisibility(View.INVISIBLE);
        });

        nextButton.setOnClickListener(view -> {
            if (currentQuestionIndex < questionList.size() - 1) {
                currentQuestionIndex++;
                displayQuestion();
            } else {
                showFinalScore();
            }
        });
    }
    private void disableOptions() {
        option1RadioButton.setEnabled(false);
        option2RadioButton.setEnabled(false);
        option3RadioButton.setEnabled(false);
        option4RadioButton.setEnabled(false);
    }
    private void resetOptionColor(RadioButton radioButton) {
        radioButton.setTextColor(Color.BLACK); // Set the default color to black
        radioButton.setBackgroundResource(R.drawable.mcq_background);
    }
    private void displayQuestion() {
        option1RadioButton.setEnabled(true);
        option2RadioButton.setEnabled(true);
        option3RadioButton.setEnabled(true);
        option4RadioButton.setEnabled(true);
        // Get the current question
        Question currentQuestion = questionList.get(currentQuestionIndex);
        String questionText = currentQuestion.getQuestionText();
        List<String> options = currentQuestion.getOptions();
        // Display the question and answer options
        questionTextView.setText(questionText);
        option1RadioButton.setText(options.get(0));
        option2RadioButton.setText(options.get(1));
        option3RadioButton.setText(options.get(2));
        option4RadioButton.setText(options.get(3));
        resetTimer();
        resetOptionColor(option1RadioButton);
        resetOptionColor(option2RadioButton);
        resetOptionColor(option3RadioButton);
        resetOptionColor(option4RadioButton);

        submitButton.setVisibility(View.VISIBLE);
        // Set the selected answer
        option1RadioButton.setChecked(false);
        option2RadioButton.setChecked(false);
        option3RadioButton.setChecked(false);
        option4RadioButton.setChecked(false);
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        startTimer();
    }

    private int getSelectedAnswer() {
        if (option1RadioButton.isChecked()) {
            return 0;
        } else if (option2RadioButton.isChecked()) {
            return 1;
        } else if (option3RadioButton.isChecked()) {
            return 2;
        } else if (option4RadioButton.isChecked()) {
            return 3;
        } else {
            return -1; // No option selected
        }
    }

    private void showFinalScore() {
        // Display the final score to the user
        Toast.makeText(MainActivity2.this, "Your score: " + score, Toast.LENGTH_SHORT).show();

        // TODO: Perform any additional actions at the end of the quiz
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(COUNTDOWN_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60);
                timerTextView.setText(timeFormatted);
            }

            @Override
            public void onFinish() {
                // Timer has finished, increment currentQuestionIndex
                if (currentQuestionIndex < questionList.size() - 1) {
                    currentQuestionIndex++;
                    displayQuestion();
                    startTimer(); // Start the timer for the next question
                } else {
                    // The quiz is complete
                    showFinalScore();
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}