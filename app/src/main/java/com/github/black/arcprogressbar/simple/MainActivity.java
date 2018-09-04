package com.github.black.arcprogressbar.simple;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.black.arcprogressbar.ArcProgressBar;


public class MainActivity extends AppCompatActivity {

	ArcProgressBar apb_main;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		apb_main = findViewById(R.id.apb_main);
	}
}
