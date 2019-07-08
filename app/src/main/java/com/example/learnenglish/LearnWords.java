package com.example.learnenglish;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class LearnWords extends Activity implements View.OnClickListener {

    private TextView showRusWord;
    private TextView showEngWord;
    SharedPreferences preferences;
    int currentCountOfWords;
    int countOfWordsInDay;
    Database database;
    TextView nextWord;
    int currentDate;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_words);

        showRusWord = findViewById(R.id.word);
        showEngWord = findViewById(R.id.show_translate);
        nextWord = findViewById(R.id.next_word);
        database = new Database(this);

        //ссылаемся на объект, который может быть использован для чтения и записи
        //в файл настроек по умолчанию
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // количество слов, которые пользователь учил за день
        currentCountOfWords = preferences.getInt("currentCountOfWords", 0);

        // из настроек получаем количество слов для изучения,
        // которое выбрал для себя пользователь
        countOfWordsInDay = preferences.getInt("count of words", 5);


        // получаем дату, когда последний раз пользователь изучал слова
        int lastDateWhenEntered = preferences.getInt("last date", 0);

        // получаем текущую дату
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        currentDate = Integer.parseInt(dateFormat.format(new Date()));

        if (currentCountOfWords < countOfWordsInDay) {
            cursor = database.getUnlearnedWords();
            showRusWord.setText(cursor.getString(1));
            showEngWord.setText(cursor.getString(2));
        } else if (currentDate > lastDateWhenEntered) {
            // наступил новый день => можно снова учить слова
            currentCountOfWords = 0;
            cursor = database.getUnlearnedWords();
            showRusWord.setText(cursor.getString(1));
            showEngWord.setText(cursor.getString(2));

        } else {
            //ограничение на изучение до конца дня
            showRusWord.setText("");
            showEngWord.setText("");
            findViewById(R.id.show_recommendation).setVisibility(View.VISIBLE);
            nextWord.setVisibility(View.INVISIBLE);
        }
        nextWord.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.next_word) {

            // получаем id слова, чтобы пометить слово прочтённым
            int id = database.getRusByEng(String.valueOf(showEngWord.getText())).getInt(0);
            database.changeLearned(id, String.valueOf(showEngWord.getText()),
                    String.valueOf(showRusWord.getText()), "almost");

            currentCountOfWords++;

            // создаём объект Editor для записи в файл настроек
            SharedPreferences.Editor editor = preferences.edit();

            //количество слов в день не достигнуто, продолжаем изучение
            if (currentCountOfWords < countOfWordsInDay) {
                cursor.moveToNext();
                showRusWord.setText(cursor.getString(1));
                showEngWord.setText(cursor.getString(2));

                editor.putString("word", cursor.getString(1));
                editor.putString("translate", cursor.getString(2));
            } else {
                //ограничение на изучение до конца дня
                showRusWord.setText("");
                showEngWord.setText("");
                findViewById(R.id.show_recommendation).setVisibility(View.VISIBLE);
                nextWord.setVisibility(View.INVISIBLE);
            }
            editor.putInt("currentCountOfWords", currentCountOfWords);
            editor.putInt("last date", currentDate);
            //сохраняем все добавленные пары
            editor.apply();
        }
    }

    void saveSettingsForLearnWords(int countOfWordsInDay) {
        SharedPreferences preferences = PreferenceManager.
                getDefaultSharedPreferences(LearnWords.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("countOfWordsInDay", countOfWordsInDay);
        editor.apply();
    }
}
