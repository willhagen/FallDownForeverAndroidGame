package com.example.falldownforever;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Will Hagen on 3/1/16.
 */
public class MainActivity extends Activity {

    FallDownForeverGame fallDownForeverGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fallDownForeverGame = new FallDownForeverGame(this);
        setContentView(fallDownForeverGame);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fallDownForeverGame.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fallDownForeverGame.pause();
    }
}
