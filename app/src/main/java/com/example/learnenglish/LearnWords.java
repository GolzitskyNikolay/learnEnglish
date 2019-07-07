package com.example.learnenglish;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import static com.example.learnenglish.MainActivity.database;

public class LearnWords extends Activity implements View.OnClickListener {

    private TextView showRusWord;
    private TextView showEngWord;
    SharedPreferences preferences;
    TextView nextWord;
    Settings settings;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_words);

        showRusWord = findViewById(R.id.word);
        showEngWord = findViewById(R.id.show_translate);
        nextWord = findViewById(R.id.next_word);
        settings = new Settings();
        cursor = database.getUnlearnedWords();

        showRusWord.setText(cursor.getString(1));
        showEngWord.setText(cursor.getString(2));

        nextWord.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.next_word) {

            //ссылаемся на объект, который может быть использован для чтения и записи
            //в файл настроек по умолчанию
            preferences = PreferenceManager.getDefaultSharedPreferences(LearnWords.this);

            // получаем id слова, чтобы пометить слово прочтённым
            int id = database.getRusByEng(String.valueOf(showEngWord.getText())).getInt(0);
            database.changeLearned(id, String.valueOf(showEngWord.getText()),
                    String.valueOf(showRusWord.getText()), "almost");

            // количество слов, которые пользователь учил за день
            int currentCountOfWords = preferences.getInt("currentCountOfWords", 0);
            currentCountOfWords++;

            int countOfWordsInDay = preferences.getInt("countOfWordsInDay", 5);

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
                currentCountOfWords = 0;
                showRusWord.setText("");
                showEngWord.setText("");
                findViewById(R.id.show_recommendation).setVisibility(View.VISIBLE);
                nextWord.setVisibility(View.INVISIBLE);
            }
            editor.putInt("currentCountOfWords", currentCountOfWords);
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
