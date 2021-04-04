package com.example.trialio.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.trialio.R;
import com.example.trialio.controllers.ChangeUsernameCommand;
import com.example.trialio.controllers.CurrentUserHandler;
import com.example.trialio.fragments.ChangeUsernameFragment;
import com.example.trialio.fragments.EditContactInfoFragment;
import com.example.trialio.models.User;
import com.example.trialio.controllers.UserManager;
import com.google.android.material.snackbar.Snackbar;

/**
 * This activity allows a user to view their own profile and edit it to make changes to their
 * username and contract info.
 * <p>
 * This activity navigates to no other activities.
 */
public class ViewUserActivity extends AppCompatActivity implements ChangeUsernameFragment.OnFragmentInteractionListener, EditContactInfoFragment.OnFragmentInteractionListener {
    private final String TAG = "ViewUserActivity";

    /**
     * The user data to be displayed
     */
    private User user;

    /**
     * The button used to open dialog to edit contact info
     */
    private Button editContactInfo;

    /**
     * The button used to open dialog to change username
     */
    private Button changeUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        // get the user that was passed in as an argument
        Bundle bundle = getIntent().getExtras();
        user = (User) bundle.getSerializable("user");

        // find the important views
        editContactInfo = (Button) findViewById(R.id.editContactInfoButton);
        changeUsername = (Button) findViewById(R.id.changeUsernameButton);

        setUserDataListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void setUserDataListener() {
        // use the username to get the most recent user
        UserManager userManager = new UserManager();
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
     * Sets the fields of the textViews in ViewUserActivity using the user data.
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
     * Sets the visibility of the buttons in the ViewUserActivity.
     */
    public void setVisibility() {
        // by default the editUserProfile button is invisible
        // get current user and check if same as displayed user
        CurrentUserHandler.getInstance().getCurrentUser(new CurrentUserHandler.OnUserFetchCallback() {
            @Override
            public void onUserFetch(User u) {
                // compare current user with arg user. If same id, make the edit button visible
                if (user.getUsername().equals(u.getUsername())) {
                    editContactInfo.setVisibility(View.VISIBLE);
                    changeUsername.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    /**
     * Sets up the OnClick listeners for the Activity
     */
    public void setOnClickListeners() {
        // action to be performed when editContactInfo button is clicked
        editContactInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show a fragment to edit user contact information
                EditContactInfoFragment editContactInfoFragment = new EditContactInfoFragment();

                Bundle args = new Bundle();
                args.putSerializable("UserProfile", user);
                editContactInfoFragment.setArguments(args);

                editContactInfoFragment.show(getSupportFragmentManager(), "editContactInfo");
            }
        });

        // action to be performed when changeUsername button is clicked
        changeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show a fragment to enter a new username
                ChangeUsernameFragment changeUsernameFragment = new ChangeUsernameFragment();
                changeUsernameFragment.show(getSupportFragmentManager(), "changeUsername");
            }
        });
    }

    /**
     * Action to be performed when OK clicked on change username dialog
     *
     * @param requestedUsername the requested username input from the dialog
     */
    @Override
    public void onChangeUsernameConfirm(String requestedUsername) {
        ChangeUsernameCommand command = new ChangeUsernameCommand(user, requestedUsername, isSuccess -> {
            /* devDeejay, https://stackoverflow.com/users/6145568/devdeejay,
             * "How to show Snackbar when Activity starts", 2017-08-17, CC BY-SA 4.0,
             * https://stackoverflow.com/a/45532564/15048024
             */
            if (isSuccess) {
                setUserDataListener();
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Your username has been changed", Snackbar.LENGTH_SHORT);
                snackbar.show();
            } else {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Sorry, that username is unavailable", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });
        command.execute();
    }

    /**
     * Action to be performed when OK clicked on edit contact info dialog
     *
     * @param phone the phone number input from the dialog
     * @param email the email input from the dialog
     */
    @Override
    public void onEditContactInfoConfirm(String phone, String email) {
        user.getContactInfo().setPhone(phone);
        user.getContactInfo().setEmail(email);
        UserManager userManager = new UserManager();
        userManager.updateUser(user);
    }
}