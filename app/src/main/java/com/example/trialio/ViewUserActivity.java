package com.example.trialio;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.trialio.ui.main.EditProfileFragment;

public class ViewUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        Intent intent = getIntent();
        //User user = intent.getSerializableExtra("CurrentUser");

        //setup back button functionality
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.custom_action_bar_layout, null);
        ImageButton backButton = (ImageButton) mCustomView.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        TextView userName = findViewById(R.id.usernameText);
        TextView userPhone = findViewById(R.id.phoneText);
        TextView UserEMail = findViewById(R.id.emailText);

        //userName.setText(user.getUsername());
        //userPhone.setText(user.getContactInfo().getPhone());
        //UserEMail.setText(user.getContactInfo().getEmail());

        final Button editUserProfile = (Button) findViewById(R.id.editButton);
        editUserProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new EditProfileFragment().show(getSupportFragmentManager(), "ADD_CITY");
                     }
                });

        //fragment sends info to userManager to change the information

        //onBackPressed() method to go back to main activity
    }
}