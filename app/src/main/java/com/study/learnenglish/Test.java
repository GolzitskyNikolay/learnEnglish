package com.study.learnenglish;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Test extends Activity implements View.OnClickListener {

    TextView word;
    EditText translate;
    Button button;
    Cursor cursor;
    Animation animation;
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        word = findViewById(R.id.test_word);
        translate = findViewById(R.id.test_translate);
        button = findViewById(R.id.test_button);

        database = new Database(this);
        cursor = database.getKindWords("almost");
        animation = AnimationUtils.loadAnimation(this, R.anim.anim_incorrect_enter);

        // если пользователь уже пытался учить слова
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();

            TextView textView = findViewById(R.id.test_1st_text);
            textView.setVisibility(View.INVISIBLE);
            word.setVisibility(View.VISIBLE);
            translate.setVisibility(View.VISIBLE);

            word.setText(cursor.getString(1));

            button.setOnClickListener(this);
            translate.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    button.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.test_button) {
            String rightAnswer = cursor.getString(2);

            // если перевод слова введён верно
            if (String.valueOf(translate.getText()).toLowerCase().equals(rightAnswer)) {
                database.changeLearned(cursor.getInt(0), cursor.getString(1),
                        cursor.getString(2), "yes");
                showWords();
            } else {
                //пользователь ответил неверно - пусть повторяет слово в LearnWords
                database.changeLearned(cursor.getInt(0), cursor.getString(1),
                        cursor.getString(2), "no");

                // подключаем файл анимации
                translate.startAnimation(animation);
                showWords();
            }
        }
    }

    private void showWords() {
        if (cursor.getPosition() != cursor.getCount() - 1) {
            cursor.moveToNext();
            word.setText(cursor.getString(1));
            translate.setText("");
        } else {
            translate.setVisibility(View.INVISIBLE);
            button.setVisibility(View.INVISIBLE);
            word.setText("Слова закончились!\nЧтобы провести тест\nвыучите новые слова" +
                    "\nили повторите невыученные!\n^_^");
        }
    }
}

