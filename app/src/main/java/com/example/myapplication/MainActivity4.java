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

import java.util.ArrayList;
import java.util.List;
public class MainActivity4 extends AppCompatActivity {
    private TextView questionTextView;
    Button submitButton, nextButton;
    private DatabaseReference databaseReference;
    RadioGroup optionRadioGroup;
    private int currentQuestionIndex = 0;
    List<String> questionList = new ArrayList<>();
    List<String> choiceList = new ArrayList<>();
    String option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        questionTextView = findViewById(R.id.questionTextView2);
        optionRadioGroup = findViewById(R.id.optionRadioGroup5);
        nextButton = findViewById(R.id.nextButton1);
        submitButton = findViewById(R.id.submitButton1);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Questions");

        fetchQuestions();

        nextButton.setOnClickListener(view -> {
            // Increment the current question index to move to the next question
            currentQuestionIndex++;
            // Check if there are more questions to display
            if (currentQuestionIndex < questionList.size()) {
                // Get the question and choices based on the current index
                String question = questionList.get(currentQuestionIndex);
                String choices = choiceList.get(currentQuestionIndex);
                // Display the question and choices
                setQuestion(question, choices);
            } else {
                // If there are no more questions, display a message or handle as desired
                // For example, you can reset the index to display the questions again
                currentQuestionIndex = 0;
                // Or show a message indicating that all questions have been displayed
            }
        });
        submitButton.setOnClickListener(view -> {
            submitButton.setVisibility(View.INVISIBLE);
            // Get the ID of the selected RadioButton from the RadioGroup
            int selectedRadioButtonId = optionRadioGroup.getCheckedRadioButtonId();
            // If no RadioButton is selected, selectedRadioButtonId will be -1
            if (selectedRadioButtonId == -1) {
                // Handle the case when no option is selected (show a message or handle as desired)
                Toast.makeText(MainActivity4.this, "Please select an option.", Toast.LENGTH_SHORT).show();
            } else {
                // Find the selected RadioButton based on its ID
                RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                String selectedOption = selectedRadioButton.getText().toString();
                // Compare the selected option with the correct option
                if (selectedOption.equals(option)) {
                    // Handle the case when the selected option is correct
                    Toast.makeText(MainActivity4.this, "Correct!", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle the case when the selected option is incorrect
                    Toast.makeText(MainActivity4.this, "Incorrect!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void fetchQuestions() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
                    String question = questionSnapshot.getKey();
                    String choices = questionSnapshot.getValue().toString();
                    questionList.add(question);
                    choiceList.add(choices);
                }
                if (!questionList.isEmpty()) {
                    setQuestion(questionList.get(0), choiceList.get(0));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Call the onError method and pass the error message
                onError(databaseError.getMessage());
            }
        });
    }
    private void setQuestion(String question, String choices) {
        submitButton.setVisibility(View.VISIBLE);

        // Assuming you have a questionTextView to display the question text
        questionTextView.setText(question);

        String[] choicesArray = choices.split(", ");

        // Assuming you have a questionTextView to display the question text

        questionTextView.setText(question);

        // Assuming you have a RadioGroup to contain the RadioButtons for choices
        optionRadioGroup.clearCheck(); // Clear any previous selection
        optionRadioGroup.removeAllViews(); // Remove any previous RadioButtons

        // Display each choice as a separate RadioButton
        for (int i = 1; i < choicesArray.length; i++) {

            RadioButton radioButton = new RadioButton(this);

            if(i!=5) {
                radioButton.setText(choicesArray[i]);
                radioButton.setId(View.generateViewId()); // Set a unique ID for the RadioButton
                optionRadioGroup.addView(radioButton);
            }// Add the RadioButton to the RadioGroup
            else
            {
                option = choicesArray[i];
                Log.d("DEBUG", "Question: " + option);
            }
        }
    }
    private void onError(String errorMessage) {
        // Handle the error here (e.g., show an error message to the user)
        Log.e("ERROR", "Fetching questions failed: " + errorMessage);
    }
}