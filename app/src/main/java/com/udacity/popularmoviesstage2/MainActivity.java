package com.udacity.popularmoviesstage2;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements MainActivityFragment.MainFragmentListener {

    private static final String TAG_MAIN_FRAGMENT = "main_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //search if the main fragment exists
        FragmentManager fManager = getSupportFragmentManager();
        MainActivityFragment mainFragment = (MainActivityFragment) fManager.findFragmentByTag(TAG_MAIN_FRAGMENT);

        //create the main fragment if it does not exist
        if (mainFragment == null) {
            mainFragment = MainActivityFragment.newInstance();
            fManager.beginTransaction().add(
                    android.R.id.content, mainFragment, TAG_MAIN_FRAGMENT).commit();
        }
    }

    public void onError(String errorText) {
        Toast.makeText(this, errorText, Toast.LENGTH_LONG).show();
    }
}
