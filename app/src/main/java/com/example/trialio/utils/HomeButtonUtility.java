package com.example.trialio.utils;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

import com.example.trialio.activities.MainActivity;

public final class HomeButtonUtility {

    // private constructor so people do not make instances
    private HomeButtonUtility() { }

    /**
     * This sets the on click listener of a button to finish all activities and start the main
     * activity.
     * @param homeButton Button to set as a home button.
     */
    public static void setHomeButtonListener(ImageButton homeButton) {

        // get the activity context
        Context context = homeButton.getContext();

        // set the on click listener
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Took code to finish all activities.
                // DATE: 	2011-06-13
                // LICENSE:	CC BY-SA 3.0 [https://creativecommons.org/licenses/by-sa/3.0/]
                // SOURCE: 	Finish all previous activities [https://stackoverflow.com/questions/6330260/finish-all-previous-activities]
                // AUTHOR: 	DArkO [https://stackoverflow.com/users/448192/darko]
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
        });
    }
}
