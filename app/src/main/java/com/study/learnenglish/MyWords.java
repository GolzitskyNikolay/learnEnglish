package com.study.learnenglish;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

public class MyWords extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_words);

        Cursor cursor = new Database(this).getKindWords("yes");
        cursor.moveToFirst();

        // добавляем в изученные слова новые
        if (cursor.getCount() != 0) {
            TextView view = findViewById(R.id.all_my_words);
            StringBuilder newText = new StringBuilder();

            if (String.valueOf(view.getText()).equals("У вас нет выученных слов")) {
                view.setText("");
            }
            newText.append(view.getText());
            for (int i = 1; i <= cursor.getCount(); i++) {
                newText.append("\n");
                newText.append(cursor.getString(1));
                newText.append(" <--> ");
                newText.append(cursor.getString(2));
                cursor.moveToNext();
            }
            view.setText(newText);
        }
    }
}
