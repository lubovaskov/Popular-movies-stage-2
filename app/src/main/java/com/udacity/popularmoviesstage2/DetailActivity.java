package com.udacity.popularmoviesstage2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.udacity.popularmoviesstage2.valueobjects.MovieInfo;

public class DetailActivity extends AppCompatActivity
        implements DetailActivityFragment.DetailFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null) {
                MovieInfo movieInfo = intent.getParcelableExtra(MovieInfo.MOVIE_INFO_PARCELABLE_NAME);
                if (movieInfo != null) {
                    DetailActivityFragment detailFragment = DetailActivityFragment.newInstance(movieInfo);
                    getSupportFragmentManager().beginTransaction().add(
                            android.R.id.content, detailFragment).commit();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onError(String errorText) {
        Toast.makeText(this, errorText, Toast.LENGTH_LONG).show();
    }
}