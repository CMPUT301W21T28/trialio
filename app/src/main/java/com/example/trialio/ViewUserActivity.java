package com.example.trialio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class ViewUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        Intent intent = getIntent();

        //display user information

        //edit button to open fragment

        //fragment sends info to userManager to change the information

        //onBackPressed() method to go back to main activity
    }
}