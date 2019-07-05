package com.example.learnenglish;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;


import static com.example.learnenglish.MainActivity.*;

public class LearnWords extends Activity implements View.OnClickListener {

    private TextView showWord;
    private TextView showTranslate;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_words);

        showWord = findViewById(R.id.word);
        showTranslate = findViewById(R.id.show_translate);
        TextView nextWord = findViewById(R.id.next_word);

        //��������� �� ������, ������� ����� ���� ����������� ��� ������ � ������
        //� ���� �������� �� ���������
        preferences = PreferenceManager.getDefaultSharedPreferences(LearnWords.this);

        //�� ��������� ��� �������� ����� �������� ����� ������������
        // ��������� ����������� �����
        showWord.setText(preferences.getString(
                "word", cursor.getString(1)));
        showTranslate.setText(preferences.getString(
                "translate", cursor.getString(2)));

        nextWord.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.next_word) {
            cursor.moveToNext();
            showWord.setText(cursor.getString(1));
            showTranslate.setText(cursor.getString(2));

            // ������ ������ Editor ��� ������ � ���� ��������
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("word", cursor.getString(1));
            editor.putString("translate", cursor.getString(2));
            //��������� ��� ����������� ����
            editor.apply();
        }
    }
}
