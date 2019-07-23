package com.example.learnenglish;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MyWords extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_words);

        Cursor cursor = new Database(this).getKindOfWords("yes");
        cursor.moveToFirst();

        if (cursor.getCount() != 0) {
            TextView view = findViewById(R.id.all_my_words);
            StringBuilder newText = new StringBuilder();

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
        findViewById(R.id.back_button_myWords).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_button_myWords){
            finish();
        }
    }
}