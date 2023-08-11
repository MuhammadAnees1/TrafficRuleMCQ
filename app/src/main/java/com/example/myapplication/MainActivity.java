package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
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

public class MainActivity extends AppCompatActivity {
    private TextView questionTextView;
    Button  nextButton , Retry;
    private DatabaseReference databaseReference;
    private RadioGroup option1RadioGroup;
    private TextView timerTextView;
    private CountDownTimer countDownTimer;
    private static final long COUNTDOWN_DURATION = 15000; // 30 seconds
    static int Score = 0;
    private int maxNumberOfDots = 5; // Maximum number of dots on the timeline
    private TextView[] dotViews = new TextView[maxNumberOfDots];
    private int currentQuestionIndex = 0;
    ProgressBar progressBar;
    View blurOverlay;
    Map<String, String> questionMap = new HashMap<>();
    ArrayList<WrongAnswer> wrongAnswersList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        blurOverlay = findViewById(R.id.blurOverlay);

        blurOverlay.setVisibility(View.VISIBLE);// Show the ProgressBar before fetching questions
        progressBar.setVisibility(View.VISIBLE); // Show the ProgressBar before fetching questions
        Score = 0;
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
            Resources res = getResources();
            Drawable actionBarBackground = ResourcesCompat.getDrawable(res,R.drawable.view_background,null);
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
        Retry=findViewById(R.id.Retry);
        nextButton.setEnabled(false);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Questions");
        fetchQuestions();

        Retry.setOnClickListener(view -> {
            fetchQuestions();
        });

        nextButton.setOnClickListener(view -> {
            showNextQuestion();
        });
    }

    private void fetchQuestions() {
        progressBar.setVisibility(View.VISIBLE);
        blurOverlay.setVisibility(View.VISIBLE);

        // Start a timer for 10 seconds
            // Timer finished, check if data is fetched from Firebase
                if (isConnectedToInternet()) {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            QuizDatabaseHelper dbHelper = new QuizDatabaseHelper(MainActivity.this);
                            SQLiteDatabase db = dbHelper.getWritableDatabase();

                            db.delete(QuizDatabaseHelper.TABLE_QUESTIONS, null, null);

                            for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
                                String question = questionSnapshot.getKey();
                                String choice = questionSnapshot.getValue().toString();

                                ContentValues values = new ContentValues();
                                values.put(QuizDatabaseHelper.COLUMN_QUESTION, question);
                                values.put(QuizDatabaseHelper.COLUMN_CHOICES, choice);
                                long newRowId = db.insert(QuizDatabaseHelper.TABLE_QUESTIONS, null, values);
                            }

                            db.close();
                            loadQuestionsFromDatabase();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            onError(databaseError.getMessage());
                            loadQuestionsFromDatabase();
                        }
                    });
                } else {
                    loadQuestionsFromDatabase();
                }
    }
    private void loadQuestionsFromDatabase() {
        QuizDatabaseHelper dbHelper = new QuizDatabaseHelper(MainActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {QuizDatabaseHelper.COLUMN_QUESTION, QuizDatabaseHelper.COLUMN_CHOICES};
        Cursor cursor = db.query(
                QuizDatabaseHelper.TABLE_QUESTIONS,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        questionMap.clear();
        while (cursor.moveToNext()) {
            String question = cursor.getString(cursor.getColumnIndexOrThrow(QuizDatabaseHelper.COLUMN_QUESTION));
            String choices = cursor.getString(cursor.getColumnIndexOrThrow(QuizDatabaseHelper.COLUMN_CHOICES));
            questionMap.put(question, choices);
        }
        cursor.close();
        db.close();

        if (!questionMap.isEmpty()) {
            String firstQuestionKey = questionMap.keySet().iterator().next();
            setQuestion(firstQuestionKey);
        } else {
            // If there's no data in SQLite, and the timer has finished, show the error message
            new CountDownTimer(10000, 1000) {
                public void onTick(long millisUntilFinished) {
                    progressBar.setVisibility(View.VISIBLE);
                    blurOverlay.setVisibility(View.VISIBLE);
                    // Timer is ticking every second
                }

                public void onFinish() {
                    progressBar.setVisibility(View.GONE);
                    blurOverlay.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "NO internet Connection && data in Database", Toast.LENGTH_SHORT).show();
                    nextButton.setVisibility(View.GONE);
                    Retry.setVisibility(View.VISIBLE);
                }
            }.start();
        }

        progressBar.setVisibility(View.GONE);
        blurOverlay.setVisibility(View.GONE);
    }
    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    // Helper method to check if a question already exists in the database
    private void setQuestion(String questionKey) {
        cancelQuestionTimer();

        QuizDatabaseHelper dbHelper = new QuizDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {QuizDatabaseHelper.COLUMN_QUESTION, QuizDatabaseHelper.COLUMN_CHOICES};
        String selection = QuizDatabaseHelper.COLUMN_QUESTION + " = ?";
        String[] selectionArgs = {questionKey};

        Cursor cursor = db.query(
                QuizDatabaseHelper.TABLE_QUESTIONS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if (cursor.moveToFirst()) {
            String question = cursor.getString(cursor.getColumnIndexOrThrow(QuizDatabaseHelper.COLUMN_QUESTION));
            String choices = cursor.getString(cursor.getColumnIndexOrThrow(QuizDatabaseHelper.COLUMN_CHOICES));

            String[] choicesArray = choices.split(", ");

            questionTextView.setText(question);
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
//                    .error(R.drawable.placeholder_error) // Add an error placeholder drawable
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            // Image loaded successfully, set it to the ImageView and make it visible
                            imageView.setImageDrawable(resource);
                            imageView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // This method is called when the resource is cleared
                            // Set the ImageView visibility to GONE as there was an error loading the image
                            imageView.setVisibility(View.GONE);

                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            // Error loading the image, set the ImageView visibility to GONE
                            imageView.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);// Show the ProgressBar before fetching questions
                        }
                    });

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
                    option1RadioGroup.clearCheck();
                    if (selectedOption.equals(correctOption)) {
                        selectedRadioButton.setBackgroundColor(Color.parseColor("#80cf88"));
                        // User selected the correct answer, update the score
                        Log.d("DEBUG", "User selected the correct answer: " + selectedOption);
                        selectedRadioButton.setTextColor(Color.WHITE);

                        Score++;
                    } else {
                        int wrongOptionIndex = -1;
                        for (int i = 0; i < choicesArray.length; i++) {
                            if (selectedOption.equals(choicesArray[i])) {
                                wrongOptionIndex = i;
                                break;
                            }}
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
                } else {
                    Log.d("DEBUG", "No option selected by the user.");
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Your text is not displaying.", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();
    }
    private void onError(String errorMessage) {
        Log.e("ERROR", "Fetching questions failed: " + errorMessage);
    }
    private void showFinalScore() {
        Intent intent = new Intent(MainActivity.this, WrongAnswersActivity.class);
        intent.putExtra("scoreKey", Score);
        intent.putParcelableArrayListExtra("wrongAnswersList", wrongAnswersList);
        startActivity(intent);
    }

    private void showNextQuestion() {
        progressBar.setVisibility(View.VISIBLE);// Show the ProgressBar before fetching questions
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
        progressBar.setVisibility(View.GONE);
    }
    // Modify the startTimer() method as follows:
    private void startTimer() {
        countDownTimer = new CountDownTimer(COUNTDOWN_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60);
                timerTextView.setText(timeFormatted);
            }
            public void onFinish() {
                handleTimeUp();
            }
        }.start();
    }
    private void handleTimeUp() {
        cancelQuestionTimer(); // Cancel the timer early
        String currentQuestionKey = questionMap.keySet().toArray()[currentQuestionIndex].toString();

        // Assuming the correct option is at index 5 (choicesArray[5])
        String[] choicesArray = questionMap.get(currentQuestionKey).split(", ");
        String correctOption = choicesArray[5];

        // Check if the correct option is already selected by the user
        if (option1RadioGroup.getCheckedRadioButtonId() == -1) {
            // No option selected by the user within the time limit (time ran out)
            // Create a new WrongAnswer object and add it to the list
            int correctOptionIndex = -1;
            for (int i = 0; i < choicesArray.length; i++) {
                if (correctOption.equals(choicesArray[i])) {
                    correctOptionIndex = i;
                    break;
                }
            }
            if (correctOptionIndex != -1) {
                WrongAnswer wrongAnswer = new WrongAnswer(currentQuestionKey, choicesArray, correctOptionIndex);
                wrongAnswersList.add(wrongAnswer);
            }
        }
        // Move to the next question
        showNextQuestion();
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