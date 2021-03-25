package com.example.trialio.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.trialio.R;
import com.example.trialio.models.User;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.fragments.EditContactInfoFragment;

import java.io.Serializable;

//Code referenced from Stack Overflow thread Android custom back button with text https://stackoverflow.com/questions/46242280/android-custom-back-button-with-text
//by user Nuovo 001, profile https://stackoverflow.com/users/8615244/nuovo-001
// in thread https://stackoverflow.com/questions/46242280/android-custom-back-button-with-text/46244113#46244113

/**
 * This activity allows a user to view their own profile and edit it to make changes to their username and contract info
 */

public class ViewUserActivity extends AppCompatActivity {
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);


        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("User");
        /*
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
        */
        TextView userName = findViewById(R.id.usernameText);
        TextView userPhone = findViewById(R.id.phoneText);
        TextView UserEMail = findViewById(R.id.emailText);

        UserManager manager = new UserManager();
        manager.getCurrentUser(new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                manager.addUserUpdateListener(user.getUsername(), new UserManager.OnUserFetchListener() {
                    @Override
                    public void onUserFetch(User user) {
                        userName.setText(user.getUsername());
                        userPhone.setText(user.getContactInfo().getPhone());
                        UserEMail.setText(user.getContactInfo().getEmail());
                    }
                });
            }
        });

        final Button editUserProfile = (Button) findViewById(R.id.editButton);
        editUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditContactInfoFragment editContactInfoFragment = new EditContactInfoFragment();
                Bundle args = new Bundle();
                args.putSerializable("UserProfile", (Serializable) user);
                editContactInfoFragment.setArguments(args);
                editContactInfoFragment.show(getSupportFragmentManager(), "editProfile");
            }
        });
    }
}