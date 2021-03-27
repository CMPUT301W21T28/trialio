package com.example.trialio.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.trialio.R;
import com.example.trialio.controllers.ChangeUsernameCommand;
import com.example.trialio.fragments.ChangeUsernameFragment;
import com.example.trialio.fragments.EditContactInfoFragment;
import com.example.trialio.models.User;
import com.example.trialio.controllers.UserManager;
import com.google.android.material.snackbar.Snackbar;

// Code referenced from Stack Overflow thread Android custom back button with text https://stackoverflow.com/questions/46242280/android-custom-back-button-with-text
// by user Nuovo 001, profile https://stackoverflow.com/users/8615244/nuovo-001
// in thread https://stackoverflow.com/questions/46242280/android-custom-back-button-with-text/46244113#46244113

/**
 * This activity allows a user to view their own profile and edit it to make changes to their username and contract info
 */
public class ViewUserActivity extends AppCompatActivity implements ChangeUsernameFragment.OnFragmentInteractionListener {
    private final String TAG = "ViewUserActivity";

    private User user;
    private Button editUserProfile;
    private Button changeUsername;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        // get the user that was passed in as an argument
        Bundle bundle = getIntent().getExtras();
        user = (User) bundle.getSerializable("user");

        editUserProfile = (Button) findViewById(R.id.editContactInfoButton);
        changeUsername = (Button) findViewById(R.id.changeUsernameButton);
        userManager = new UserManager();

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

    }

    @Override
    protected void onStart() {
        super.onStart();

        setUserDataListener();
    }

    private void setUserDataListener() {
        // use the userID to get the most recent user
        userManager.addUserUpdateListener(user.getUsername(), new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User newUser) {
                user = newUser;         // update the user
                setFields();            // set fields
                setVisibility();        // set visibility
                setOnClickListeners();  // set listeners
            }
        });
    }

    /**
     * This sets the fields of the textViews in ViewUserActivity using the user data.
     */
    public void setFields() {

        // get text views
        TextView userName = findViewById(R.id.usernameText);
        TextView userPhone = findViewById(R.id.phoneText);
        TextView UserEmail = findViewById(R.id.emailText);

        // set text views
        userName.setText(user.getUsername());
        userPhone.setText(user.getContactInfo().getPhone());
        UserEmail.setText(user.getContactInfo().getEmail());
    }

    /**
     * This sets the visibility of the edit button in the ViewUserActivity.
     */
    public void setVisibility() {

        // by default the editUserProfile button is invisible
        editUserProfile.setVisibility(View.INVISIBLE);
        changeUsername.setVisibility(View.INVISIBLE);

        // get current user
        userManager.getCurrentUser(new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User currentUser) {
                // compare current user with arg user. If same id, make the edit button visible
                if (user.getUsername().equals(currentUser.getUsername())) {
                    editUserProfile.setVisibility(View.VISIBLE);
                    changeUsername.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * This sets the onclick listeners of the buttons in ViewUserActivity.
     */
    public void setOnClickListeners() {

        // show a fragment to edit user profile
        editUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditContactInfoFragment editContactInfoFragment = new EditContactInfoFragment();

                Bundle args = new Bundle();
                args.putSerializable("UserProfile", user);
                editContactInfoFragment.setArguments(args);

                editContactInfoFragment.show(getSupportFragmentManager(), "editProfile");
            }
        });

        changeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeUsernameFragment changeUsernameFragment = new ChangeUsernameFragment();
                changeUsernameFragment.show(getSupportFragmentManager(), "changeUsername");
            }
        });
    }

    @Override
    public void onNewUsernameConfirm(String newUsername) {
        ChangeUsernameCommand command = new ChangeUsernameCommand(user, newUsername, isSuccess -> {
            /* devDeejay, https://stackoverflow.com/users/6145568/devdeejay,
             * "How to show Snackbar when Activity starts", 2017-08-17, CC BY-SA 4.0,
             * https://stackoverflow.com/a/45532564/15048024
             */
            if (isSuccess) {
                setUserDataListener();
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Your username has been changed", Snackbar.LENGTH_LONG);
                snackbar.show();
            } else {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Sorry, that username is unavailable", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
        command.execute();
    }
}