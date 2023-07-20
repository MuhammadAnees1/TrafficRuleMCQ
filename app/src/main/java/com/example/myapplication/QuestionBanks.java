package com.example.myapplication;

//import android.util.Log;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import java.util.HashMap;
//import java.util.Map;
//
//public class QuestionBanks {
//    DatabaseReference databaseReference;
//
//    public QuestionBanks() {
//        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//        databaseReference = firebaseDatabase.getReference("Questions");
//    }
//
//    public void fetchQuestions(QuestionCallback callback) {
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Map<String, String> QuestionMap = new HashMap<>();
//
//                for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
//                    String question = questionSnapshot.getKey();
//                    String choice = questionSnapshot.getValue().toString();
//
//                    QuestionMap.put(question, choice);
//                    Log.d("DEBUG", "Question: " + question);
//                    Log.d("DEBUG", "Choice: " + choice);
//                }
//                // Call the onSuccess method of the callback and pass the questionMap
//                callback.onSuccess(QuestionMap);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Call the onError method of the callback and pass the error message
//                callback.onError(databaseError.getMessage());
//            }
//        });
//    }
//}


