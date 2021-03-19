package com.example.trialio.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.trialio.R;
import com.example.trialio.fragments.BinomialTrialFragment;
import com.example.trialio.fragments.CountTrialFragment;
import com.example.trialio.models.BinomialTrial;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.fragments.EditProfileFragment;

import java.io.Serializable;

//Code referenced from Stack Overflow thread Android custom back button with text https://stackoverflow.com/questions/46242280/android-custom-back-button-with-text
//by user Nuovo 001, profile https://stackoverflow.com/users/8615244/nuovo-001
// in thread https://stackoverflow.com/questions/46242280/android-custom-back-button-with-text/46244113#46244113

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
        manager.addCurrentUserUpdateListener(new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                userName.setText(user.getUsername());
                userPhone.setText(user.getContactInfo().getPhone());
                UserEMail.setText(user.getContactInfo().getEmail());
            }
        });

        final Button editUserProfile = (Button) findViewById(R.id.editButton);
        editUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProfileFragment editProfileFragment = new EditProfileFragment();
                Bundle args = new Bundle();
                args.putSerializable("UserProfile", (Serializable) user);
                editProfileFragment.setArguments(args);
                editProfileFragment.show(getSupportFragmentManager(), "editProfile");
            }
            });
    }
}