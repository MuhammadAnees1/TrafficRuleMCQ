package com.NestTechVentures.trafficquiz.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import com.NestTechVentures.trafficquiz.R;
import com.NestTechVentures.trafficquiz.viewModels.WrongAnswer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
public class WrongAnswersActivity extends AppCompatActivity {
    TextView scoreTextView;
    int scoreValue , totalScore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong_answers);
         scoreTextView = findViewById(R.id.ScoreTextView); // Keep this line


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            Resources res = getResources();
            Drawable actionBarBackground = ResourcesCompat.getDrawable(res,R.drawable.view_background,null);
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
        scoreValue = getIntent().getIntExtra("scoreKey", 0);
        scoreTextView = findViewById(R.id.ScoreTextView);

        scoreTextView.setText("Score: " + scoreValue);

        totalScore= getIntent().getIntExtra("totalScore" ,0);

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

                String imageUrl = wrongAnswer.getImageUrl();
                ImageView imageView = itemView.findViewById(R.id.setImage);
                Glide.with(this)
                        .load(imageUrl)
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
                            }
                        });

                String[] choices = wrongAnswer.getChoices();
                for (int i = 1; i <= 4; i++) {
                    RadioButton radioButton = (AppCompatRadioButton) inflater.inflate(R.layout.custom_radio_button, choicesRadioGroup, false);
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
        }
        else {
            Toast.makeText(this, "Congratulations", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(WrongAnswersActivity.this, CongulationActivity.class);
            startActivity(intent);
            finish();
        }
        Button finishButton = findViewById(R.id.finishButton);
        finishButton.setOnClickListener(view -> {
          finish();
            Intent intent = new Intent(WrongAnswersActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        long currentTimeMillis = System.currentTimeMillis();

        // Save the score and timestamp to SharedPreferences or a database
        // For example, using SharedPreferences
        SharedPreferences preferences = getSharedPreferences("ScoreHistory", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("score_" + currentTimeMillis, scoreValue);
        editor.putInt("totalScore_" + currentTimeMillis, totalScore); // Use consistent key prefix
        editor.putLong("timestamp_" + currentTimeMillis, currentTimeMillis);
        editor.apply();

    }
}