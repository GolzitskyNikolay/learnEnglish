package com.example.learnenglish;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class Settings extends Activity implements View.OnClickListener {

    private TextView textView;
    private SharedPreferences myPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        textView = findViewById(R.id.text_with_seekBar);
        findViewById(R.id.back_button_settings).setOnClickListener(this);

        SeekBar seekBar = findViewById(R.id.seek_bar);
        seekBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(getBaseContext(),
                R.color.darkBlue), PorterDuff.Mode.SRC_ATOP);

        myPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int count = myPreferences.getInt("count of words", 5);

        textView.setText(String.valueOf(count));
        seekBar.setProgress(count - 1);
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
                    SharedPreferences.Editor editor = myPreferences.edit();
                    editor.putInt("count of words", seekBar.getProgress() + 1);
                    editor.apply();
                }
            };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_button_settings) {
            finish();
        }
    }
}

