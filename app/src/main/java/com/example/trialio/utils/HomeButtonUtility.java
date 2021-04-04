package com.example.trialio.utils;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.example.trialio.activities.MainActivity;

public class HomeButtonUtility {

    /**
     * This sets the on click listener of a button to finish all activities and start the main
     * activity.
     * @param homeButton Button to set as a home button.
     */
    public void setHomeButtonListener(Button homeButton) {

        // get the activity context
        Context context = homeButton.getContext();

        // set the on click listener
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
        });
    }
}
