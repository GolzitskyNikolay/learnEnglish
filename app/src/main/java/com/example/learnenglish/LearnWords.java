package com.example.learnenglish;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LearnWords extends Activity implements View.OnClickListener {

    private TextView howManyWordsWatched;
    SharedPreferences.Editor editor;
    SharedPreferences preferences;
    private int currentCountOfWords;
    private int countOfWordsInDay;
    private TextView showRusWord;
    private TextView showEngWord;
    private Database database;
    private TextView nextWord;
    private int currentDate;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_words);

        nextWord = findViewById(R.id.next_word);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        if (preferences.getBoolean("firstLaunch", true)) {
            showRecommendation();
        } else {
            startStudy();
        }
        editor.apply();
    }

    private void showRecommendation() {
        nextWord.setVisibility(View.INVISIBLE);
        findViewById(R.id.park).setVisibility(View.INVISIBLE);
        findViewById(R.id.first_launch).setVisibility(View.VISIBLE);
        final Button goToSettings = findViewById(R.id.go_to_settings);
        final Button next = findViewById(R.id.next);
        next.setVisibility(View.VISIBLE);
        goToSettings.setVisibility(View.VISIBLE);

        editor.putBoolean("firstLaunch", false);
        editor.apply();

        goToSettings.setOnClickListener(v ->
                startActivity(new Intent(LearnWords.this, Settings.class)));

        next.setOnClickListener(v -> {
            nextWord.setVisibility(View.VISIBLE);
            findViewById(R.id.park).setVisibility(View.VISIBLE);
            findViewById(R.id.first_launch).setVisibility(View.INVISIBLE);
            goToSettings.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);

            startStudy();
        });
    }

    private void startStudy() {
        showRusWord = findViewById(R.id.word);
        showEngWord = findViewById(R.id.show_translate);
        howManyWordsWatched = findViewById(R.id.howManyWordsWatched);
        database = new Database(this);

        currentCountOfWords = preferences.getInt("currentCountOfWords", 0);
        countOfWordsInDay = preferences.getInt("count of words", 5);

        int lastDateWhenEntered = preferences.getInt("last date", 0);

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        currentDate = Integer.parseInt(dateFormat.format(new Date()));

        if (currentCountOfWords < countOfWordsInDay &&
                !preferences.getBoolean("hasLimit", false)) {
            showWordAndTranslate();

        } else if (currentDate > lastDateWhenEntered) {
            currentCountOfWords = 0;
            editor.putBoolean("hasLimit", false);
            showWordAndTranslate();

        } else {
            showRusWord.setText("");
            showEngWord.setText("");
            findViewById(R.id.show_recommendation).setVisibility(View.VISIBLE);
            nextWord.setVisibility(View.INVISIBLE);
        }
        findViewById(R.id.back_button_learnWords).setOnClickListener(this);
        nextWord.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.next_word) {

            int id = cursor.getInt(0);
            database.changeLearned(id, String.valueOf(showEngWord.getText()),
                    String.valueOf(showRusWord.getText()), "almost");
            currentCountOfWords++;
            showHowManyWordsWatched();

            if (currentCountOfWords < countOfWordsInDay) {
                cursor.moveToNext();
                showRusWord.setText(cursor.getString(1));
                showEngWord.setText(cursor.getString(2));

                editor.putString("word", cursor.getString(1));
                editor.putString("translate", cursor.getString(2));
            } else {
                showRusWord.setText("");
                showEngWord.setText("");
                findViewById(R.id.show_recommendation).setVisibility(View.VISIBLE);
                nextWord.setVisibility(View.INVISIBLE);

                editor.putBoolean("hasLimit", true);
                editor.apply();
            }
            editor.putInt("currentCountOfWords", currentCountOfWords);
            editor.putInt("last date", currentDate);
            editor.apply();
        } else if (v.getId() == R.id.back_button_learnWords) {
            finish();
        }
    }

    private void showWordAndTranslate() {
        cursor = database.getKindOfWords("no");
        showRusWord.setText(cursor.getString(1));
        showEngWord.setText(cursor.getString(2));
        showHowManyWordsWatched();
    }

    private void showHowManyWordsWatched() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(currentCountOfWords);
        stringBuilder.append("/");
        stringBuilder.append(countOfWordsInDay);
        howManyWordsWatched.setText(stringBuilder);
    }
}
