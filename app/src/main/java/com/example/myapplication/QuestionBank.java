package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class QuestionBank {
    List<Question> questionList;
    public QuestionBank() {
        questionList = new ArrayList<>();
        initializeQuestions();
    }
    private void initializeQuestions() {
        // Example:
        Question question1 = new Question("What is the capital of France?",
                List.of("London", "Paris", "Berlin", "Rome"), 1);
        questionList.add(question1);

        Question question2 = new Question("Which planet is known as the Red Planet?",
                List.of("Venus", "Mars", "Jupiter", "Saturn"), 2);
        questionList.add(question2);


    }
    public List<Question> getQuestionList() {
        return questionList;
    }
}
