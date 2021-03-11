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

//Code referenced from Stack Overflow thread Android custom back button with text https://stackoverflow.com/questions/46242280/android-custom-back-button-with-text
//by user Nuovo 001, profile https://stackoverflow.com/users/8615244/nuovo-001
// in thread https://stackoverflow.com/questions/46242280/android-custom-back-button-with-text/46244113#46244113

public class ViewUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        Intent intent = getIntent();

        //setup back button functionality
        ActionBar customActionBar = getSupportActionBar();
        customActionBar.setDisplayShowHomeEnabled(false);
        customActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater customizedInflater = LayoutInflater.from(this);

        View mCustomView = customizedInflater.inflate(R.layout.custom_action_bar_layout, null);
        ImageButton backButton = (ImageButton) mCustomView.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        customActionBar.setCustomView(mCustomView);
        customActionBar.setDisplayShowCustomEnabled(true);

        TextView userName = findViewById(R.id.usernameText);
        TextView userPhone = findViewById(R.id.phoneText);
        TextView UserEMail = findViewById(R.id.emailText);

        UserManager manager = new UserManager();
        manager.addCurrentUserUpdateListener(new UserManager.OnUserUpdateListener() {
            @Override
            public void onUserUpdate(User user) {
                userName.setText(user.getUsername());
                userPhone.setText(user.getContactInfo().getPhone());
                UserEMail.setText(user.getContactInfo().getEmail());
            }
        });

        final Button editUserProfile = (Button) findViewById(R.id.editButton);
        editUserProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new EditProfileFragment().show(getSupportFragmentManager(), "EDIT_USER");
                     }
                });

        //fragment sends info to userManager to change the information

        //onBackPressed() method to go back to main activity
    }
}