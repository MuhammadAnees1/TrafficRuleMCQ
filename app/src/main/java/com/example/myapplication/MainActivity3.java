package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
public class MainActivity3 extends AppCompatActivity {
    private TextView questionTextView;
     Button submitButton, nextButton;
     private int currentQuestionNumber = 0;
    private DatabaseReference databaseReference;
    RadioGroup optionRadioGroup;
    Map<String, String> questionMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        questionTextView = findViewById(R.id.questionTextView1);

        optionRadioGroup = findViewById(R.id.optionRadioGroup);

        nextButton = findViewById(R.id.nextButton);
        submitButton = findViewById(R.id.submitButton);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Questions");

        fetchQuestions();

        nextButton.setOnClickListener(view -> {
            if (currentQuestionNumber < questionMap.size() - 1) {
                currentQuestionNumber++;
                String questionKey = questionMap.keySet().toArray()[currentQuestionNumber].toString();
                setQuestion(questionKey);
            } else {
//                showFinalScore();
            }
        });



        submitButton.setOnClickListener(view -> {
            // Submit the answer
            // You can implement your logic here
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

//        Log.d("DEBUG", "Question: " + question);
//        Log.d("DEBUG", "Choice: " + choice);

        if (questionMap.containsKey(questionKey)) {

            String[] choicesArray = questionMap.get(questionKey).split(", ");

            questionTextView.setText(questionKey);

//        Log.d("DEBUG", "Question: " + questionMap);

            optionRadioGroup.clearCheck(); // Clear any previous selection
            optionRadioGroup.removeAllViews(); // Remove any previous RadioButtons

            // Display each choice as a separate RadioButton
            for (int i = 1; i < choicesArray.length; i++) {
                RadioButton radioButton = new RadioButton(this);
                if(i!=5) {
                    radioButton.setText(choicesArray[i]);
                    radioButton.setId(View.generateViewId()); // Set a unique ID for the RadioButton
                    optionRadioGroup.addView(radioButton);
                } else {
                          String option = choicesArray[i];
//                    Log.d("DEBUG", "Question: " + option);
                }
            }
        }
        else {
            Toast.makeText(MainActivity3.this, "Your text is not displaying.", Toast.LENGTH_SHORT).show();
//            Log.d("DEBUG", "Question not found in questionMap: " + question);
        }
    }
    private void onError(String errorMessage) {
        // Handle the error here (e.g., show an error message to the user)
        Log.e("ERROR", "Fetching questions failed: " + errorMessage);
    }
}



