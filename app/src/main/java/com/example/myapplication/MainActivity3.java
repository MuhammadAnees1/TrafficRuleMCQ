package com.example.myapplication;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
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
     private static final long COUNTDOWN_DURATION = 15000; // 30 seconds
    int Score = 0;
    private int maxNumberOfDots = 5; // Maximum number of dots on the timeline
    private TextView[] dotViews = new TextView[maxNumberOfDots];
    private int currentQuestionIndex = 0;
    Map<String, String> questionMap = new HashMap<>();
    ArrayList<WrongAnswer> wrongAnswersList = new ArrayList<>();

    @Override
            protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
// timeline
        dotViews[0] = findViewById(R.id.dotView1);
        dotViews[1] = findViewById(R.id.dotView2);
        dotViews[2] = findViewById(R.id.dotView3);
        dotViews[3] = findViewById(R.id.dotView4);
        dotViews[4] = findViewById(R.id.dotView5);

        for (int i = 0; i < maxNumberOfDots; i++) {
            dotViews[i].setTextColor(Color.parseColor("#000000"));
            dotViews[i].setText(String.valueOf(i + 1));
        }
        // Set initial color for the first question number
        dotViews[currentQuestionIndex % maxNumberOfDots].setTextColor(Color.parseColor("#FFFFFF"));


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


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

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
                AppCompatRadioButton radioButton = (AppCompatRadioButton) getLayoutInflater().inflate(R.layout.custom_radio_button, option1RadioGroup, false);
                if (i != 5) {
                    radioButton.setText(choicesArray[i]);
                    radioButton.setId(View.generateViewId());
                    option1RadioGroup.addView(radioButton);
                }
            }
            String imageURL = choicesArray[choicesArray.length - 1];
            ImageView imageView = findViewById(R.id.questionImageView4);
            Glide.with(this)
                    .load(imageURL)
//                    .placeholder(R.drawable.sign1)
                    .into(imageView);

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
                        selectedRadioButton.setBackgroundColor(Color.parseColor("#80cf88"));
                        // User selected the correct answer, update the score
                        Log.d("DEBUG", "User selected the correct answer: " + selectedOption);
                        selectedRadioButton.setTextColor(Color.WHITE);

                        Score++;
                    }
                    else {
                        int wrongOptionIndex = -1;
                        for (int i = 0; i < choicesArray.length; i++) {
                            if (selectedOption.equals(choicesArray[i])) {
                                wrongOptionIndex = i;
                                break;
                            }
                        }

                        if (wrongOptionIndex != -1) {
                            // Create a new WrongAnswer object and add it to the list
                            WrongAnswer wrongAnswer = new WrongAnswer(questionKey, choicesArray, wrongOptionIndex);
                            wrongAnswersList.add(wrongAnswer);

                            selectedRadioButton = findViewById(checkedId);
                            if (selectedRadioButton != null) {
                                selectedRadioButton.setBackgroundColor(Color.parseColor("#FA5959"));
                                selectedRadioButton.setTextColor(Color.WHITE);
                            }
                        }
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
         Intent intent = new Intent(MainActivity3.this, WrongAnswersActivity.class);
         intent.putParcelableArrayListExtra("wrongAnswersList", (ArrayList<? extends Parcelable>) wrongAnswersList);
         startActivity(intent);

     }
            private void showNextQuestion() {
        // Remove color for the current question number
        dotViews[currentQuestionIndex % maxNumberOfDots].setTextColor(Color.parseColor("#000000"));

        if (currentQuestionIndex < questionMap.size() - 1) {
            currentQuestionIndex++;
            String questionKey = questionMap.keySet().toArray()[currentQuestionIndex].toString();
            setQuestion(questionKey);
        } else {
            showFinalScore();
            cancelQuestionTimer();
        }

        // Set text for the new group of question numbers on the dots
        for (int i = 0; i < maxNumberOfDots; i++) {
            int number = (currentQuestionIndex / maxNumberOfDots) * maxNumberOfDots + (i + 1);
            dotViews[i].setText(String.valueOf(number));
        }

        // Set color for the new current question number
        dotViews[currentQuestionIndex % maxNumberOfDots].setTextColor(Color.parseColor("#FFFFFF"));
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