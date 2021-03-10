package com.example.trialio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

        TextView userName = findViewById(R.id.usernameText);
        TextView userPhone = findViewById(R.id.phoneText);
        TextView UserEMail = findViewById(R.id.emailText);

        //userName.setText(user.getUsername());
        //userPhone.setText(user.getContactInfo().getPhone());
        //UserEMail.setText(user.getContactInfo().getEmail());

        final ImageButton editUserProfile = (ImageButton) findViewById(R.id.bckBtn);
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