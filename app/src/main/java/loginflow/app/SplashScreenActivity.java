package loginflow.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import loginflow.app.database.DatabaseHelper;

public class SplashScreenActivity extends AppCompatActivity {

    private long displayTimeStart;
    private long displayTime = 6000;
    private final Handler countDownTimer = new Handler();
    private final Runnable nextActivityRunner;
    private boolean isSplashFinished = false;
    private final static String TIME_LEFT_KEY = "timeLeft";

    public SplashScreenActivity() {
        nextActivityRunner = new Runnable() {
            @Override
            public void run() {
                isSplashFinished = true;
                Intent homeScreenIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                SplashScreenActivity.this.startActivity(homeScreenIntent);
                SplashScreenActivity.this.finish();
            }
        };
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        if (isSplashFinished) {
            Intent homeScreenIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
            SplashScreenActivity.this.startActivity(homeScreenIntent);
            SplashScreenActivity.this.finish();
        }
        if (savedInstanceState != null) {
            displayTime = savedInstanceState.getLong(TIME_LEFT_KEY);
        }

        displayWelcomeText();


        displayTimeStart = System.currentTimeMillis();
        countDownTimer.postDelayed(nextActivityRunner, displayTime);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        long elapsedTime = System.currentTimeMillis() - displayTimeStart;
        outState.putLong(TIME_LEFT_KEY, displayTime - elapsedTime);
    }

    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.removeCallbacks(nextActivityRunner);
    }

    private void displayWelcomeText() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        TextView bntHeaderText = this.findViewById(R.id.splash_screen_activity_txtView_welcome_header);

        if (databaseHelper.isUserIsLoggedIn()) {

            Cursor userData = databaseHelper.getUserData();
            userData.moveToFirst();
            String displayText = this.getResources().getString(R.string.welcome_info_logged_in, databaseHelper.getEmail());
            bntHeaderText.setText(displayText);

        } else {

            bntHeaderText.setText(this.getResources().getString(R.string.welcome_info_default));

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent homeScreenIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
        SplashScreenActivity.this.startActivity(homeScreenIntent);
        SplashScreenActivity.this.finish();
    }
}
