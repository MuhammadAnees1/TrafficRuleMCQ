package com.example.myapplication;

import java.util.Map;

public interface QuestionCallback {
    void onSuccess(Map<String, String> questionMap);

    void onError(String errorMessage);

}

