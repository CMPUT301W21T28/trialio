package com.example.trialio;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.User;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class UserManagerTest {

    String testCollection = "user-test";
    ArrayList<String> testUserIds;
    UserManager userManager;

    @Before
    void initUserManager() {
        FirebaseFirestore.getInstance().collection(testCollection).get();
        userManager = new UserManager(testCollection);
    }

    @Test
    public void createNewUser() {
        UserManager um = new UserManager("user-test");
        um.getCurrentUser(new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                assertEquals(User.class, user.getClass());

            }
        });
    }
}
