package com.example.learnenglish;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.widget.SeekBar;
import android.widget.TextView;

public class Settings extends Activity {

    private TextView textView;
    private SharedPreferences myPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        textView = findViewById(R.id.text_with_seekBar);

        SeekBar seekBar = findViewById(R.id.seek_bar);
        //меняем цвет полоски
        seekBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(getBaseContext(),
                R.color.darkBlue), PorterDuff.Mode.SRC_ATOP);

        //ссылаемся на объект, который может быть использован для чтения и записи
        //в файл настроек по умолчанию
        myPreferences = PreferenceManager.getDefaultSharedPreferences(Settings.this);

        int count = myPreferences.getInt("count of words", 1);
        textView.setText(String.valueOf(count + 1));
        seekBar.setProgress(count);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener =
            new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    textView.setText(String.valueOf(seekBar.getProgress() + 1));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // создаём объект Editor для записи в файл настроек
                    SharedPreferences.Editor editor = myPreferences.edit();
                    editor.putInt("count of words", seekBar.getProgress());
                    //сохраняем все добавленные пары
                    editor.apply();
                }
            };

}

