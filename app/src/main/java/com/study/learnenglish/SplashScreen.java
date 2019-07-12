package com.study.learnenglish;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // экран загрузки будет показываться столько, сколько
        // необходимо времени для загрузки, после чего откроется главное активити
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
