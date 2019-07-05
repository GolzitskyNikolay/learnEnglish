package com.example.learnenglish;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

import java.io.*;
import java.util.*;

public class MainActivity extends Activity implements View.OnClickListener {

    static Database database;
    static Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity);

        findViewById(R.id.learn_words).setOnClickListener(this);
        findViewById(R.id.test).setOnClickListener(this);
        findViewById(R.id.my_words).setOnClickListener(this);
        findViewById(R.id.dictionary).setOnClickListener(this);
        findViewById(R.id.settings).setOnClickListener(this);

        // создаём базу данных, которая хранит в себе все слова, их перевод и флаги,
        // которые показывают выучены слова или нет
        database = new Database(this);

        // если база данных пустая, то заполняем её
        if (database.getAllWords().getCount() == 0) {
            try {
                addInDataBase(database);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cursor = database.getAllWords();
        cursor.moveToFirst();
    }

    /**
     * Данный метод вызывается только при первом запуске приложения.
     * Для более быстрой работы приложения создаётся база данных, которая
     * сохраняет в себя слова с переводом из исходного файла со словами "dictionary".
     */
    void addInDataBase(Database database) throws IOException {

        InputStream fileInputStream =
                this.getResources().openRawResource(R.raw.dictionary);
        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(fileInputStream));

        String line = bufferedReader.readLine();

        while (line != null) {
            List<String> list = new ArrayList<>(Arrays.asList(line.split("<>")));
            database.addWord(list.get(0), list.get(1), false);
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.learn_words) {
            startActivity(new Intent(this, LearnWords.class));
        } else if (v.getId() == R.id.test) {
            startActivity(new Intent(this, Test.class));
        } else if (v.getId() == R.id.my_words) {
            startActivity(new Intent(this, MyWords.class));
        } else if (v.getId() == R.id.dictionary) {
            startActivity(new Intent(this, Dictionary.class));
        } else if (v.getId() == R.id.settings) {
            startActivity(new Intent(this, Settings.class));
        }
    }
}
