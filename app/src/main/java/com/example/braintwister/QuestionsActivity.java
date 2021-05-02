package com.example.braintwister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity {
    public static final String FILE_NAME = "Brain Twister";
    public static final String KEY_NAME ="Questions";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    private TextView question,numberindicator;
    private FloatingActionButton bookmarkbtn;
    private LinearLayout optionscontainer;
    private Button share,next;
    private int count=0;
    private List<QuestionModel> questionModelList;
    private int position=0;
    private int score=0;
    private String category;
    private int setNO;
    private Dialog dialog;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private int matchedQuestionPosition;
    private List<QuestionModel> bookmarksList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        question = findViewById(R.id.question);
        numberindicator = findViewById(R.id.number_indicator);
        bookmarkbtn = findViewById(R.id.bookmark_button);
        optionscontainer = findViewById(R.id.option_container);
        share = findViewById(R.id.share_btn);
        next = findViewById(R.id.next_btn);
        preferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();
        getBookmarks();
        bookmarkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modelMatch()){
                    bookmarksList.remove(matchedQuestionPosition);
                    bookmarkbtn.setImageDrawable(getDrawable(R.drawable.bookmark_border));

                }
                else {
                    bookmarksList.add(questionModelList.get(position));
                    bookmarkbtn.setImageDrawable(getDrawable(R.drawable.bookmark));
                }
            }
        });
        category = getIntent().getStringExtra("category");
        setNO = getIntent().getIntExtra("setNO",1);
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.buttonshape));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);


        questionModelList = new ArrayList<>();
        dialog.show();
        reference.child("SETS").child(category).child("questions").orderByChild("setNO").equalTo(setNO).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    questionModelList.add(snapshot.getValue(QuestionModel.class));
                }
                if(questionModelList.size()>0){

                    for(int i=0;i<4;i++){
                        optionscontainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checkAnswer((Button) v);
                            }
                        });
                    }
                    playAnim(question,0,questionModelList.get(position).getQuestion());
                    next.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            next.setEnabled(false);
                            next.setAlpha(0.7f);
                            position++;
                            enbleOption(true);
                            if(position == questionModelList.size()){
                                Intent scoreIntent = new Intent(QuestionsActivity.this,ScoreActivity.class);
                                scoreIntent.putExtra("score",score);
                                scoreIntent.putExtra("total",questionModelList.size());
                                startActivity(scoreIntent);
                                finish();

                                return;
                            }
                            count = 0;
                            playAnim(question,0,questionModelList.get(position).getQuestion());
                        }
                    });
                    share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String body = questionModelList.get(position).getQuestion() + "\n" +
                                    questionModelList.get(position).getOptionA() + "\n" +
                                    questionModelList.get(position).getOptionB() + "\n" +
                                    questionModelList.get(position).getOptionC() + "\n" +
                                    questionModelList.get(position).getOptionD() ;
                            Intent shareintent = new Intent(Intent.ACTION_SEND);
                            shareintent.setType("text/plain");
                            shareintent.putExtra(Intent.EXTRA_SUBJECT,"Brain Twister Challenge");
                            shareintent.putExtra(Intent.EXTRA_TEXT,body);
                            startActivity(Intent.createChooser(shareintent,"Share via"));
                        }
                    });

                }
                else {
                    finish();
                    Toast.makeText(QuestionsActivity.this,"No questions",Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuestionsActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                finish();
            }
        });



    }

    @Override
    protected void onPause() {
        super.onPause();
        storeBookmarks();
    }

    private void playAnim(final View view, final int value, final String data){
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(0)
                .setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
             if (value==0 && count<4 ){
                 String option = "";
                 if(count==0){
                     option = questionModelList.get(position).getOptionA();
                 }
                 else if(count==1){
                     option = questionModelList.get(position).getOptionB();
                 }
                 else if(count==2){
                     option = questionModelList.get(position).getOptionC();
                 }
                 else if(count==3){
                     option = questionModelList.get(position).getOptionD();
                 }
                 playAnim(optionscontainer.getChildAt(count),0,option);
                 count++;
             }
            }

            @Override
            public void onAnimationEnd(Animator animation) {



                if(value==0){
                    try{
                        ((TextView)view).setText(data);
                        numberindicator.setText(position+1+"/"+questionModelList.size());
                        if(modelMatch()){
                            bookmarkbtn.setImageDrawable(getDrawable(R.drawable.bookmark));

                        }
                        else {
                            bookmarkbtn.setImageDrawable(getDrawable(R.drawable.bookmark_border));
                        }
                    }
                    catch (ClassCastException ex){
                        ((Button)view).setText(data);
                    }
                    view.setTag(data);
                    playAnim(view,1,data);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
    private void checkAnswer(Button selectedOption){
        enbleOption(false);
        next.setEnabled(true);
        next.setAlpha(1);
        if(selectedOption.getText().toString().equals(questionModelList.get(position).getCorrectAns())){
            score++;
            selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        }
        else {
            selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
            Button correctoption = optionscontainer.findViewWithTag(questionModelList.get(position).getCorrectAns());
            correctoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        }

    }
    private void enbleOption(boolean enable){
        for (int i=0;i<4;i++){
            optionscontainer.getChildAt(i).setEnabled(enable);
            if(enable){
                optionscontainer.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#989898")));
            }
        }
    }
    private void getBookmarks(){
        String json = preferences.getString(KEY_NAME,"");
        Type type = new TypeToken<List<QuestionModel>>(){}.getType();
        bookmarksList = gson.fromJson(json,type);
        if (bookmarksList == null){
            bookmarksList = new ArrayList<>();
        }
    }
    private boolean modelMatch(){
        boolean matched = false;
        int i=0;
        for(QuestionModel model : bookmarksList){

            if(model.getQuestion().equals(questionModelList.get(position).getQuestion())
            && model.getCorrectAns().equals(questionModelList.get(position).getCorrectAns())
            && model.getSetNO() == questionModelList.get(position).getSetNO()){
                matched = true;
                matchedQuestionPosition = i;
            }
            i++;
        }
        return matched;
    }
    private void storeBookmarks(){
        String json = gson.toJson(bookmarksList);
        editor.putString(KEY_NAME,json);
        editor.commit();
    }
}
