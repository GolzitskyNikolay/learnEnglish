package com.example.learnenglish;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

public class Dictionary extends Activity implements View.OnClickListener {

    EditText word;
    TextView translate;
    Animation animation;
    Database database;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        database = new Database(this);
        word = findViewById(R.id.word_dictionary);
        translate = findViewById(R.id.translate_dictionary);
        animation = AnimationUtils.loadAnimation(this, R.anim.anim_incorrect_enter);

        findViewById(R.id.button_dictionary).setOnClickListener(this);
        findViewById(R.id.back_button_dictionary).setOnClickListener(this);
        word.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                translate.setText(" ");
            }

            @Override
            public void afterTextChanged(Editable s) {
                translate.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_dictionary) {

            translate.setTextColor(getResources().getColor(R.color.darkBlue));

            String string = String.valueOf(word.getText());

            //ищем перевод слова в базе данных, если оно введено корректно
            if (string.toLowerCase().matches(" *[a-z]+([ |\\-][a-z]+)*")) {
                cursor = database.getRusByEng(string);
                showTranslate();
            } else if (string.toLowerCase().matches(" *[а-я|ё]+([ |\\-][а-я|ё]+)*")) {
                cursor = database.getEngByRus(string);
                showTranslate();
            } else {
                if (string.matches(" *")) {
                    translate.setText("Введите слово!");
                } else {
                    translate.setText("Слово введено\n  некорректно!");
                }
                // подключаем файл анимации
                translate.startAnimation(animation);
                translate.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        } else if (v.getId() == R.id.back_button_dictionary){
            finish();
        }
    }

    void showTranslate() {
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            StringBuilder showTranslate = new StringBuilder();
            while (cursor.getPosition() < cursor.getCount()) {
                showTranslate.append(cursor.getString(2));
                showTranslate.append("; ");
                cursor.moveToNext();
            }
            translate.setText(showTranslate);
        } else {
            translate.setText("Такого слова\nв словаре нет!");
        }
    }
}
