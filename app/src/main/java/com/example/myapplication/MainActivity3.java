package com.example.myapplication;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity3 extends AppCompatActivity {
    private TextView questionTextView;
     Button  nextButton;
     private int currentQuestionNumber = 0;
     private DatabaseReference databaseReference;
     private RadioGroup option1RadioGroup;
     private TextView timerTextView;
     private CountDownTimer countDownTimer;
     private static final long COUNTDOWN_DURATION = 30000; // 30 seconds
     int Score = 0;
     Map<String, String> questionMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);



        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            Drawable actionBarBackground = getResources().getDrawable(R.drawable.view_background);
            actionBar.setBackgroundDrawable(actionBarBackground);
            // Create a SpannableString to apply custom styles
            SpannableString spannableString = new SpannableString("Traffic Rules Quiz App");
            // Apply color to the title text
            int titleColor = Color.parseColor("#FFFFFFFF");
            spannableString.setSpan(new ForegroundColorSpan(titleColor), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // You can apply other styles as well if needed, for example, bold the text
            spannableString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            // Set the styled SpannableString as the title
            actionBar.setTitle(spannableString);
        }
        questionTextView = findViewById(R.id.questionTextView1);
        option1RadioGroup = findViewById(R.id.optionRadioGroup2);
        timerTextView = findViewById(R.id.timerTextView1);
        nextButton = findViewById(R.id.nextButton);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Questions");
        fetchQuestions();

        nextButton.setOnClickListener(view -> {
            showNextQuestion();
        });
    }
    private void setRadioButtonsBackground(RadioGroup radioGroup) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            View radioButton = radioGroup.getChildAt(i);
            if (radioButton instanceof RadioButton) {
                radioButton.setBackgroundResource(R.drawable.radio_button_background);
            }
        }
    }
    private void fetchQuestions() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
                    String question = questionSnapshot.getKey();
                    String choice = questionSnapshot.getValue().toString();
                    questionMap.put(question, choice);
                }
                // Call the setQuestion method and pass the first question key
                if (!questionMap.isEmpty()) {
                    String firstQuestionKey = questionMap.keySet().iterator().next();
                    setQuestion(firstQuestionKey);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                onError(databaseError.getMessage());
            }
        });
    }
    private void setQuestion(String questionKey) {


        cancelQuestionTimer();

        if (questionMap.containsKey(questionKey)) {
            String[] choicesArray = questionMap.get(questionKey).split(", ");

            questionTextView.setText(questionKey);
            option1RadioGroup.clearCheck();
            option1RadioGroup.removeAllViews();
            for (int i = 1; i < choicesArray.length-1; i++) {
                RadioButton radioButton = new RadioButton(this);

                if (i != 5) {
                    radioButton.setText(choicesArray[i]);
                    radioButton.setId(View.generateViewId());
                    option1RadioGroup.addView(radioButton);
                    setRadioButtonsBackground(option1RadioGroup);

                }
            }
            startTimer();
            nextButton.setBackgroundColor(0xFF808080);
            nextButton.setEnabled(false);

            // Assuming the correct option is at index 5 (choicesArray[5])
            String correctOption = choicesArray[5];
            option1RadioGroup.setOnCheckedChangeListener((group, checkedId) -> {

                RadioButton selectedRadioButton = findViewById(checkedId);
                if (selectedRadioButton != null) {

                    nextButton.setEnabled(true);

                    nextButton.setBackground(getResources().getDrawable(R.drawable.button_background));

                    String selectedOption = selectedRadioButton.getText().toString();

                    Log.d("DEBUG", "Selected Option: " + selectedOption);
                    Log.d("DEBUG", "Correct Option: " + correctOption);

                    for (int i = 0; i < option1RadioGroup.getChildCount(); i++) {
                        option1RadioGroup.getChildAt(i).setEnabled(false);
                    }

                    if (selectedOption.equals(correctOption)) {
                        selectedRadioButton.setBackgroundResource(R.drawable.is);
                        // User selected the correct answer, update the score
                        Log.d("DEBUG", "User selected the correct answer: " + selectedOption);
                        Score++;
                    }
                    else {
                        selectedRadioButton.setBackgroundResource(R.drawable.worng);
                         Log.d("DEBUG", "User selected the wrong answer: " + selectedOption);
                    }
                }
                else {

                    Log.d("DEBUG", "No option selected by the user.");
                }
            });
        } else {
            Toast.makeText(MainActivity3.this, "Your text is not displaying.", Toast.LENGTH_SHORT).show();
        }
    }
    private void onError(String errorMessage) {
        Log.e("ERROR", "Fetching questions failed: " + errorMessage);
    }
     private void showFinalScore() {
        Toast.makeText(MainActivity3.this, "Your score: " + Score, Toast.LENGTH_SHORT).show();
    }
    private void showNextQuestion() {
        if (currentQuestionNumber < questionMap.size() - 1) {
            currentQuestionNumber++;
            String questionKey = questionMap.keySet().toArray()[currentQuestionNumber].toString();
            setQuestion(questionKey);
        } else {
            showFinalScore();
        }
    }
    private void startTimer() {
        countDownTimer = new CountDownTimer(COUNTDOWN_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60);
                timerTextView.setText(timeFormatted);
            }
            public void onFinish() {
                // Timer finished, show the next question
                showNextQuestion();
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
    private void cancelQuestionTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }
}